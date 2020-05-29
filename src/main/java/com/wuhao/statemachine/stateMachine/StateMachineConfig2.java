package com.wuhao.statemachine.stateMachine;

import com.wuhao.statemachine.event.TurnstileEvent;
import com.wuhao.statemachine.status.TurnstileStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/28 19:05
 */
@Configuration
@EnableStateMachine(name = "simple2")
public class StateMachineConfig2
        extends EnumStateMachineConfigurerAdapter<TurnstileStatus, TurnstileEvent> {

    /**
     * 设置状态机通用配置
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<TurnstileStatus, TurnstileEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(false);  //是否自动启动
    }

    @Override
    public void configure(StateMachineStateConfigurer<TurnstileStatus, TurnstileEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(TurnstileStatus.LOCKED)
                .states(EnumSet.allOf(TurnstileStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TurnstileStatus, TurnstileEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(TurnstileStatus.LOCKED)
                .target(TurnstileStatus.UNLOCKED)
                .event(TurnstileEvent.COIN)
                .and()
                .withExternal()
                .source(TurnstileStatus.UNLOCKED)
                .target(TurnstileStatus.LOCKED)
                .event(TurnstileEvent.PUSH);
    }

}
