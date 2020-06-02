package com.wuhao.engine.action;

import com.wuhao.engine.context.EngineContext;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.Data;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;

import java.util.Map;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:07
 */
@Data
public abstract class AbstractAction implements Action<TestStates, TestEvents> {

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        //获取数据参数
        MessageHeaders messageHeaders = context.getMessageHeaders();

        executeInternal(messageHeaders);
    }


    public abstract void executeInternal(MessageHeaders messageHeaders);


    public abstract TestEvents getNextEvent();
}
