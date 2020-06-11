package com.wuhao.engine.listener.listener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.event.StateMachineEvent;

import java.util.concurrent.CountDownLatch;

/**
 * 使用Spring事件监听器处理触发的事件
 * 设置 @EnableStateMachine(contextEvents = false) 将关闭监听功能
 *
 * @Author: wuhao.w
 * @Date: 2020/6/1 16:51
 */
@Slf4j
public class StateMachineApplicationEventListener implements ApplicationListener<StateMachineEvent> {
    public volatile CountDownLatch latch = new CountDownLatch(1);
    public volatile int count = 0;

    @Override
    public void onApplicationEvent(StateMachineEvent event) {
        log.warn("触发事件：" + event);
        count++;
        latch.countDown();
    }

    public void reset() {
        count = 0;
        latch = new CountDownLatch(1);
    }
}
