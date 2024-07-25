package com.mtd.edgegateway.configuration;

import com.mtd.edgegateway.mqtt.publisher.MQTTPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {
   @Bean(
      initMethod = "publish"
   )
   MQTTPublisher mQTTPublisher() {
      return new MQTTPublisher();
   }
}