package com.wuhao.engine.common;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 13:43
 */
@Slf4j
@Data
public class ExecuteAction implements Action<TestStates, TestEvents> {

    public String actionName;

    public Boolean isSuccess=false;

    public ExecuteAction(String actionName){
        this.actionName=actionName;
    }

    @Override
    public void execute(StateContext<TestStates, TestEvents> context) {
        log.warn(String.format("%s execute", actionName));
        isSuccess=true;
    }
}
