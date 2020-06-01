package com.wuhao.statemachine.common;

import com.wuhao.statemachine.status.TestEvents;
import com.wuhao.statemachine.status.TestStates;
import lombok.Data;
import org.springframework.statemachine.state.State;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 16:21
 */
@Data
public class StatesChangeHolder {
    public State<TestStates, TestEvents> from;
    public State<TestStates, TestEvents> to;

    public StatesChangeHolder(State<TestStates, TestEvents> from, State<TestStates, TestEvents> to) {
        this.from = from;
        this.to = to;
    }
}
