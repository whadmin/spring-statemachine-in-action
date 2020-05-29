package com.wuhao.statemachine.context;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineContext<T> {

    private T payload;
}
