package com.wuhao.engine.action;

import com.wuhao.engine.common.ErrorAction;
import com.wuhao.engine.common.ErrorRetryAction;
import com.wuhao.engine.common.ErrorHandleAction;
import com.wuhao.engine.common.ExecuteAction;
import com.wuhao.engine.action.stateMachine.StateMachineActionConfig;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 13:15
 */
public class ActionTest {

    protected AnnotationConfigApplicationContext context;

    @Before
    public void setup() {
        context = buildContext();
    }

    @After
    public void clean() {
        if (context != null) {
            context.close();
        }
    }

    protected AnnotationConfigApplicationContext buildContext() {
        return new AnnotationConfigApplicationContext();
    }

    @Test
    public void testTransitionActions() {
        context.register(StateMachineActionConfig.class);
        context.refresh();
        assertTrue(context.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
        StateMachine<TestStates, TestEvents> machine =
                context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, StateMachine.class);
        machine.start();

        ErrorAction s1ExitErrorAction = context.getBean("s1ExitErrorAction", ErrorAction.class);
        ErrorHandleAction s1ExitErrorHandleAction = context.getBean("s1ExitErrorHandleAction", ErrorHandleAction.class);
        ExecuteAction s1tos2Action = context.getBean("s1tos2Action", ExecuteAction.class);
        ErrorAction s2EntryErrorAction = context.getBean("s2EntryErrorAction", ErrorAction.class);
        ErrorHandleAction s2EntryErrorHandleAction = context.getBean("s2EntryErrorHandleAction", ErrorHandleAction.class);
        ErrorAction s2DoErrorAction = context.getBean("s2DoErrorAction", ErrorAction.class);
        ErrorHandleAction s2DoErrorHandleAction = context.getBean("s2DoErrorHandleAction", ErrorHandleAction.class);


        ErrorRetryAction s2tos3ErrorRetryAction = context.getBean("s2tos3ErrorRetryAction", ErrorRetryAction.class);
        ErrorHandleAction s2tos3ErrorHandleAction = context.getBean("s2tos3ErrorHandleAction", ErrorHandleAction.class);
        ExecuteAction s3tos4Action = context.getBean("s3tos4Action", ExecuteAction.class);

        //执行(TestEvents.E1事件
        assertThat(machine.getState().getId(), is(TestStates.S1));
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());

        assertThat(s1ExitErrorAction.getIsExecute(), is(true));

        assertThat(s1ExitErrorHandleAction.getIsExecute(), is(true));
        assertThat(s1ExitErrorHandleAction.getException(), notNullValue());

        assertThat(s1tos2Action.isSuccess, is(true));

        assertThat(s2EntryErrorAction.getIsExecute(), is(true));
        assertThat(s2EntryErrorHandleAction.getException(), notNullValue());

        assertThat(s2DoErrorAction.getIsExecute(), is(true));
        assertThat(s2DoErrorHandleAction.getException(), notNullValue());

        assertThat(machine.getState().getId(), is(TestStates.S2));


        //执行(TestEvents.E2事件,不加上重试参数,s2tos3ErrorAction会抛出异常,通过s2tos3ErrorHandleAction捕获异常,做特殊处理
        assertThat(machine.getState().getId(), is(TestStates.S2));
        boolean retry = machine.sendEvent(MessageBuilder.withPayload(TestEvents.EH).setHeader("retry", Boolean.FALSE).build());
        assertThat(machine.getState().getId(), is(TestStates.S2));
        assertThat(s2tos3ErrorRetryAction.getIsSuccess(), is(false));
        assertThat(s2tos3ErrorHandleAction.getIsExecute(), is(true));
        assertThat(s2tos3ErrorHandleAction.getException(), notNullValue());
        s2tos3ErrorHandleAction.reset();
        //执行(TestEvents.E2事件,加上重试保证成功
        assertThat(machine.getState().getId(), is(TestStates.S2));
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).setHeader("retry",Boolean.TRUE).build());
        assertThat(machine.getState().getId(), is(TestStates.S3));
        assertThat(s2tos3ErrorRetryAction.getIsSuccess(), is(true));
        assertThat(s2tos3ErrorHandleAction.getIsExecute(), is(false));
        assertThat(s2tos3ErrorHandleAction.getException(), nullValue());

        //执行(TestEvents.E3事件
        assertThat(machine.getState().getId(), is(TestStates.S3));
        machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
        assertThat(s3tos4Action.isSuccess, is(true));
        assertThat(machine.getState().getId(), is(TestStates.S4));
    }
}
