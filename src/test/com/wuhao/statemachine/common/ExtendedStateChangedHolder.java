package com.wuhao.statemachine.common;

import lombok.Data;

/**
 * @Author: wuhao.w
 * @Date: 2020/5/29 16:25
 */
@Data
public class ExtendedStateChangedHolder {

    public Object key;
    public Object value;

    public ExtendedStateChangedHolder(Object key, Object value) {
        this.key = key;
        this.value = value;
    }
}
