package com.wuhao.engine.action;

import com.wuhao.engine.event.TestEvents;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:24
 */
@Component("s4tos5Action")
public class S4toS5Action extends AbstractAction {


    @Override
    public void executeInternal(MessageHeaders messageHeaders) {
        System.out.println("S4toS5Action");
    }

    @Override
    public TestEvents getNextEvent() {
        return null;
    }
}