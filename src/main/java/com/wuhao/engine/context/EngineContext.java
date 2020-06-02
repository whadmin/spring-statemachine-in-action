package com.wuhao.engine.context;



import com.wuhao.engine.status.TestStates;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineContext {

    private TestStates source;

    private TestStates target;
}
