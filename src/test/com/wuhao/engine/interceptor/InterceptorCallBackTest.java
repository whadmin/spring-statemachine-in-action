package com.wuhao.engine.interceptor;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.interceptor.interceptor.TestStateChangeInterceptor;
import com.wuhao.engine.interceptor.stateMachine.StateMachineInterceptorConfig;
import com.wuhao.engine.listener.listener.TestMachineListener;
import com.wuhao.engine.listener.stateMachine.StateMachineListenerEnabledConfig;
import com.wuhao.engine.status.TestStates;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 17:24
 */
public class InterceptorCallBackTest {

    @Test
    public void teststateMachineStartedAndStop() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestStateChangeInterceptor testStateChangeInterceptor = context.getBean("testStateChangeInterceptor", TestStateChangeInterceptor.class);

        machine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });

        //启动状态机
        machine.start();
        //触发TestEvents.E1事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E4).build());
    }
}
