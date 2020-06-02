package com.wuhao.engine.listener;

import com.wuhao.engine.listener.listener.TestMachineListener;
import com.wuhao.engine.listener.stateMachine.StateMachineListenerEnabledConfig;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.transition.DefaultExternalTransition;
import org.springframework.statemachine.transition.InitialTransition;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/1 12:11
 */
public class ListenerCallBackTest {

    @Test
    public void teststateMachineStartedAndStop() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();
        assertThat(listener.startLatch.await(2, TimeUnit.SECONDS), is(true));
        assertThat(listener.started, is(1));
        //关闭状态机
        machine.stop();
        assertThat(listener.stopLatch.await(2, TimeUnit.SECONDS), is(true));
        assertThat(listener.stopped, is(1));
    }


    @Test
    public void testStateChanged() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();
        assertThat(listener.states.size(), is(1));
        assertThat(listener.states.get(0).from, nullValue());
        assertThat(listener.states.get(0).to.getIds(), contains(TestStates.S1));

        //触发TestEvents.E1事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(listener.states.size(), is(2));
        assertThat(listener.states.get(1).from.getIds(), contains(TestStates.S1));
        assertThat(listener.states.get(1).to.getIds(), contains(TestStates.S2));

        //触发TestEvents.E2事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
        assertThat(listener.states.size(), is(3));
        assertThat(listener.states.get(2).from.getIds(), contains(TestStates.S2));
        assertThat(listener.states.get(2).to.getIds(), contains(TestStates.S3));

        //触发TestEvents.E3事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
        assertThat(listener.states.size(), is(4));
        assertThat(listener.states.get(3).from.getIds(), contains(TestStates.S3));
        assertThat(listener.states.get(3).to.getIds(), contains(TestStates.S4));

        //关闭状态机
        machine.stop();
    }

    @Test
    public void testStateEntered() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();
        assertThat(listener.enteredStates.size(), is(1));
        assertThat(listener.enteredStates.get(0).getId(), is(TestStates.S1));

        //触发TestEvents.E1事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(listener.enteredStates.size(), is(2));
        assertThat(listener.enteredStates.get(1).getId(), is(TestStates.S2));

        //触发TestEvents.E2事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
        assertThat(listener.enteredStates.size(), is(3));
        assertThat(listener.enteredStates.get(2).getId(), is(TestStates.S3));

        //触发TestEvents.E3事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
        assertThat(listener.enteredStates.size(), is(4));
        assertThat(listener.enteredStates.get(3).getId(), is(TestStates.S4));

        //关闭状态机
        machine.stop();
    }

    @Test
    public void testStateExited() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();

        //触发TestEvents.E1事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(listener.exitedStates.size(), is(1));
        assertThat(listener.exitedStates.get(0).getId(), is(TestStates.S1));

        //触发TestEvents.E2事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
        assertThat(listener.exitedStates.size(), is(2));
        assertThat(listener.exitedStates.get(1).getId(), is(TestStates.S2));

        //触发TestEvents.E3事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
        assertThat(listener.exitedStates.size(), is(3));
        assertThat(listener.exitedStates.get(2).getId(), is(TestStates.S3));

        //关闭状态机
        machine.stop();
    }

    @Test
    public void testExtendedStateEvents() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();

        machine.getExtendedState().getVariables().put("foo", "jee");
        assertThat(listener.extendedLatch.await(2, TimeUnit.SECONDS), is(true));
        assertThat(listener.extendedStates.size(), is(1));
        assertThat(listener.extendedStates.get(0).key, is("foo"));
        assertThat(listener.extendedStates.get(0).value, is("jee"));
        //关闭状态机
        machine.stop();
    }


    @Test
    public void testTransition() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();

        assertThat(listener.starTransitions.size(), is(1));
        assertThat(listener.starTransitions.get(0), instanceOf(InitialTransition.class));
        assertThat(((InitialTransition) listener.starTransitions.get(0)).getSource(), nullValue());
        assertThat(((InitialTransition) listener.starTransitions.get(0)).getTarget().getId(), is(TestStates.S1));

        assertThat(listener.transitions.size(), is(1));
        assertThat(listener.transitions.get(0), instanceOf(InitialTransition.class));
        assertThat(((InitialTransition) listener.transitions.get(0)).getSource(), nullValue());
        assertThat(((InitialTransition) listener.transitions.get(0)).getTarget().getId(), is(TestStates.S1));

        assertThat(listener.endTransitions.size(), is(1));
        assertThat(listener.endTransitions.get(0), instanceOf(InitialTransition.class));
        assertThat(((InitialTransition) listener.endTransitions.get(0)).getSource(), nullValue());
        assertThat(((InitialTransition) listener.endTransitions.get(0)).getTarget().getId(), is(TestStates.S1));

        //触发TestEvents.E1事件
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(listener.starTransitions.size(), is(2));
        assertThat(listener.starTransitions.get(1), instanceOf(DefaultExternalTransition.class));
        assertThat(((DefaultExternalTransition) listener.starTransitions.get(1)).getSource().getId(), is(TestStates.S1));
        assertThat(((DefaultExternalTransition) listener.starTransitions.get(1)).getTarget().getId(), is(TestStates.S2));

        assertThat(listener.transitions.size(), is(2));
        assertThat(listener.transitions.get(1), instanceOf(DefaultExternalTransition.class));
        assertThat(((DefaultExternalTransition) listener.transitions.get(1)).getSource().getId(), is(TestStates.S1));
        assertThat(((DefaultExternalTransition) listener.transitions.get(1)).getTarget().getId(), is(TestStates.S2));

        assertThat(listener.endTransitions.size(), is(2));
        assertThat(listener.endTransitions.get(1), instanceOf(DefaultExternalTransition.class));
        assertThat(((DefaultExternalTransition) listener.endTransitions.get(1)).getSource().getId(), is(TestStates.S1));
        assertThat(((DefaultExternalTransition) listener.endTransitions.get(1)).getTarget().getId(), is(TestStates.S2));
        //关闭状态机
        machine.stop();
    }

    @Test
    public void eventNotAccepted() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineListenerEnabledConfig.class);
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        ObjectStateMachine<TestStates, TestEvents> machine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        assertThat(machine, notNullValue());
        TestMachineListener listener = context.getBean("testMachineListener", TestMachineListener.class);
        //启动状态机
        machine.start();
    }
}
