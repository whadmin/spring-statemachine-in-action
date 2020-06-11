package com.wuhao.engine.action.stateMachine;

import com.wuhao.engine.common.BugAction;
import com.wuhao.engine.common.BugHandleAction;
import com.wuhao.engine.common.ErrorAction;
import com.wuhao.engine.common.ExecuteAction;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/11 15:16
 */
@Configuration
@EnableStateMachine
public class StateMachineStateActionConfig extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<TestStates, TestEvents> states) throws Exception {
        states
                .withStates()
                .initial(TestStates.S1)
                .state(TestStates.S1)
                .state(TestStates.S2)
                .state(TestStates.S3)
                .state(TestStates.S4)
                .stateExit(TestStates.S1, s1ExitErrorAction(), s1ExitErrorHandleAction())
                .stateEntry(TestStates.S2, s2EntryErrorAction(), s2EntryErrorHandleAction())
                .stateDo(TestStates.S2, s2DoErrorAction(), s2DoErrorHandleAction())
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TestStates, TestEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(TestStates.S1)
                .target(TestStates.S2)
                .event(TestEvents.E1)
                .and()
                .withExternal()
                .source(TestStates.S2)
                .target(TestStates.S3)
                .event(TestEvents.E2)
                .and()
                .withExternal()
                .source(TestStates.S3)
                .target(TestStates.S4)
                .event(TestEvents.E3);
    }

    @Bean
    public ErrorAction s1ExitErrorAction() {
        return new ErrorAction("S1 Exit Error Action");
    }

    @Bean
    public BugHandleAction s1ExitErrorHandleAction() {
        return new BugHandleAction("S1 Exit Error Handle Action");
    }

    @Bean
    public ErrorAction s2EntryErrorAction() {
        return new ErrorAction("S2 Entry Error Action");
    }

    @Bean
    public BugHandleAction s2EntryErrorHandleAction() {
        return new BugHandleAction("S2 Entry Error Handle Action");
    }

    @Bean
    public ErrorAction s2DoErrorAction() {
        return new ErrorAction("S2 Do Error Action");
    }

    @Bean
    public BugHandleAction s2DoErrorHandleAction() {
        return new BugHandleAction("S2 Do Error Handle Action");
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
