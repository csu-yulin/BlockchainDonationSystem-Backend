package csu.yulin.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class IPFSUploader {

    private static final String IPFS_API_URL = "http://118.145.177.151:5001/api/v0";
    // 10MB in bytes
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final OkHttpClient client;

    public IPFSUploader() {
        this.client = new OkHttpClient();
    }

    // 上传文件到IPFS
    public String uploadFileAndPin(File file) throws IOException {
        // 校验文件大小
        if (file.length() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum limit of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }

        // Step 1: 上传文件到IPFS
        String cid = uploadFileToIPFS(file);

        // Step 2: Pin文件
        pinFile(cid);
        // 返回文件的CID
        return cid;
    }

    // 上传文件到IPFS
    private String uploadFileToIPFS(File file) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(IPFS_API_URL + "/add")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 解析响应，获取CID
                String responseBody = null;
                if (response.body() != null) {
                    responseBody = response.body().string();
                }
                String cid = null;
                if (responseBody != null) {
                    cid = parseCIDFromResponse(responseBody);
                }
                log.info("File uploaded successfully. CID: {}", cid);
                return cid;
            } else {
                log.error("Failed to upload file to IPFS. Response: {}", response.message());
                throw new IOException("Failed to upload file to IPFS: " + response.message());
            }
        }
    }

    // Pin文件
    private void pinFile(String cid) throws IOException {
        Request request = new Request.Builder()
                .url(IPFS_API_URL + "/pin/add?arg=" + cid)
                .post(RequestBody.create(new byte[0], MediaType.parse("application/octet-stream")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                log.info("File pinned successfully. CID: {}", cid);
            } else {
                log.error("Failed to pin file. Response: {}", response.message());
                throw new IOException("Failed to pin file: " + response.message());
            }
        }
    }

    // 解析CID从响应
    private String parseCIDFromResponse(String responseBody) {
        // 假设返回的响应是JSON格式
        // {"Name":"test.png", "Hash":"QmX..."}
        int start = responseBody.indexOf("\"Hash\":\"") + 8;
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }
}