FROM public.ecr.aws/docker/library/maven:3.9.5-amazoncorretto-21-al2023 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-headless
WORKDIR /app
RUN dnf install -y shadow-utils && \
    useradd -ms /bin/bash springuser && \
    dnf clean all
USER springuser
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]