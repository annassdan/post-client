package com.project.ifish.postclient.configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@SuppressWarnings("unused")
public class EBrplTaskConnectorConfig {


    @Bean
    public TaskExecutor ebrplConnectorConfig() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1000000);
        executor.setMaxPoolSize(1000000);
        executor.setQueueCapacity(5000000);
        executor.setThreadNamePrefix("EBrplConnector-");
        executor.initialize();
        return executor;
    }

}
