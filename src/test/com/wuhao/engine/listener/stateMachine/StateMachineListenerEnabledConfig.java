package com.wuhao.engine.listener.stateMachine;

import com.wuhao.engine.common.ExecuteAction;
import com.wuhao.engine.listener.listener.StateMachineApplicationEventListener;
import com.wuhao.engine.listener.listener.TestMachineListener;
import com.wuhao.engine.event.TestEvents;
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
public class StateMachineListenerEnabledConfig extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {

    /**
     * 设置状态机通用配置
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<TestStates, TestEvents> config)
            throws Exception {
        config
                .withConfiguration()
                //是否自动启动
                .autoStartup(false)
                //设置监听器,也可以手动API设置 machine.addStateListener(listener);
                .listener(testMachineListener());
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
                .and()
                .withExternal()
                .source(TestStates.S2)
                .target(TestStates.S3)
                .event(TestEvents.E2)
                .action(s2tos3Action())
                .and()
                .withExternal()
                .source(TestStates.S3)
                .target(TestStates.S4)
                .event(TestEvents.E3)
                .action(s3tos4Action());
    }


    @Bean
    public TestMachineListener testMachineListener() {
        return new TestMachineListener();
    }

    @Bean
    public ExecuteAction s1tos2Action() {
        return new ExecuteAction("S1->S2 Action");
    }

    @Bean
    public ExecuteAction s2tos3Action() {
        return new ExecuteAction("S1->S2 Action");
    }

    @Bean
    public ExecuteAction s3tos4Action() {
        return new ExecuteAction("S1->S2 Action");
    }

    @Bean
    public StateMachineApplicationEventListener stateMachineApplicationEventListener() {
        return new StateMachineApplicationEventListener();
    }

}
