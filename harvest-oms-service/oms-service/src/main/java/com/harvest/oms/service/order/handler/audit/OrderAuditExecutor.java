package com.harvest.oms.service.order.handler.audit;

import com.harvest.core.context.SpringHelper;
import com.harvest.core.enums.oms.OrderStatusEnum;
import com.harvest.core.service.mq.ProducerMessageService;
import com.harvest.oms.client.order.OrderWriteClient;
import com.harvest.oms.domain.order.OrderInfoDO;
import com.harvest.oms.domain.order.audit.OrderAuditTransferDTO;
import com.harvest.oms.enums.OrderEventEnum;
import com.harvest.oms.request.order.audit.SubmitAuditRequest;
import com.harvest.oms.request.order.warehouse.SubmitWmsOrderMessage;
import com.harvest.oms.service.order.event.OrderEventPublisher;
import com.harvest.oms.service.order.processor.OrderAuditProcessor;
import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Alodi
 * @Date: 2023/2/16 3:44 PM
 * @Description: TODO
 **/
@Component
public class OrderAuditExecutor implements OrderAuditProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderAuditExecutor.class);

    @Autowired
    private OrderWriteClient orderWriteClient;

    @Autowired
    private ProducerMessageService producerMessageService;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    /**
     * 执行处理
     *
     * @param companyId
     * @param request
     */
    public void exec(Long companyId, SubmitAuditRequest request) {
        this.execute(companyId, request);
    }

    @Override
    public void check(Long companyId, SubmitAuditRequest request) {
        if (request.isForce()) {
            // 强制审核不检查数据问题
            return;
        }
        SpringHelper.getBean(OrderAuditCheckExecutor.class).check(request.getOrder());
    }

    @Override
    public OrderAuditTransferDTO beforeAudit(Long companyId, SubmitAuditRequest request) {
        OrderInfoDO order = request.getOrder();
        order.setOrderStatus(OrderStatusEnum.FINANCE_APPROVE);

        OrderAuditTransferDTO transfer = new OrderAuditTransferDTO();
        transfer.setContinue(true);
        return transfer;
    }

    @Override
    public void processAudit(Long companyId, SubmitAuditRequest request, OrderAuditTransferDTO transfer) {
        OrderInfoDO order = request.getOrder();

        orderWriteClient.updateOrderStatus(companyId, order);
    }

    @Override
    public void afterAudit(Long companyId, SubmitAuditRequest request) {
        SendResult sendResult = this.pushWms(companyId, request);
        // 发布订单审核事件
        orderEventPublisher.publishAsync(companyId, request.getOrder().getOrderId(), OrderEventEnum.AUDIT);
    }

    private SendResult pushWms(Long companyId, SubmitAuditRequest request) {
        // 推送wms
        SubmitWmsOrderMessage submitWmsOrderMessage = new SubmitWmsOrderMessage();
        submitWmsOrderMessage.setCompanyId(companyId);
        submitWmsOrderMessage.setOrder(request.getOrder());
        return producerMessageService.syncSend("my-topic", submitWmsOrderMessage);
    }
}
