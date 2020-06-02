package com.wuhao.engine.context;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineContext<S> {

    private S source;

    private S target;
}
