package com.wuhao.engine.service;

import com.wuhao.engine.context.EngineContext;
import org.springframework.statemachine.StateMachine;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 14:06
 * <p>
 * 参考Spring状态机StateMachineService修改
 */
public interface StateMachineStrategyService<S, E> {

    public StateMachine<S, E> acquireStateMachine(String businessId, String businessIdentity,EngineContext engineContext);

    public StateMachine<S, E> acquireStateMachine(String businessId, String businessIdentit, EngineContext engineContext, boolean start);

    public void releaseStateMachine(String machineId);

    public void releaseStateMachine(String machineId, boolean stop);
}
