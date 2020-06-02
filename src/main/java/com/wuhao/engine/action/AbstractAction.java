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
        //获取上下文
        EngineContext engineContext = messageHeaders.get("engineContext", EngineContext.class);
        if (hasNext(context, engineContext)) {
            onNext(context);
        }
    }

    protected void onNext(StateContext<TestStates, TestEvents> context) {
        boolean result = context.getStateMachine().sendEvent(bulidMessage(getNextEvent(), context.getMessage().getHeaders()));
        System.out.println(result);
    }

    public boolean hasNext(StateContext<TestStates, TestEvents> context, EngineContext engineContext) {
        return !context.getTransition().getTarget().getId().equals(engineContext.getTarget());
    }

    /**
     * 构造消息事件
     */
    public Message<TestEvents> bulidMessage(TestEvents event, MessageHeaders headers) {
        MessageBuilder<TestEvents> messageBuilder = MessageBuilder
                .withPayload(event);
        headers.entrySet().stream().filter(p->!p.getKey().equals("id")&&!p.getKey().equals("timestamp")).forEach(entry -> {
            messageBuilder.setHeader(entry.getKey(), entry.getValue());
        });
        return messageBuilder.build();
    }

    public abstract void executeInternal(MessageHeaders messageHeaders);


    public abstract TestEvents getNextEvent();
}
