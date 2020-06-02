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
package com.wuhao.engine.command;

import com.wuhao.engine.context.EngineContext;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.service.EngineService;
import com.wuhao.engine.status.TestStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;




@ShellComponent
public class StateMachineCommands {

    @Autowired
    private EngineService<TestStates, TestEvents> engineService;

    @Autowired
    @Qualifier("stateChartModel")
    private String stateChartModel;


    /**
     * exe  "15271872494" "default" --event "E2"  --source "S2"  --target "S5"
     * @param businessId
     * @param businessIdentity
     * @param event
     * @param source
     * @param target
     * @return
     */
    @ShellMethod(key = "exe", value = "执行流程")
    public String execute(@ShellOption final String businessId, @ShellOption final String businessIdentity, @ShellOption final TestEvents event, @ShellOption final TestStates source, @ShellOption final TestStates target) {
        EngineContext context = EngineContext.builder().source(source).target(target).build();
        engineService.execute(businessId, businessIdentity, event, context);
        return "execute success";
    }
}