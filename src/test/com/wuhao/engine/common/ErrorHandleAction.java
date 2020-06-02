package com.wuhao.engine.common;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.Data;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 14:24
 */
@Data
public class ErrorHandleAction implements Action<TestStates, TestEvents> {

    public String actionName;

    public Exception exception;

    public Boolean isExecute = false;

    public ErrorHandleAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        System.err.println(String.format("%s 处理异常", actionName));
        exception = context.getException();
        isExecute = true;
    }

    public void reset() {
        isExecute = false;
        exception = null;
    }
}
