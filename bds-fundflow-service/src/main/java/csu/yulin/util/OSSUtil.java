package csu.yulin.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;

/**
 * 头像操作工具类
 *
 * @author lp
 * @create 2025-01-04
 */
@Slf4j
@Component
public class OSSUtil {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.directory}")
    private String directory;

    private OSS ossClient;

    /**
     * 初始化 OSS 客户端
     */
    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("OSS客户端初始化成功");
    }

    /**
     * 销毁 OSS 客户端
     */
    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS客户端已关闭");
        }
    }

    /**
     * 上传文件到阿里云 OSS
     *
     * @param fileName    文件名（包含路径，如 avatar/123.jpg）
     * @param inputStream 文件输入流
     * @return 文件的 OSS URL
     * @throws OSSException 如果上传失败
     */
    public String uploadFile(String fileName, InputStream inputStream) throws OSSException {
        try {
            // 构造 OSS 存储路径
            String objectName = directory + "/" + fileName;

            // 上传文件
            PutObjectResult result = ossClient.putObject(bucketName, objectName, inputStream);
            log.info("文件上传成功: bucketName={}, objectName={}", bucketName, objectName);

            // 构造文件访问 URL
            return String.format("https://%s.%s/%s", bucketName, endpoint, objectName);
        } catch (OSSException e) {
            log.error("文件上传失败: fileName={}, error={}", fileName, e.getMessage());
            throw e;
        }
    }

    /**
     * 删除 OSS 中的文件
     *
     * @param fileName 文件名（包含路径，如 avatar/123.jpg）
     */
    public void deleteFile(String fileName) {
        try {
            // 构造 OSS 存储路径
            String objectName = directory + "/" + fileName;

            // 删除文件
            ossClient.deleteObject(bucketName, objectName);
            log.info("文件删除成功: bucketName={}, objectName={}", bucketName, objectName);
        } catch (OSSException e) {
            log.warn("文件删除失败或文件不存在: fileName={}, error={}", fileName, e.getMessage());
        }
    }

    /**
     * 从文件 URL 提取文件名
     *
     * @param fileUrl 文件 URL
     * @return 文件名（包含路径，如 avatar/123.jpg）
     */
    public String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        return fileUrl.substring(fileUrl.indexOf(directory));
    }
}