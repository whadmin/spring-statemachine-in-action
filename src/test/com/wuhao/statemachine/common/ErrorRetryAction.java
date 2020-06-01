package com.wuhao.statemachine.common;

import com.wuhao.statemachine.status.TestEvents;
import com.wuhao.statemachine.status.TestStates;
import lombok.Data;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 14:04
 */
@Data
public class ErrorRetryAction implements Action<TestStates, TestEvents> {

    public String actionName;

    public Boolean isSuccess = false;

    public Boolean isExecute = false;

    public ErrorRetryAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        isExecute=true;
        Boolean retry = context.getMessageHeaders().get("retry", Boolean.class);
        if (retry==null||!retry) {
            System.err.println(String.format("%s 抛出异常", actionName));
            throw new RuntimeException("Fake Error");
        } else {
            System.err.println(String.format("%s execute", actionName));
            isSuccess = true;
        }
    }
}
