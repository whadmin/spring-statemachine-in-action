package com.wuhao.engine.listener.listener;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/1 16:51
 */

import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.event.StateMachineEvent;

import java.util.concurrent.CountDownLatch;


public class StateMachineApplicationEventListener implements ApplicationListener<StateMachineEvent> {
    public volatile CountDownLatch latch = new CountDownLatch(1);
    public volatile int count = 0;

    @Override
    public void onApplicationEvent(StateMachineEvent event) {
        System.err.println("触发事件：" + event);
        count++;
        latch.countDown();
    }

    public void reset() {
        count = 0;
        latch = new CountDownLatch(1);
    }
}
