package csu.yulin.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
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
     * 上传头像
     *
     * @param fileName    文件名
     * @param inputStream 文件流
     * @return 上传后的文件 URL
     */
    public String uploadAvatar(String fileName, InputStream inputStream) {
        try {
            // 拼接文件路径
            String objectName = directory + "/" + fileName;

            // 上传文件
            ossClient.putObject(bucketName, objectName, inputStream);

            // 返回文件的访问 URL
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } catch (Exception e) {
            log.error("上传头像失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传头像失败", e);
        }
    }

    /**
     * 删除头像
     *
     * @param fileName 文件名
     */
    public void deleteAvatar(String fileName) {
        try {
            // 拼接文件路径
            String objectName = directory + "/" + fileName;

            // 删除文件
            ossClient.deleteObject(bucketName, objectName);
            log.info("删除头像成功: {}", objectName);
        } catch (Exception e) {
            log.error("删除头像失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除头像失败", e);
        }
    }
}
