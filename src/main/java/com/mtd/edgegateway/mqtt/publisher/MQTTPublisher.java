package com.mtd.edgegateway.mqtt.publisher;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder.Send.Complete;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class MQTTPublisher {
   @SuppressWarnings("unused")
   private static final Logger log = LoggerFactory.getLogger(MQTTPublisher.class);
   @Value("${mqtt.server.host}")
   private String host;
   @Value("${mqtt.server.port}")
   private int port;
   @Value("${mqtt.client.id}")
   private String clientId;

   @SuppressWarnings({ "unused", "rawtypes" })
   void publish() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
      Mqtt5BlockingClient client = ((Mqtt5ClientBuilder)((Mqtt5ClientBuilder)((Mqtt5ClientBuilder)Mqtt5Client.builder().identifier(this.clientId)).serverHost(this.host)).serverPort(this.port)).buildBlocking();
      client.connect();
      SecretKey key = null;
      String encrypted = null;
      int count = 0;
      boolean var5 = false;

      while(true) {
         Random random = new Random();
         Integer temp = random.ints(20, 25).findFirst().getAsInt();
         String message = "{\"id_gateway\":\"gateway3\",\"id_device\":\"device3\",\"device_attribute\": \"temperature\",\"device_attribute_type\":\"number\",\"device_attribute_value\":\"" + temp + " °C\"}";
         ((Complete)((Complete)client.publishWith().topic("default/edgenode/temperature")).payload(message.getBytes())).send();

         try {
            Thread.sleep(5000L);
         } catch (InterruptedException var10) {
            throw new RuntimeException(var10);
         }

         ++count;
      }
   }

   @SuppressWarnings("unused")
private static String getEncryptedWithAES(String message) throws Throwable {
      try {
         SecretKey key;
         try {
            key = getKeyFromPassword("password", "salt");
         } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
         }

         String algorithm = "AES/CBC/PKCS5Padding";
         IvParameterSpec ivParameterSpec = generateIv();
         return encrypt(algorithm, message, key, ivParameterSpec);
      } catch (Throwable var6) {
         throw var6;
      }
   }

   @SuppressWarnings("unused")
private static String getEncryptedWith3DES(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
      byte[] secretKey = "9mng65v8jf4lxn93nabf981m".getBytes();
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "TripleDES");
      byte[] iv = "a76nb5h9".getBytes();
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      Cipher encryptCipher = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
      encryptCipher.init(1, secretKeySpec, ivSpec);
      byte[] secretMessagesBytes = message.getBytes(StandardCharsets.UTF_8);
      byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessagesBytes);
      String encrypted = Base64.getEncoder().encodeToString(encryptedMessageBytes);
      return encrypted;
   }

   public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(n);
      SecretKey key = keyGenerator.generateKey();
      return key;
   }

   public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
      SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
      return secret;
   }

   public static IvParameterSpec generateIv() {
      byte b = 1;
      byte[] iv = new byte[]{b, b, b, b, b, b, b, b, b, b, b, b, b, b, b, b};
      return new IvParameterSpec(iv);
   }

   public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(1, key, iv);
      byte[] cipherText = cipher.doFinal(input.getBytes());
      return Base64.getEncoder().encodeToString(cipherText);
   }
}