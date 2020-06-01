package com.wuhao.statemachine.listener.listener;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/1 16:51
 */

import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.event.StateMachineEvent;


public class StateMachineApplicationEventListener implements ApplicationListener<StateMachineEvent> {

    @Override
    public void onApplicationEvent(StateMachineEvent event) {
        System.err.println("触发事件："+event);
    }
}
