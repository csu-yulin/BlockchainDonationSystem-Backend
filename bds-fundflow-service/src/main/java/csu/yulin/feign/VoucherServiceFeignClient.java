package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.model.dto.VoucherDTO;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-voucher-service", configuration = VoucherServiceFeignClient.Configuration.class)
public interface VoucherServiceFeignClient {

    @PostMapping(value = "/voucher/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CommonResponse<VoucherDTO> createVoucher(@RequestPart("file") MultipartFile file,
                                             @RequestParam("projectId") Long projectId,
                                             @RequestParam("orgId") Long orgId);

    // Custom configuration for this client
    class Configuration {
        @Bean
        public Encoder formEncoder() {
            return new SpringFormEncoder();
        }
    }
}
