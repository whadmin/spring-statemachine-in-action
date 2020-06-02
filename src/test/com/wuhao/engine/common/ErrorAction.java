package com.wuhao.engine.common;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.Data;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 15:40
 */
@Data
public class ErrorAction implements Action<TestStates, TestEvents> {

    public String actionName;

    public Boolean isSuccess = false;

    public Boolean isExecute = false;

    public ErrorAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        isExecute = true;
        System.err.println(String.format("%s 抛出异常", actionName));
        throw new RuntimeException("Fake Error");
    }
}
