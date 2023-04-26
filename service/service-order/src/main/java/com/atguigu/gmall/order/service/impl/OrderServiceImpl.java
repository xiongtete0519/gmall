package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("all")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${ware.url}")
    public String wareUrl;

    /**
     * 提交订单
     * 涉及到的表：
     * order_info 订单详情表 --订单的说明
     * order_detail 订单详情表   --商品的说明
     * <p>
     * 一个订单对应多个订单明细
     * 关联关系：orderDetailList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrderInfo orderInfo) {

        //总金额
        orderInfo.sumTotalAmount();
        //订单状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        //付款方式
        orderInfo.setPaymentWay(PaymentWay.ONLINE.name());
        //订单交易号
        String outTradeNo = "atguigu" + UUID.randomUUID().toString().replaceAll("-", "");
        orderInfo.setOutTradeNo(outTradeNo);
        StringBuilder tradeBody=new StringBuilder();
        //订单描述
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            tradeBody.append(orderDetail.getSkuNum()+"  ");
        }
        //设置订单描述
        if(tradeBody.toString().length()>100){
            orderInfo.setTradeBody(tradeBody.toString().substring(0,100));
        }else{
            orderInfo.setTradeBody(tradeBody.toString());
        }
        //操作时间
        orderInfo.setOperateTime(new Date());
        //失效时间+1天
        Calendar calendar = Calendar.getInstance();
        //天数+1
        calendar.add(Calendar.DATE,1);
        //设置超时时间
        orderInfo.setExpireTime(calendar.getTime());
        //设置订单进度状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());


        //保存订单
        orderInfoMapper.insert(orderInfo);

        //保存订单明细
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            //设置订单id
            orderDetail.setOrderId(orderInfo.getId());

            //添加
            orderDetailMapper.insert(orderDetail);
        }

        //删除购物车 --为了测试这里不删除
        //redisTemplate.delete(RedisConst.USER_KEY_PREFIX+orderInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX);

        //返回订单id
        return orderInfo.getId();
    }

    /**
     * 生成流水号
     * 1、生成后返回页面
     * 2、生成后存储到redis
     * @param userId
     * @return
     */
    @Override
    public String getTradeNo(String userId) {
        String tradeNo=UUID.randomUUID().toString().replaceAll("-","");
        //存储到redis
        String tradeNoKey="user:"+userId+":tradeno";
        //存储
        redisTemplate.opsForValue().set(tradeNoKey,tradeNo);
        return tradeNo;
    }

    //校验流水号
    @Override
    public boolean checkTradeCode(String userId, String tradeNoCode) {
        //获取redis中的tradeNo
        String tradeNoKey="user:"+userId+":tradeno";
        String redisTradeNo = (String) redisTemplate.opsForValue().get(tradeNoKey);
        //判断
        if(StringUtils.isEmpty(redisTradeNo)){
            return false;
        }
        return redisTradeNo.equals(tradeNoCode);
    }

    //删除流水号
    @Override
    public void deleteTradeCode(String userId) {
        //获取redis中的tradeNo
        String tradeNoKey="user:"+userId+":tradeno";
        redisTemplate.delete(tradeNoKey);
    }

    ////校验库存
    @Override
    public boolean checkStock(String skuId, String skuNum) {
        //http请求 http://localhost:9001/hasStock?skuId=22&num=200
        String result = HttpClientUtil.doGet(wareUrl+"/hasStock?skuId="+skuId+"&num="+skuNum);

        return "1".equals(result);
    }

    //我的订单
    @Override
    public IPage<OrderInfo> getOrderPageByUserId(Page<OrderInfo> orderInfoPage, String userId) {
        IPage<OrderInfo> orderInfoIPage = orderInfoMapper.selectOrderPageByUserId(orderInfoPage, userId);
        List<OrderInfo> records = orderInfoIPage.getRecords();
        records.stream().forEach(orderInfo -> {
            //设置订单状态
            orderInfo.setOrderStatusName(OrderStatus.getStatusNameByStatus(orderInfo.getOrderStatus()));
        });
        return orderInfoPage;
    }
}
