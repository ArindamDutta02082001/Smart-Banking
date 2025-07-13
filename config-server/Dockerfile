FROM amazoncorretto:17
LABEL maintainer="arindam"
WORKDIR /app
COPY target/config-server-1.0.jar /app/config-server-1.0.jar

# Copy config files into the container (optional: use volume in docker run instead)
# it will not work since Docker dont have permission to go outside its directory i.e cannot use ../
# so the only way to copy is to bring the config files inside
# I a using volume mounting

#royal-reserve-bank/
#├── config-server/
#│   ├── Dockerfile
#│   ├── config-files/
#│   └── target/

#COPY ../config-files /app/config-files


# Tell Spring where to find the external config files
ENV SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS=file:/app/config-files

EXPOSE 8888
ENTRYPOINT ["java",  "-jar" , "/app/config-server-1.0.jar"]

# docker build -t conf:1 .
# docker run -d -p 8888:8888 --network my-network --name conf -v "E:\Java_Backend_Git\royal-reserve-bank\config-files:/app/config-files" -t conf:1