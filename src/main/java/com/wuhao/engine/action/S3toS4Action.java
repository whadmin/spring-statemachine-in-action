package com.wuhao.engine.action;


import com.wuhao.engine.event.TestEvents;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:09
 */
@Component("s3tos4Action")
public class S3toS4Action extends AbstractAction {


    @Override
    public void executeInternal(MessageHeaders messageHeaders) {
        System.out.println("S3toS4Action");
    }

    @Override
    public TestEvents getNextEvent() {
        return TestEvents.E4;
    }
}
