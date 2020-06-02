package com.wuhao.engine.persister;

import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.status.TestStates;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 12:37
 */
@Repository
public class InMemoryStateMachinePersist implements StateMachinePersist<TestStates, TestEvents, String> {

    private final HashMap<String, StateMachineContext<TestStates, TestEvents>> contexts = new HashMap<>();

    @Override
    public void write(StateMachineContext<TestStates, TestEvents> context, String contextObj) throws Exception {
        contexts.put(contextObj, context);
    }

    @Override
    public StateMachineContext<TestStates, TestEvents> read(String contextObj) throws Exception {
        return contexts.get(contextObj);
    }
}
