package com.harvest.oms.service.order.handler.feature.platform;

import com.harvest.core.enums.oms.OrderSourceEnum;
import com.harvest.oms.domain.order.OrderInfoDO;
import com.harvest.oms.domain.order.platform.OrderPlatformFeature;
import com.harvest.oms.domain.order.platform.PlatformFeature_Taobao;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Author: Alodi
 * @Date: 2023/1/28 4:34 PM
 * @Description:
 **/
@Component
public class OrderPlatformFeature_TaobaoHandler extends AbstractOrderPlatformFeatureHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderPlatformFeature_TaobaoHandler.class);

    private final static OrderSourceEnum TAOBAO = OrderSourceEnum.Taobao;

    @Override
    public void batchFeatureFill(Long companyId, Collection<OrderInfoDO> orders) {
        Collection<OrderInfoDO> filter = this.filter(companyId, TAOBAO, orders);
        if (CollectionUtils.isEmpty(filter)) {
            return;
        }

        filter.forEach(order -> {
            OrderPlatformFeature<PlatformFeature_Taobao> orderPlatformFeature = new OrderPlatformFeature<>();
            PlatformFeature_Taobao feature_Taobao = new PlatformFeature_Taobao();
            feature_Taobao.setOrderSource(TAOBAO);
            feature_Taobao.setCompanyId(companyId);
            orderPlatformFeature.setFeature(feature_Taobao);
            order.setPlatformFeature(orderPlatformFeature);
        });
    }
}
