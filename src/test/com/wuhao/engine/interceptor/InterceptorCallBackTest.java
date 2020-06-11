package com.wuhao.engine.interceptor;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.interceptor.interceptor.TestStateMachineInterceptor;
import com.wuhao.engine.interceptor.stateMachine.StateMachineInterceptorConfig;
import com.wuhao.engine.status.TestStates;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 17:24
 */
@Slf4j
public class InterceptorCallBackTest {

    /**
     * 拦截 状态更改后回调
     */
    @Test
    public void postStateChange() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();
        assertThat(testStateChangeInterceptor.postStateChangeCount1, is(1));
        assertThat(testStateChangeInterceptor.postStateChangeStates1.get(0).getId(), is(TestStates.S1));
        assertThat(testStateChangeInterceptor.postStateChangeCount2, is(1));
        assertThat(testStateChangeInterceptor.postStateChangeStates2.get(0).getId(), is(TestStates.S1));

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));

        assertThat(testStateChangeInterceptor.postStateChangeCount1, is(2));
        assertThat(testStateChangeInterceptor.postStateChangeStates1.get(1).getId(), is(TestStates.S2));
        assertThat(testStateChangeInterceptor.postStateChangeCount2, is(2));
        assertThat(testStateChangeInterceptor.postStateChangeStates2.get(1).getId(), is(TestStates.S2));
    }


    /**
     * 拦截 状态更改后回调
     * Action发生异常，不在回调
     */
    @Test
    public void postStateChange2() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();
        assertThat(testStateChangeInterceptor.postStateChangeCount1, is(1));
        assertThat(testStateChangeInterceptor.postStateChangeStates1.get(0).getId(), is(TestStates.S1));
        assertThat(testStateChangeInterceptor.postStateChangeCount2, is(1));
        assertThat(testStateChangeInterceptor.postStateChangeStates2.get(0).getId(), is(TestStates.S1));

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("bug", Boolean.TRUE).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));

        assertThat(testStateChangeInterceptor.postStateChangeCount1, is(1));
        assertThat(testStateChangeInterceptor.postStateChangeStates1.size(),is(1));
        assertThat(testStateChangeInterceptor.postStateChangeCount2, is(1));
        assertThat(testStateChangeInterceptor.postStateChangeStates2.size(), is(1));
    }


    /**
     * 拦截 状态更改前回调，（初始化状态不会回调）
     */
    @Test
    public void preStateChange() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));

        assertThat(testStateChangeInterceptor.preStateChangeCount1, is(1));
        assertThat(testStateChangeInterceptor.preStateChangeStates1.get(0).getId(), is(TestStates.S2));
        assertThat(testStateChangeInterceptor.preStateChangeCount2, is(1));
        assertThat(testStateChangeInterceptor.preStateChangeStates2.get(0).getId(), is(TestStates.S2));
    }

    /**
     * 拦截 状态更改前回调,引发异常将停止状态更改
     */
    @Test
    public void preStateChange1() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("bug1", Boolean.TRUE).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));

        assertThat(testStateChangeInterceptor.preStateChangeCount1, is(1));
        assertThat(testStateChangeInterceptor.preStateChangeStates1.get(0).getId(), is(TestStates.S2));
        assertThat(testStateChangeInterceptor.preStateChangeCount2, is(1));
        assertThat(testStateChangeInterceptor.preStateChangeStates2.get(0).getId(), is(TestStates.S2));
    }


    /**
     * 拦截 触发Transition，则在Transition后回调
     */
    @Test
    public void postTransition() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
        assertThat(testStateChangeInterceptor.postTransitionCount, is(1));
    }


    /**
     * 拦截 触发Transition，则在Transition后回调,
     * Action发生异常，正常回调
     */
    @Test
    public void postTransition2() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("bug", Boolean.TRUE).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        assertThat(testStateChangeInterceptor.postTransitionCount, is(1));
    }


    /**
     * 拦截 触发Transition,在Transition开始之前回调,
     */
    @Test
    public void preTransition() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S2));
        assertThat(testStateChangeInterceptor.preTransitionCount, is(1));
    }

    /**
     * 拦截 触发Transition,在Transition开始之前回调,引发异常将停止状态更改
     */
    @Test
    public void preTransition2() throws Exception {
        //创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StateMachineInterceptorConfig.class);
        //从IOC中获取状态机
        ObjectStateMachine<TestStates, TestEvents> stateMachine = context.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, ObjectStateMachine.class);
        //从IOC中获取拦截器
        TestStateMachineInterceptor testStateChangeInterceptor = context.getBean("testStateMachineInterceptor", TestStateMachineInterceptor.class);
        //手动给状态机设置拦截器
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.addStateMachineInterceptor(testStateChangeInterceptor);
            }
        });
        log.warn("启动状态机");
        stateMachine.start();

        log.warn("触发E1事件");
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        stateMachine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("bug2", Boolean.TRUE).build());
        assertThat(stateMachine.getState().getId(), is(TestStates.S1));
        assertThat(testStateChangeInterceptor.preTransitionCount, is(1));
    }
}
