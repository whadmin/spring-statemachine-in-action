package com.wuhao.engine.service;

import com.wuhao.engine.context.EngineContext;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/02 11:09
 * <p>
 * 流程引擎执行接口
 */
public interface EngineService<S,E> {


    /**
     * 执行流程引擎
     * @param businessId 业务ID(通常是订单Id)
     * @param businessIdentity 业务标识(针对不同的业务标识会选择不同流程处理)
     * @param event  事件
     * @param engineContext 流程上下文环境
     */
    public void execute(String businessId, String businessIdentity, E event, EngineContext engineContext);
}
