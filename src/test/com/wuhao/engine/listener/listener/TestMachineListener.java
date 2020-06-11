package com.wuhao.engine.listener.listener;

import com.wuhao.engine.common.ExtendedStateChangedHolder;
import com.wuhao.engine.common.StatesChangeHolder;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.InitialTransition;
import org.springframework.statemachine.transition.Transition;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 16:22
 */
@Slf4j
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
        log.warn(String.format("触发监听：stateMachineStarted"));
        started++;
        startLatch.countDown();
    }


    @Override
    public void stateMachineStopped(StateMachine<TestStates, TestEvents> stateMachine) {
        log.warn(String.format("触发监听：stateMachineStopped"));
        stopped++;
        stopLatch.countDown();
    }


    @Override
    public void stateChanged(State<TestStates, TestEvents> from, State<TestStates, TestEvents> to) {
        if (from == null) {
            log.warn(String.format("触发监听：stateChanged (%s,%s)", "", to.getId().toString()));
        } else {
            log.warn(String.format("触发监听：stateChanged (%s,%s)", from.getId().toString(), to.getId().toString()));
        }
        states.add(new StatesChangeHolder(from, to));
    }

    @Override
    public void stateEntered(State<TestStates, TestEvents> state) {
        log.warn(String.format("触发监听：stateEntered %s", state.getId().toString()));
        enteredStates.add(state);
    }

    @Override
    public void stateExited(State<TestStates, TestEvents> state) {
        log.warn(String.format("触发监听：stateExited %s", state.getId().toString()));
        exitedStates.add(state);
    }

    @Override
    public void eventNotAccepted(Message<TestEvents> event) {
        log.warn(String.format("触发监听：eventNotAccepted %s", event.toString()));
    }

    @Override
    public void transition(Transition<TestStates, TestEvents> transition) {
        if (transition instanceof InitialTransition) {
            log.warn(String.format("触发监听：transition transition(%s,%s)", "", transition.getTarget().getId().toString()));
        } else {
            log.warn(String.format("触发监听：transition transition(%s,%s)", transition.getSource().getId().toString(), transition.getTarget().getId().toString()));
        }
        transitions.add(transition);
    }

    @Override
    public void transitionStarted(Transition<TestStates, TestEvents> transition) {
        if (transition instanceof InitialTransition) {
            log.warn(String.format("触发监听：transitionStarted transition(%s,%s)", "", transition.getTarget().getId().toString()));
        } else {
            log.warn(String.format("触发监听：transitionStarted transition(%s,%s)", transition.getSource().getId().toString(), transition.getTarget().getId().toString()));
        }
        starTransitions.add(transition);
    }

    @Override
    public void transitionEnded(Transition<TestStates, TestEvents> transition) {
        if (transition instanceof InitialTransition) {
            log.warn(String.format("触发监听：transitionEnded transition(%s,%s)", "", transition.getTarget().getId().toString()));
        } else {
            log.warn(String.format("触发监听：transitionEnded transition(%s,%s)", transition.getSource().getId().toString(), transition.getTarget().getId().toString()));
        }
        endTransitions.add(transition);
    }


    @Override
    public void stateMachineError(StateMachine<TestStates, TestEvents> stateMachine, Exception exception) {
        log.warn("触发事件：stateMachineError:" + exception);
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        log.warn(String.format("触发监听：extendedStateChanged (%s,%s)", key.toString(), value.toString()));
        extendedStates.add(new ExtendedStateChangedHolder(key, value));
        extendedLatch.countDown();
    }

    @Override
    public void stateContext(StateContext<TestStates, TestEvents> stateContext) {
    }
}
