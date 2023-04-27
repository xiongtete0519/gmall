package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<OrderInfo> {
    //提交订单
    Long submitOrder(OrderInfo orderInfo);

    //生成流水号
    String getTradeNo(String userId);

    //校验流水号
    boolean checkTradeCode(String userId,String tradeNoCode);

    //删除流水号
    void deleteTradeCode(String userId);

    //校验库存
    boolean checkStock(String skuId,String skuNum);

    //我的订单
    IPage<OrderInfo> getOrderPageByUserId(Page<OrderInfo> orderInfoPage, String userId);

    //处理超时订单
    void execExpiredOrder(Long orderId);
    //修改订单状态
    void updateOrderStatus(Long orderId, ProcessStatus closed);
}
