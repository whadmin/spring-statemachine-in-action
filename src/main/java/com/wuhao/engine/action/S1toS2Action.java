package com.wuhao.engine.action;


import com.wuhao.engine.event.TestEvents;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:09
 */
@Component("s1tos2Action")
public class S1toS2Action extends AbstractAction {


    @Override
    public void executeInternal(MessageHeaders messageHeaders) {
      System.out.println("S1toS2Action");
    }

    @Override
    public TestEvents getNextEvent() {
        return TestEvents.E2;
    }
}
