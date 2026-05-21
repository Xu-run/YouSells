FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY yousells-server/target/yousells-server-*.jar app.jar

ENV TZ=Asia/Shanghai

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
