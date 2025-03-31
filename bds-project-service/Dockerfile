# 使用 JDK 17 基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制打包好的 JAR 文件
COPY target/bds-project-service-1.0-SNAPSHOT.jar app.jar

# 暴露应用端口
EXPOSE 3451

# 运行应用，添加 Nacos 参数
ENTRYPOINT ["java", "-jar", "app.jar", "--NACOS_USERNAME=nacos", "--NACOS_PASSWORD=jntff526"]