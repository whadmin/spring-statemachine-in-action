package com.wuhao.engine.config;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.statemachine.data.redis.RedisPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.redis.RedisStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 13:09
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public String stateChartModel() throws IOException {
        ClassPathResource model = new ClassPathResource("statechartmodel.txt");
        InputStream inputStream = model.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return content;
    }

    @Bean
    public StateMachineRuntimePersister<TestStates, TestEvents, String> stateMachineRuntimePersister(
            RedisStateMachineRepository jpaStateMachineRepository) {
        return new RedisPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }
}
