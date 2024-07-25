FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ENV DB_URL jdbc:postgresql://192.168.1.37:5432/edgegateway
ENV DB_USER edgegatewayadmin
ENV DB_PASSWORD edgegateway
ENV MQTT_SERVER_HOST 192.168.1.37
ENV MQTT_SERVER_PORT 1883
ENV MQTT_CLIENT_ID gateway2
COPY target/edgegateway.jar edgegateway.jar
ENTRYPOINT ["java","-jar","/edgegateway.jar"]