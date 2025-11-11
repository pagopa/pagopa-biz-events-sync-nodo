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
FROM amazoncorretto:17.0.9-al2023@sha256:638da7295a278de15f3ef1ee672aae19d40e49438c7b4164f0bb3bd0dcd215a5 AS layertools
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract


#
# Final runtime stage
#
FROM ghcr.io/pagopa/docker-base-springboot-openjdk17:v2.2.0@sha256:b866656c31f2c6ebe6e78b9437ce930d6c94c0b4bfc8e9ecc1076a780b9dfb18
WORKDIR /app

COPY --chown=spring:spring --from=layertools /app/dependencies/ ./dependencies/
COPY --chown=spring:spring --from=layertools /app/snapshot-dependencies/ ./snapshot-dependencies/
COPY --chown=spring:spring --from=layertools /app/spring-boot-loader/ ./spring-boot-loader/
COPY --chown=spring:spring --from=layertools /app/application/ ./

EXPOSE 8080
USER spring
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]