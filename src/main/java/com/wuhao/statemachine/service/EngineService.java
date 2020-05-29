package com.wuhao.statemachine.service;

import com.wuhao.statemachine.context.EngineContext;
import com.wuhao.statemachine.event.OrderEvent;

/**
 * @Author: wuhao.w
 * @Date: 2020/1/23 11:09
 *
 * 流程引擎执行接口
 */
public interface EngineService {


    /**
     * 执行流程引擎
     * @param businessId 业务ID(通常是订单Id)
     * @param businessIdentity 业务标识(针对不同的业务标识会选择不同流程处理)
     * @param event  事件
     * @param engineContext 流程上下文环境
     */
    public void execute(Long businessId, String businessIdentity, OrderEvent event, EngineContext<Object> engineContext);
}
