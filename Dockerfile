#
# Build
#
FROM maven:3.9.5-amazoncorretto-17-al2023@sha256:eeaa7ab572d931f7273fc5cf31429923f172091ae388969e11f42ec6dd817d74 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -U -Dmaven.test.skip=true

#
# Package stage
#
FROM maven:3.9.5-amazoncorretto-17-al2023@sha256:eeaa7ab572d931f7273fc5cf31429923f172091ae388969e11f42ec6dd817d74 AS builder
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
RUN java -Djarmode=builder -jar app.jar extract


FROM ghcr.io/pagopa/docker-base-springboot-openjdk17:v2.2.0@sha256:b866656c31f2c6ebe6e78b9437ce930d6c94c0b4bfc8e9ecc1076a780b9dfb18
WORKDIR /app
COPY --chown=spring:spring   --from=builder /app/dependencies/ ./dependencies/
COPY --chown=spring:spring   --from=builder /app/snapshot-dependencies/ ./snapshot-dependencies/
COPY --chown=spring:spring   --from=builder /app/spring-boot-loader/ ./spring-boot-loader/
COPY --chown=spring:spring   --from=builder /app/application/ ./application/

COPY --chown=spring:spring  --from=builder dependencies/ ./
COPY --chown=spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --chown=spring:spring  --from=builder spring-boot-loader/ ./
COPY --chown=spring:spring  --from=builder application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]