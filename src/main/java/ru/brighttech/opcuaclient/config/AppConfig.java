package ru.brighttech.opcuaclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AppConfig {
    @Bean
    public ExecutorService scheduledExecutorService () {

        return Executors.newFixedThreadPool(4);
    }
}
