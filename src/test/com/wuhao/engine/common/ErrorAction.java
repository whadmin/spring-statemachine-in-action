package com.wuhao.engine.common;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 15:40
 */
@Slf4j
@Data
public class ErrorAction implements Action<TestStates, TestEvents> {

    public String actionName;

    public ErrorAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        log.error(String.format("%s 抛出异常", actionName));
        throw new RuntimeException("Fake Error");
    }
}
