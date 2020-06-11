package com.wuhao.engine.interceptor.interceptor;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 17:20
 */
@Slf4j
public class TestStateMachineInterceptor extends StateMachineInterceptorAdapter<TestStates, TestEvents> {


    public volatile int preStateChangeCount1 = 0;
    public volatile int preStateChangeCount2 = 0;
    public volatile int postStateChangeCount1 = 0;
    public volatile int postStateChangeCount2 = 0;
    public volatile int preTransitionCount = 0;
    public volatile int postTransitionCount = 0;

    public ArrayList<State<TestStates, TestEvents>> preStateChangeStates1 = new ArrayList<>();
    public ArrayList<State<TestStates, TestEvents>> preStateChangeStates2 = new ArrayList<>();
    public ArrayList<State<TestStates, TestEvents>> postStateChangeStates1 = new ArrayList<>();
    public ArrayList<State<TestStates, TestEvents>> postStateChangeStates2 = new ArrayList<>();

    public CountDownLatch preStateChangeStatesLatch1 = new CountDownLatch(1);
    public CountDownLatch preStateChangeStatesLatch2 = new CountDownLatch(1);
    public CountDownLatch postStateChangeStatesLatch1 = new CountDownLatch(1);
    public CountDownLatch postStateChangeStatesLatch2 = new CountDownLatch(1);

    /**
     * 状态更改之前回调。
     * 此方法引发异常将停止状态更改逻辑。
     */
    @Override
    public void preStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                               StateMachine<TestStates, TestEvents> stateMachine) {
        log.warn(String.format("preStateChange1 %s", state.getId().toString()));
        preStateChangeStates1.add(state);
        preStateChangeCount1++;
        preStateChangeStatesLatch1.countDown();
    }

    /**
     * 状态更改之前回调。
     * 此方法引发异常将停止状态更改逻辑。
     */
    @Override
    public void preStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                               StateMachine<TestStates, TestEvents> stateMachine, StateMachine<TestStates, TestEvents> rootStateMachine) {
        log.warn(String.format("preStateChange2 %s", state.getId().toString()));
        preStateChangeStates2.add(state);
        preStateChangeCount2++;
        preStateChangeStatesLatch2.countDown();

        Boolean retry = message.getHeaders().get("bug1", Boolean.class);
        if (retry==null||!retry) {
        } else {
            throw new RuntimeException("Fake Error");
        }
    }

    /**
     * 状态更改后回调。
     */
    @Override
    public void postStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                                StateMachine<TestStates, TestEvents> stateMachine) {
        log.warn(String.format("postStateChange1 %s", state.getId().toString()));
        postStateChangeStates1.add(state);
        postStateChangeCount1++;
        postStateChangeStatesLatch1.countDown();
    }

    /**
     * 状态更改后回调。
     */
    @Override
    public void postStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                                StateMachine<TestStates, TestEvents> stateMachine, StateMachine<TestStates, TestEvents> rootStateMachine) {
        log.warn(String.format("postStateChange2 %s", state.getId().toString()));
        postStateChangeStates2.add(state);
        postStateChangeCount2++;
        postStateChangeStatesLatch2.countDown();
    }


    /**
     * 触发Transition,在Transition开始之前回调
     * 如果此方法返回{@code null}将中断Transition
     * 如果此方抛出异常将中断Transition
     */
    @Override
    public StateContext<TestStates, TestEvents> preTransition(StateContext<TestStates, TestEvents> stateContext) {
        log.warn("preTransition");
        preTransitionCount++;

        Boolean retry = stateContext.getMessageHeaders().get("bug2", Boolean.class);
        if (retry==null||!retry) {
        } else {
            throw new RuntimeException("Fake Error");
//            return null;
        }
        return stateContext;
    }

    /**
     * 触发Transition，则在Transition后调用。
     * 如果此方抛出异常将中断Transition
     */
    @Override
    public StateContext<TestStates, TestEvents> postTransition(StateContext<TestStates, TestEvents> stateContext) {
        log.warn("postTransition");
        postTransitionCount++;
        return stateContext;
    }


}
