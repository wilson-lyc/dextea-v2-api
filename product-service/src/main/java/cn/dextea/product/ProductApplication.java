package cn.dextea.product;

import cn.dextea.store.api.feign.StoreInternalFeign;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {StoreInternalFeign.class})
@ComponentScan(basePackages = {"cn.dextea.product", "cn.dextea.common"})
@MapperScan(basePackages = {"cn.dextea.product.mapper"})
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
