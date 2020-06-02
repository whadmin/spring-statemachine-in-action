package com.wuhao.engine.interceptor;

import com.wuhao.engine.action.AbstractAction;
import com.wuhao.engine.context.EngineContext;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.InitialTransition;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 17:20
 */
@Component
public class OnNextActionInterceptor extends StateMachineInterceptorAdapter<TestStates, TestEvents> {




    @Override
    public void postStateChange(State<TestStates, TestEvents> state, Message<TestEvents> message, Transition<TestStates, TestEvents> transition,
                                StateMachine<TestStates, TestEvents> stateMachine, StateMachine<TestStates, TestEvents> rootStateMachine) {

        if(transition instanceof InitialTransition){
            return;
        }
        //获取当前状态
        TestStates currentState = stateMachine.getState().getId();
        //获取传入目标状态
        EngineContext engineContext = message.getHeaders().get("engineContext", EngineContext.class);

        if (Objects.nonNull(engineContext)&&hasNext(stateMachine.getState().getId(), engineContext)) {
            onNext(stateMachine, transition, message);
        }
    }

    public boolean hasNext(TestStates currentState, EngineContext engineContext) {
        return !currentState.equals(engineContext.getTarget());
    }


    protected void onNext(StateMachine<TestStates, TestEvents> stateMachine, Transition<TestStates, TestEvents> transition, Message<TestEvents> message) {
        TestEvents nextEvent = transition.getActions().stream().filter(p -> p instanceof AbstractAction).map(action -> {
            return ((AbstractAction) action).getNextEvent();
        }).findFirst().get();
        boolean result = stateMachine.sendEvent(bulidMessage(nextEvent, message.getHeaders()));
        System.out.println(result);
    }

    /**
     * 构造消息事件
     */
    public Message<TestEvents> bulidMessage(TestEvents event, MessageHeaders headers) {
        MessageBuilder<TestEvents> messageBuilder = MessageBuilder
                .withPayload(event);
        headers.entrySet().stream().filter(p -> !p.getKey().equals("id") && !p.getKey().equals("timestamp")).forEach(entry -> {
            messageBuilder.setHeader(entry.getKey(), entry.getValue());
        });
        return messageBuilder.build();
    }

}
