package csu.yulin.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * IO 工具类
 * <p>提供文件和流的读写、拷贝、删除等操作</p>
 *
 * @author lp
 * @create 2025-03-26
 */
@Slf4j
public class IOUtil {
    private static final int BUF_SIZE = 2048;

    private IOUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 读取文件内容为字符串
     */
    public static String readAsString(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return readAsString(in);
        }
    }

    /**
     * 读取资源文件内容为字符串
     */
    public static String readResourceAsString(String resource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(resource)) {
            if (in == null) {
                log.warn("资源文件 {} 未找到", resource);
                return null;
            }
            return readAsString(in);
        } catch (IOException ex) {
            log.error("读取资源文件 {} 失败", resource, ex);
            return null;
        }
    }

    /**
     * 从输入流读取内容为字符串
     */
    public static String readAsString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(inputStream, baos);
        return baos.toString(StandardCharsets.UTF_8);
    }

    /**
     * 将字符串写入文件
     */
    public static void writeString(File target, String content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("写入内容不能为空");
        }
        try (InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
             OutputStream fos = new FileOutputStream(target, false)) {
            copy(in, fos);
        }
    }

    /**
     * 复制文件夹及其内容
     */
    public static void copyFolder(File srcDir, File destDir) throws IOException {
        if (!srcDir.exists()) {
            throw new IOException("源目录不存在: " + srcDir.getAbsolutePath());
        }
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new IOException("无法创建目标目录: " + destDir.getAbsolutePath());
        }

        for (File file : Objects.requireNonNull(srcDir.listFiles(), "无法列出源目录文件")) {
            File fileCopyTo = new File(destDir, file.getName());
            if (file.isDirectory()) {
                copyFolder(file, fileCopyTo);
            } else {
                copyFile(file, fileCopyTo);
            }
        }
    }

    /**
     * 复制文件
     */
    public static void copyFile(File src, File tgt) throws IOException {
        if (!src.exists()) {
            throw new IOException("源文件不存在: " + src.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(tgt, false)) {
            copy(fis, fos);
        }
    }

    /**
     * 将输入流的数据复制到输出流
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        if (is == null || os == null) {
            throw new IllegalArgumentException("输入流或输出流不能为空");
        }
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            byte[] buf = new byte[BUF_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buf)) != -1) {
                bos.write(buf, 0, bytesRead);
            }
            bos.flush();
        }
    }

    /**
     * 删除文件或文件夹
     */
    public static void removeItem(File item) {
        if (item == null || !item.exists()) {
            log.warn("文件/文件夹 {} 不存在，跳过删除", item);
            return;
        }

        if (item.isDirectory()) {
            File[] subFiles = item.listFiles();
            if (subFiles != null) {
                for (File subItem : subFiles) {
                    removeItem(subItem);
                }
            }
        }

        if (!item.delete()) {
            log.error("删除文件/文件夹失败: {}", item.getAbsolutePath());
        }
    }
}
