package com.wuhao.engine.interceptor.stateMachine;

import com.wuhao.engine.common.BugAction;
import com.wuhao.engine.common.ExecuteAction;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.interceptor.interceptor.TestStateMachineInterceptor;
import com.wuhao.engine.listener.listener.TestMachineListener;
import com.wuhao.engine.status.TestStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/1 11:57
 */
@Configuration
@EnableStateMachine
public class StateMachineInterceptorConfig extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {

    /**
     * 设置状态机通用配置
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<TestStates, TestEvents> config)
            throws Exception {
        config
                .withConfiguration()
                //是否自动启动
                .autoStartup(false);

    }

    /**
     * 设置状态机状态配置
     */
    @Override
    public void configure(StateMachineStateConfigurer<TestStates, TestEvents> states) throws Exception {
        states
                .withStates()
                .initial(TestStates.S1)
                .state(TestStates.S1)
                .state(TestStates.S2)
                .state(TestStates.S3)
                .state(TestStates.S4);
    }

    /**
     * 设置状态机过渡配置
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<TestStates, TestEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(TestStates.S1)
                .target(TestStates.S2)
                .event(TestEvents.E1)
                .action(s1tos2Action())
        .and().withExternal();
    }


    @Bean
    public TestMachineListener testMachineListener() {
        return new TestMachineListener();
    }

    @Bean
    public BugAction s1tos2Action() {
        return new BugAction("S1->S2 Action");
    }

    @Bean
    public TestStateMachineInterceptor testStateMachineInterceptor(){
        return new TestStateMachineInterceptor();
    }

}
