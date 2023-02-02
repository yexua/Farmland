package com.harvest.oms.service.order.task;

import com.harvest.oms.service.order.task.stat.OrderLogisticTrackBackTask;
import com.harvest.oms.service.order.task.stat.OrderStockLackBackTask;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @Author: Alodi
 * @Date: 2023/2/1 8:37 PM
 * @Description: TODO
 **/
@Component
public class OrderBackStatTaskContext implements OrderBackStatTask, InitializingBean {

    /**
     * 订单物流追踪
     */
    private AbstractBackTaskProcessor LogisticsTrackingBackTaskProcessor;

    /**
     * 缺货
     */
    private AbstractBackTaskProcessor StockLackBackTaskProcessor;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogisticsTrackingBackTaskProcessor = new AbstractBackTaskProcessor("logistics-tracking-task", 10, 10) {
            @Override
            protected Callable<Boolean> getTask(long companyId) {
                return new OrderLogisticTrackBackTask(companyId);
            }
        };
        StockLackBackTaskProcessor = new AbstractBackTaskProcessor("stock-lack-task", 10, 10) {
            @Override
            protected Callable<Boolean> getTask(long companyId) {
                return new OrderStockLackBackTask(companyId);
            }
        };
    }

    @Override
    public void StockLackStat(long companyId) {
        StockLackBackTaskProcessor.execute(companyId);
    }

    @Override
    public void LogisticsTracking(long companyId) {
        LogisticsTrackingBackTaskProcessor.execute(companyId);
    }
}
