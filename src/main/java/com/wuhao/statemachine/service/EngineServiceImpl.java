package com.wuhao.statemachine.service;


import com.wuhao.statemachine.context.EngineContext;
import com.wuhao.statemachine.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
public class EngineServiceImpl implements EngineService {

    @Autowired
    private StateMachineStrategyService stateMachineStrategyService;


    @Override
    public void execute(Long businessId, String businessIdentity, OrderEvent event, EngineContext<Object> engineContext) {
        StateMachine stateMachine = null;
        try {
            /** 1 根据不同的业务场景获取不同流程状态机，每个订单+业务对应唯一个状态机 **/
            stateMachine = stateMachineStrategyService.acquireBusinessStateMachine(String.valueOf(businessId), businessIdentity, event.getPreOrderStatus(), true);

            /** 2 构造消息 **/
            Message<OrderEvent> message = bulidMessage(businessId, businessIdentity, event, engineContext);

            /** 3 触发事件 **/
            stateMachine.sendEvent(message);
        } finally {
            /** 4 清理状态机 **/
            stateMachineStrategyService.releaseBusinessStateMachine(String.valueOf(businessId), true);
        }
    }

    /**
     * 构造消息事件
     *
     * @param businessId       业务ID
     * @param businessIdentity 业务身份
     * @param event            事件
     * @param engineContext    流程引擎上下文
     * @return
     */
    public Message<OrderEvent> bulidMessage(Long businessId, String businessIdentity, OrderEvent event, EngineContext<Object> engineContext) {
        MessageBuilder<OrderEvent> messageBuilder = MessageBuilder
                .withPayload(event)
                .setHeader("businessId", businessId)
                .setHeader("businessIdentity", businessIdentity)
                .setHeader("engineContext", engineContext);
        return messageBuilder.build();
    }
}
