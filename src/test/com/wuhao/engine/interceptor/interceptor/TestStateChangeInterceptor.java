package com.wuhao.engine.interceptor.interceptor;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.springframework.messaging.Message;
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
public class TestStateChangeInterceptor extends StateMachineInterceptorAdapter<TestStates, TestEvents> {

    public volatile CountDownLatch preStateChangeLatch1 = new CountDownLatch(1);
    public volatile CountDownLatch preStateChangeLatch2 = new CountDownLatch(1);
    public volatile CountDownLatch postStateChangeLatch1 = new CountDownLatch(1);
    public volatile CountDownLatch postStateChangeLatch2 = new CountDownLatch(1);
    public volatile int preStateChangeCount1 = 0;
    public volatile int preStateChangeCount2 = 0;
    public volatile int postStateChangeCount1 = 0;
    public volatile int postStateChangeCount2 = 0;
    public ArrayList<State<TestStates, TestEvents>> preStateChangeStates1 = new ArrayList<>();
    public ArrayList<State<TestStates, TestEvents>> preStateChangeStates2 = new ArrayList<>();
    public ArrayList<State<TestStates, TestEvents>> postStateChangeStates1 = new ArrayList<>();
    public ArrayList<State<TestStates, TestEvents>> postStateChangeStates2 = new ArrayList<>();

    @Override
    public void preStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                               StateMachine<TestStates, TestEvents> stateMachine) {
        preStateChangeStates1.add(state);
        preStateChangeCount1++;
        preStateChangeLatch1.countDown();
    }

    @Override
    public void preStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                               StateMachine<TestStates, TestEvents> stateMachine, StateMachine<TestStates, TestEvents> rootStateMachine) {
        preStateChangeStates2.add(state);
        preStateChangeCount2++;
        preStateChangeLatch2.countDown();
    }

    @Override
    public void postStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                                StateMachine<TestStates, TestEvents> stateMachine) {
        postStateChangeStates1.add(state);
        postStateChangeCount1++;
        postStateChangeLatch1.countDown();
    }

    @Override
    public void postStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                                StateMachine<TestStates, TestEvents> stateMachine, StateMachine<TestStates, TestEvents> rootStateMachine) {
        postStateChangeStates2.add(state);
        postStateChangeCount2++;
        postStateChangeLatch2.countDown();
    }

}
