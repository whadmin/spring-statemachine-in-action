package com.wuhao.statemachine.status;

/**
 * 订单状态
 */
public enum OrderStatus {
    /**
     * 待创建
     **/
    WAIT_CREATE,
    /**
     * 待付款
     **/
    WAIT_PAYMENT,
    /**
     * 付款中
     **/
    PAYMENT_ING,
    /**
     * 已付款待发货
     **/
    WAIT_DELIVER,
    /**
     * 已发货待收货
     **/
    WAIT_RECEIVE,
    /**
     * 订单完成
     **/
    FINISH;

    /**
     * 支付状态（1 待付款，2已付款）
     **/
    private Byte payStatus;

    /**
     * 物流状态（1 待发货，2已发货）
     **/
    private Byte logisticsStatus;

    /**
     * 是否完结（1 进行中，2已完结）
     **/
    private byte isFinish;


    OrderStatus() {

    }

    OrderStatus(Integer payStatus, Integer logisticsStatus, Integer isFinish) {
        this.payStatus = payStatus.byteValue();
        this.logisticsStatus = logisticsStatus.byteValue();
        this.isFinish = isFinish.byteValue();
    }
}
