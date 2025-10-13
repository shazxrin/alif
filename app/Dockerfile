# Builder container
FROM bellsoft/liberica-runtime-container:jdk-25-glibc AS builder
WORKDIR /builder

# Copy the jar file to the working directory
COPY build/libs/app-*.jar app.jar

# Extract the jar file as layers
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# Runtime container
FROM bellsoft/liberica-runtime-container:jre-25-glibc
WORKDIR /application

# Copy the extracted jar contents from the builder container into the working directory in the runtime container
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# Execute the AOT training run and create the AOT cache
RUN java -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -Dspring.profiles.active=build -jar app.jar

ENTRYPOINT [ \
    "java", \
    "-XX:AOTCacheOutput=app.aot", \
    "-jar", "app.jar" \
]
