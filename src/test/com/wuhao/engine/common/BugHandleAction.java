package com.wuhao.engine.common;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 14:24
 */
@Slf4j
@Data
public class BugHandleAction implements Action<TestStates, TestEvents> {

    public String actionName;

    public Exception exception;


    public BugHandleAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        log.warn(String.format("%s 处理异常", actionName));
        exception = context.getException();
    }

    public void reset() {
        exception = null;
    }
}
