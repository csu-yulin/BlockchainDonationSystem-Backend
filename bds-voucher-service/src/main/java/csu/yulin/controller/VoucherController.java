package csu.yulin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import csu.yulin.common.CommonResponse;
import csu.yulin.common.PageDTO;
import csu.yulin.enums.ResultCode;
import csu.yulin.exception.BusinessException;
import csu.yulin.feign.BlockchainServiceFeignClient;
import csu.yulin.model.convert.VoucherConverter;
import csu.yulin.model.dto.CharityDonationUploadVoucherInputDTO;
import csu.yulin.model.dto.VoucherDTO;
import csu.yulin.model.entity.Voucher;
import csu.yulin.model.vo.VoucherResponseVO;
import csu.yulin.service.IVoucherService;
import csu.yulin.util.AssertUtil;
import csu.yulin.util.IPFSUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 凭证记录表Controller
 *
 * @author lp
 * @create 2025-03-27
 */
@Slf4j
@RestController
@RequestMapping("/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final IVoucherService voucherService;

    private final IPFSUploader ipfsUploader;

    private final BlockchainServiceFeignClient charityDonationService;

    /**
     * 创建凭证
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<VoucherDTO> createVoucher(@RequestPart("file") MultipartFile file,
                                                    @RequestParam("projectId") Long projectId,
                                                    @RequestParam("orgId") Long orgId) throws IOException {
        // 参数校验
        AssertUtil.notNull(file, ResultCode.BAD_REQUEST, "上传文件不能为空");
        AssertUtil.notNull(projectId, ResultCode.BAD_REQUEST, "项目ID不能为空");
        AssertUtil.notNull(orgId, ResultCode.BAD_REQUEST, "组织ID不能为空");

        try {
            // 将文件保存到本地临时目录
            File tempFile = File.createTempFile(Objects.requireNonNull(file.getOriginalFilename()), null);
            file.transferTo(tempFile);

            // 调用 IPFSUploader 上传并 Pin 文件
            String cid = ipfsUploader.uploadFileAndPin(tempFile);
            // 删除临时文件
            tempFile.delete();

            // 将 IPFS CID 转换为 bytes32（链上需要）
            byte[] ipfsHash = DigestUtils.sha256(cid);

            // 构造链上请求 DTO
            CharityDonationUploadVoucherInputDTO inputDTO = new CharityDonationUploadVoucherInputDTO();
            inputDTO.setProjectId(BigInteger.valueOf(projectId));
            inputDTO.setOrgId(BigInteger.valueOf(orgId));
            inputDTO.setIpfsHash(ipfsHash);

            // 调用区块链服务上传凭证
            CommonResponse<VoucherResponseVO> response = charityDonationService.uploadVoucher(inputDTO);
            AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应为空");
            AssertUtil.isTrue(response.getCode().equals(ResultCode.SUCCESS.getCode()), ResultCode.INTERNAL_SERVER_ERROR,
                    "区块链凭证上传失败: " + response.getMessage());
            VoucherResponseVO responseVO = response.getData();
            AssertUtil.notNull(responseVO, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应数据为空");

            // 调试原始时间戳
            log.info("链上时间戳: {}", responseVO.getTimestamp());

            // 检查时间戳是否合理（假设是以秒为单位）
            LocalDateTime timestamp = responseVO.getTimestamp();
            LocalDateTime minDateTime = LocalDateTime.of(1000, 1, 1, 0, 0, 0);
            LocalDateTime maxDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
            if (timestamp.isBefore(minDateTime) || timestamp.isAfter(maxDateTime)) {
                log.warn("链上时间戳超出 MySQL DATETIME 范围: {}", timestamp);
                timestamp = LocalDateTime.now();
            }

            // 构造 Voucher 实体
            Voucher voucher = new Voucher();
            voucher.setVoucherId(responseVO.getVoucherId().longValue());
            voucher.setProjectId(projectId);
            voucher.setOrgId(orgId);
            // 存储原始 CID
            voucher.setIpfsHash(cid);
            // 使用修正后的时间戳
            voucher.setTimestamp(timestamp);
            voucher.setTransactionHash(responseVO.getTransactionHash());
            voucher.setFileUrl("https://ipfs.io/ipfs/" + cid);

            // 保存到数据库
            boolean success = voucherService.save(voucher);
            AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "凭证记录保存失败");

            // 转换返回 DTO
            VoucherDTO voucherDTO = VoucherConverter.toDTO(voucher);

            log.info("凭证创建成功: voucherId={}, projectId={}, orgId={}, ipfsHash={}, txHash={}",
                    voucherDTO.getVoucherId(), voucherDTO.getProjectId(), voucherDTO.getOrgId(),
                    voucherDTO.getIpfsHash(), voucherDTO.getTransactionHash());

            return CommonResponse.success("凭证创建成功", voucherDTO);
        } catch (Exception e) {

            log.error("凭证创建失败: projectId={}, orgId={}, error={}", projectId, orgId, e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "凭证创建失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询单个凭证
     */
    @GetMapping("/{voucherId}")
    public CommonResponse<VoucherDTO> getVoucherById(@PathVariable("voucherId") Long voucherId) {
        // 参数校验
        AssertUtil.notNull(voucherId, ResultCode.BAD_REQUEST, "凭证ID不能为空");

        try {
            // 通过 voucherService 查询凭证
            Voucher voucher = voucherService.getById(voucherId);
            AssertUtil.notNull(voucher, ResultCode.NOT_FOUND, "凭证不存在");

            // 转换返回 DTO
            VoucherDTO voucherDTO = VoucherConverter.toDTO(voucher);

            log.info("查询凭证成功: voucherId={}, projectId={}, orgId={}, ipfsHash={}, txHash={}",
                    voucherDTO.getVoucherId(), voucherDTO.getProjectId(), voucherDTO.getOrgId(),
                    voucherDTO.getIpfsHash(), voucherDTO.getTransactionHash());

            return CommonResponse.success("查询凭证成功", voucherDTO);
        } catch (Exception e) {
            log.error("查询凭证失败: voucherId={}, error={}", voucherId, e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "查询凭证失败: " + e.getMessage());
        }
    }

    /**
     * 查询凭证列表（支持分页和过滤）
     */
    @PostMapping("/list")
    public CommonResponse<IPage<VoucherDTO>> listVouchers(@RequestBody PageDTO pageDTO) {
        // 参数校验
        AssertUtil.notNull(pageDTO, ResultCode.BAD_REQUEST, "分页参数不能为空");
        AssertUtil.isTrue(pageDTO.getPage() > 0, ResultCode.BAD_REQUEST, "页码必须大于0");
        AssertUtil.isTrue(pageDTO.getSize() > 0 && pageDTO.getSize() <= 100, ResultCode.BAD_REQUEST,
                "每页大小必须在1-100之间");

        try {
            // 调用服务层查询分页数据
            IPage<VoucherDTO> voucherPage = voucherService.listVouchers(pageDTO);

            log.info("查询凭证列表成功: projectId={}, orgId={}, page={}, size={}, total={}",
                    pageDTO.getProjectId(), pageDTO.getOrgId(), pageDTO.getPage(), pageDTO.getSize(), voucherPage.getTotal());

            return CommonResponse.success("查询凭证列表成功", voucherPage);
        } catch (Exception e) {
            log.error("查询凭证列表失败: projectId={}, orgId={}, page={}, size={}, error={}",
                    pageDTO.getProjectId(), pageDTO.getOrgId(), pageDTO.getPage(), pageDTO.getSize(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "查询凭证列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据IPFS哈希查询凭证
     */
    @GetMapping("/by-ipfs")
    public CommonResponse<VoucherDTO> getVoucherByIpfsHash(@RequestParam("ipfsHash") String ipfsHash) {
        // 参数校验
        AssertUtil.notBlank(ipfsHash, ResultCode.BAD_REQUEST, "IPFS哈希不能为空");

        try {
            // 调用服务层查询凭证
            VoucherDTO voucherDTO = voucherService.getVoucherByIpfsHash(ipfsHash);
            log.info("查询凭证成功: ipfsHash={}, voucherId={}, projectId={}, orgId={}",
                    ipfsHash, voucherDTO.getVoucherId(), voucherDTO.getProjectId(), voucherDTO.getOrgId());

            return CommonResponse.success("查询凭证成功", voucherDTO);
        } catch (Exception e) {
            log.error("查询凭证失败: ipfsHash={}, error={}", ipfsHash, e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "查询凭证失败: " + e.getMessage());
        }
    }

    /**
     * 根据项目ID查询凭证列表
     */
    @GetMapping("/by-project/{projectId}")
    public CommonResponse<List<VoucherDTO>> getVouchersByProjectId(@PathVariable("projectId") Long projectId) {
        // 参数校验
        AssertUtil.notNull(projectId, ResultCode.BAD_REQUEST, "项目ID不能为空");

        try {
            // 查询凭证列表
            List<Voucher> vouchers = voucherService.list(
                    new LambdaQueryWrapper<Voucher>()
                            .eq(Voucher::getProjectId, projectId)
            );

            AssertUtil.notEmpty(vouchers, ResultCode.NOT_FOUND, "暂无相关凭证");

            // 转换为 DTO 列表
            List<VoucherDTO> voucherDTOList = vouchers.stream()
                    .map(VoucherConverter::toDTO)
                    .collect(Collectors.toList());

            log.info("查询凭证成功: projectId={}, 共{}条", projectId, voucherDTOList.size());

            return CommonResponse.success("查询凭证成功", voucherDTOList);
        } catch (Exception e) {
            log.error("查询凭证失败: projectId={}, error={}", projectId, e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "查询凭证失败: " + e.getMessage());
        }
    }

}