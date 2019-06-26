package com.jxtc.bookapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author intellij Idea
 * 本类是spring boot项目的启动类
 */
@SpringBootApplication
@MapperScan("com.jxtc.bookapp.mapper")
@EnableTransactionManagement//开启事务管理
@EnableScheduling//开启定时任务配置
public class BookappApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookappApplication.class, args);
    }

    /**
     * 注册可以访问的路径
     * @return
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
