package com.wuhao.engine.action;


import com.wuhao.engine.event.TestEvents;
import lombok.Data;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:09
 */
@Component("s2tos3Action")
public class S2toS3Action extends AbstractAction {


    @Override
    public void executeInternal(MessageHeaders messageHeaders) {
        System.out.println("S2toS3Action");
    }

    @Override
    public TestEvents getNextEvent() {
        return TestEvents.E3;
    }
}
