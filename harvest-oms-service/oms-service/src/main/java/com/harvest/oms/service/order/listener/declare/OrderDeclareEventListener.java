package com.harvest.oms.service.order.listener.declare;

import com.harvest.oms.domain.order.OrderInfoDO;
import com.harvest.oms.service.order.listener.OrderEventListener;
import org.springframework.stereotype.Component;

/**
 * @Author: Alodi
 * @Date: 2023/2/9 4:31 PM
 * @Description: TODO
 **/
@Component
public class OrderDeclareEventListener implements OrderEventListener {

    /**
     * 订单申报更新
     *
     * @param companyId 公司id
     * @param order     相关订单
     */
    @Override
    public void declare(Long companyId, OrderInfoDO order) {
        System.out.println("订单申报处理");
    }

}
