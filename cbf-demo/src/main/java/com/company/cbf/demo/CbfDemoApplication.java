package com.company.cbf.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CBF 框架示例应用启动类
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.company.cbf.demo.mapper")
public class CbfDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbfDemoApplication.class, args);
    }
}
