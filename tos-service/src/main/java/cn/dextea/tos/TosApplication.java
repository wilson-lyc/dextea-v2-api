package cn.dextea.tos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TosApplication {
    public static void main(String[] args) {
        SpringApplication.run(TosApplication.class, args);
    }
}
