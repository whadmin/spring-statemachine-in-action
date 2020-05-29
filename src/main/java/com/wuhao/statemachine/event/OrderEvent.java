package com.wuhao.statemachine.event;


import com.wuhao.statemachine.status.OrderStatus;

/**
 * 订单事件
 */
public enum OrderEvent {

    /**
     * 创建
     */
    CREATE(OrderStatus.WAIT_CREATE),

    /**
     * 支付
     */
    PAYED(OrderStatus.WAIT_PAYMENT),

    /**
     * 发货
     */
    DELIVERY(OrderStatus.WAIT_DELIVER),

    /**
     * 确认收货
     */
    RECEIVED(OrderStatus.WAIT_DELIVER);


    /**
     * 前置状态
     */
    private OrderStatus preOrderStatus;


    OrderEvent(OrderStatus preOrderStatus) {
        this.preOrderStatus = preOrderStatus;
    }

    public OrderStatus getPreOrderStatus() {
        return preOrderStatus;
    }

    public void setPreOrderStatus(OrderStatus preOrderStatus) {
        this.preOrderStatus = preOrderStatus;
    }
}
