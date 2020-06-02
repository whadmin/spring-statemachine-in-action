package com.wuhao.engine.service;

import com.wuhao.engine.context.EngineContext;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.interceptor.OnNextActionInterceptor;
import com.wuhao.engine.status.TestStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:59
 */
@Service
public class EngineServiceImpl implements EngineService<TestStates, TestEvents> {

    @Autowired
    private StateMachineStrategyService<TestStates, TestEvents> stateMachineService;



    /**
     * 执行流程引擎
     *
     * @param businessId       业务ID(通常是订单Id)
     * @param businessIdentity 业务标识(针对不同的业务标识会选择不同流程处理)
     * @param event            事件
     * @param engineContext    流程上下文环境
     */
    @Override
    public void execute(String businessId, String businessIdentity, TestEvents event, EngineContext engineContext) {
        StateMachine stateMachine = null;
        try {
            /** 1 根据不同的业务场景获取不同流程状态机，每个订单+业务对应唯一个状态机 **/
            stateMachine = stateMachineService.acquireStateMachine(businessId, businessIdentity, engineContext, true);
            /** 2 构造消息 **/
            Message<TestEvents> message = bulidMessage(businessId, businessIdentity, event, engineContext);
            /** 3 触发事件 **/
            stateMachine.sendEvent(message);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            /** 4 清理状态机 **/
            stateMachineService.releaseStateMachine(String.valueOf(businessId), true);
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
    public Message<TestEvents> bulidMessage(String businessId, String businessIdentity, TestEvents event, EngineContext engineContext) {
        MessageBuilder<TestEvents> messageBuilder = MessageBuilder
                .withPayload(event)
                .setHeader("businessId", businessId)
                .setHeader("businessIdentity", businessIdentity)
                .setHeader("engineContext", engineContext);
        return messageBuilder.build();
    }
}
