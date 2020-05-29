/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wuhao.statemachine.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.statemachine.StateMachine;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@ShellComponent
public class StateMachineCommands2<TurnstileStatus, TurnstileEvent> {

    @Autowired
    @Qualifier(value = "simple2")
    private StateMachine<TurnstileStatus, TurnstileEvent> stateMachine;

    @Autowired
    @Qualifier("stateChartModel")
    private String stateChartModel;

    @ShellMethod(key = "sm start", value = "启动状态机",group = "simpleStateMachine2")
    public String start() {
        stateMachine.start();
        return "State machine started";
    }

    @ShellMethod(key = "sm event", value = "触发状态机事件",group = "simpleStateMachine2")
    public String event(@ShellOption final TurnstileEvent event) {
        stateMachine.sendEvent(event);
        return "Event " + event + " send";
    }

    @ShellMethod(key = "sm print", value = "打印状态机模型图",group = "simpleStateMachine2")
    public String print() {
        return stateChartModel;
    }

    @ShellMethod(key = "sm stop", value = "关闭状态机",group = "simpleStateMachine2")
    public String stop() {
        stateMachine.stop();
        return "State machine stopped";
    }

    @ShellMethod(key = "sm var", value = "获取状态",group = "simpleStateMachine2")
    public String variables() {
        StringBuilder buf = new StringBuilder();
        Set<Map.Entry<Object, Object>> entrySet = stateMachine.getExtendedState().getVariables().entrySet();
        Iterator<Map.Entry<Object, Object>> iterator = entrySet.iterator();
        if (entrySet.size() > 0) {
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> e = iterator.next();
                buf.append(e.getKey() + "=" + e.getValue());
                if (iterator.hasNext()) {
                    buf.append("\n");
                }
            }
        } else {
            buf.append("No variables");
        }
        return buf.toString();
    }

}