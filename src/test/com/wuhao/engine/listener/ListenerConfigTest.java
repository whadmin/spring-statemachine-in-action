package com.wuhao.engine.listener;

import com.wuhao.engine.listener.listener.StateMachineApplicationEventListener;
import com.wuhao.engine.listener.listener.TestMachineListener;
import com.wuhao.engine.listener.stateMachine.StateMachineListenerDisabledConfig;
import com.wuhao.engine.listener.stateMachine.StateMachineListenerConfig;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/1 16:58
 */
public class ListenerConfigTest {


    /**
     * @throws Exception
     */
    @Test
    public void contextEventsEnabled() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());

        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        StateMachineApplicationEventListener listener2 = context.getBean(StateMachineApplicationEventListener.class);

        //启动状态机
        machine.start();
        assertThat(listener.startLatch.await(2, TimeUnit.SECONDS), is(true));
        assertThat(listener.started, is(1));

        assertThat(listener2.latch.await(2, TimeUnit.SECONDS), is(true));
        assertThat(listener2.count, greaterThan(1));
    }

    /**
     * 使用 @EnableStateMachine(contextEvents = false) 关闭Spring事件监听
     */
    @Test
    public void contextEventsDisabled() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerDisabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());

        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        StateMachineApplicationEventListener listener2 = context.getBean(StateMachineApplicationEventListener.class);

        machine.start();
        assertThat(machine.getState().getId(), is(TestStates.S1));
        assertThat(listener.startLatch.await(2, TimeUnit.SECONDS), is(true));
        assertThat(listener.started, is(1));

        assertThat(listener2.latch.await(2, TimeUnit.SECONDS), is(false));
        assertThat(listener2.count,is(0));
    }
}
