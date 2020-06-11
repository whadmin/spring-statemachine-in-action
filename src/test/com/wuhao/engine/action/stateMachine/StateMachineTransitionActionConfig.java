package com.wuhao.engine.action.stateMachine;

import com.wuhao.engine.common.ErrorAction;
import com.wuhao.engine.common.BugHandleAction;
import com.wuhao.engine.common.BugAction;
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
 * @Date: 2020/5/29 13:18
 */
@Configuration
@EnableStateMachine
public class StateMachineTransitionActionConfig extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {

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
                .action(s2tos3BugAction(), s2tos3BugHandleAction())
                .and()
                .withExternal()
                .source(TestStates.S3)
                .target(TestStates.S4)
                .event(TestEvents.E3)
                .action(s3tos4Action());
    }

    @Bean
    public ExecuteAction s1tos2Action() {
        return new ExecuteAction("S1->S2 Action");
    }

    @Bean
    public BugAction s2tos3BugAction() {
        return new BugAction("S2->S3 Action");
    }

    @Bean
    public BugHandleAction s2tos3BugHandleAction() {
        return new BugHandleAction("S2->S3 bug handle Action");
    }

    @Bean
    public ExecuteAction s3tos4Action() {
        return new ExecuteAction("S3->S4 Action");
    }

}
