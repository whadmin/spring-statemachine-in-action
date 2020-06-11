package com.wuhao.engine.action;

import com.wuhao.engine.common.ErrorAction;
import com.wuhao.engine.common.BugAction;
import com.wuhao.engine.common.BugHandleAction;
import com.wuhao.engine.common.ExecuteAction;
import com.wuhao.engine.action.stateMachine.StateMachineTransitionActionConfig;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.interceptor.interceptor.TestStateMachineInterceptor;
import com.wuhao.engine.status.TestStates;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 13:15
 */
@Slf4j
public class TransitionActionTest {


    @Test
    public void testTransitionActions1() {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineTransitionActionConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);

        ExecuteAction s1tos2Action = context.getBean("s1tos2Action", ExecuteAction.class);
        BugAction s2Tos3BugAction = context.getBean("s2tos3BugAction", BugAction.class);
        BugHandleAction s2Tos3BugHandleAction = context.getBean("s2tos3BugHandleAction", BugHandleAction.class);
        ExecuteAction s3tos4Action = context.getBean("s3tos4Action", ExecuteAction.class);

        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
        assertThat(s1tos2Action.isSuccess, is(true));

        log.warn("触发E2事件 发生异常");
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).setHeader("bug", Boolean.TRUE).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
        //s2Tos3BugAction执行失败
        assertThat(s2Tos3BugAction.isSuccess, is(false));
        //s2Tos3BugHandleAction 从StateContext获取到异常
        assertThat(s2Tos3BugHandleAction.getException(), notNullValue());
        //重置s2Tos3BugHandleAction
        s2Tos3BugHandleAction.reset();

        log.warn("触发E2事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S3));
        //s2Tos3BugAction执行成功
        assertThat(s2Tos3BugAction.isSuccess, is(true));

        log.warn("触发E3事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S3));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S4));
        assertThat(s3tos4Action.isSuccess, is(true));
    }
}
