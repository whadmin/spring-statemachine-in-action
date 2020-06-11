package com.wuhao.engine.action;

import com.wuhao.engine.action.stateMachine.StateMachineStateActionConfig;
import com.wuhao.engine.action.stateMachine.StateMachineTransitionActionConfig;
import com.wuhao.engine.common.BugAction;
import com.wuhao.engine.common.BugHandleAction;
import com.wuhao.engine.common.ErrorAction;
import com.wuhao.engine.common.ExecuteAction;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * StateAction 可以进行Action处理，发生异常也可以捕获，但不影响状态机正常推进
 *
 * @Author: wuhao.w
 * @Date: 2020/6/11 15:35
 */
@Slf4j
public class StateActionTest {

    @Test
    public void testStateActions() {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineStateActionConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);

        ErrorAction s1ExitErrorAction = context.getBean("s1ExitErrorAction", ErrorAction.class);
        BugHandleAction s1ExitBugHandleAction = context.getBean("s1ExitErrorHandleAction", BugHandleAction.class);

        ErrorAction s2EntryErrorAction = context.getBean("s2EntryErrorAction", ErrorAction.class);
        BugHandleAction s2EntryBugHandleAction = context.getBean("s2EntryErrorHandleAction", BugHandleAction.class);

        ErrorAction s2DoErrorAction = context.getBean("s2DoErrorAction", ErrorAction.class);
        BugHandleAction s2DoBugHandleAction = context.getBean("s2DoErrorHandleAction", BugHandleAction.class);

        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        // s1ExitBugHandleAction从StateContext获取到 s1ExitErrorAction异常
        assertThat(s1ExitBugHandleAction.getException(), notNullValue());
        // s2EntryBugHandleAction从StateContext获取到 s2EntryErrorAction异常
        assertThat(s2EntryBugHandleAction.getException(), notNullValue());
        // s2DoBugHandleAction从StateContext获取到 s2DoErrorAction异常
        assertThat(s2DoBugHandleAction.getException(), notNullValue());
        // 状态推进
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
    }
}
