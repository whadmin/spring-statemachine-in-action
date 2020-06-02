package com.wuhao.engine.config;

import com.wuhao.engine.action.S1toS2Action;
import com.wuhao.engine.action.S2toS3Action;
import com.wuhao.engine.action.S3toS4Action;
import com.wuhao.engine.action.S4toS5Action;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import java.util.EnumSet;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/28 19:05
 */
@Configuration
@EnableStateMachineFactory(name = "default")
public class StateMachineFactoryConfig
        extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {


    @Autowired
    private S1toS2Action s1tos2Action;

    @Autowired
    private S2toS3Action s2tos3Action;

    @Autowired
    private S3toS4Action s3tos4Action;

    @Autowired
    private S4toS5Action s4tos5Action;

    /**
     * 设置状态机通用配置
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<TestStates, TestEvents> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(false);  //是否自动启动
    }

    /**
     * 设置状态机状态
     */
    @Override
    public void configure(StateMachineStateConfigurer<TestStates, TestEvents> states)
            throws Exception {
        states
                .withStates()
                .initial(TestStates.S1)
                .states(EnumSet.allOf(TestStates.class));
    }

    /**
     * 设置状态机过渡
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<TestStates, TestEvents> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(TestStates.S1)
                .target(TestStates.S2)
                .event(TestEvents.E1)
                .action(s1tos2Action)
                .and()
                .withExternal()
                .source(TestStates.S2)
                .target(TestStates.S3)
                .event(TestEvents.E2)
                .action(s2tos3Action)
                .and()
                .withExternal()
                .source(TestStates.S3)
                .target(TestStates.S4)
                .event(TestEvents.E3)
                .action(s3tos4Action)
                .and()
                .withExternal()
                .source(TestStates.S4)
                .target(TestStates.S5)
                .event(TestEvents.E4)
                .action(s4tos5Action);
    }

    @Bean
    public StateMachineService<TestStates, TestEvents> stateMachineService(
            @Autowired @Qualifier("default") StateMachineFactory<TestStates, TestEvents> stateMachineFactory,
            StateMachinePersist<TestStates, TestEvents, String> stateMachinePersist) {
        return new DefaultStateMachineService<TestStates, TestEvents>(stateMachineFactory, stateMachinePersist);
    }
}
