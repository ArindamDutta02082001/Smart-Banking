FROM amazoncorretto:17
LABEL maintainer="arindam"
WORKDIR /app
COPY target/account-api-1.0.jar /app/account-api-1.0.jar
EXPOSE 8001
ENTRYPOINT ["java",  "-jar" , "/app/account-api-1.0.jar"]

# docker build -t acc:1 .
# docker run -d --name account-api --network my-network -p 8001:8001 acc:1