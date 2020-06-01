package com.wuhao.statemachine.listener.listener;

import com.wuhao.statemachine.common.ExtendedStateChangedHolder;
import com.wuhao.statemachine.common.StatesChangeHolder;
import com.wuhao.statemachine.status.TestEvents;
import com.wuhao.statemachine.status.TestStates;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 16:22
 */
public class TestMachineListener implements StateMachineListener<TestStates, TestEvents> {

    public ArrayList<StatesChangeHolder> states = new ArrayList<StatesChangeHolder>();
    public ArrayList<State> enteredStates = new ArrayList<State>();
    public ArrayList<State> exitedStates = new ArrayList<State>();


    public ArrayList<Transition<TestStates, TestEvents>> transitions = new ArrayList<Transition<TestStates, TestEvents>>();
    public ArrayList<Transition<TestStates, TestEvents>> starTransitions = new ArrayList<Transition<TestStates, TestEvents>>();
    public ArrayList<Transition<TestStates, TestEvents>> endTransitions = new ArrayList<Transition<TestStates, TestEvents>>();

    public volatile int started = 0;
    public CountDownLatch startLatch = new CountDownLatch(1);
    public volatile int stopped = 0;
    public CountDownLatch stopLatch = new CountDownLatch(1);


    public ArrayList<ExtendedStateChangedHolder> extendedStates = new ArrayList<ExtendedStateChangedHolder>();
    public CountDownLatch extendedLatch = new CountDownLatch(1);

    @Override
    public void stateMachineStarted(StateMachine<TestStates, TestEvents> stateMachine) {
        started++;
        startLatch.countDown();
    }


    @Override
    public void stateMachineStopped(StateMachine<TestStates, TestEvents> stateMachine) {
        stopped++;
        stopLatch.countDown();
    }


    @Override
    public void stateChanged(State<TestStates, TestEvents> from, State<TestStates, TestEvents> to) {
        states.add(new StatesChangeHolder(from, to));
    }

    @Override
    public void stateEntered(State<TestStates, TestEvents> state) {
        enteredStates.add(state);
    }

    @Override
    public void stateExited(State<TestStates, TestEvents> state) {
        exitedStates.add(state);
    }

    @Override
    public void eventNotAccepted(Message<TestEvents> event) {
    }

    @Override
    public void transition(Transition<TestStates, TestEvents> transition) {
        transitions.add(transition);
    }

    @Override
    public void transitionStarted(Transition<TestStates, TestEvents> transition) {
        starTransitions.add(transition);
    }

    @Override
    public void transitionEnded(Transition<TestStates, TestEvents> transition) {
        endTransitions.add(transition);
    }



    @Override
    public void stateMachineError(StateMachine<TestStates, TestEvents> stateMachine, Exception exception) {
        System.err.println("触发事件：stateMachineError:" + exception);
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        extendedStates.add(new ExtendedStateChangedHolder(key, value));
        extendedLatch.countDown();
    }

    @Override
    public void stateContext(StateContext<TestStates, TestEvents> stateContext) {
    }
}
