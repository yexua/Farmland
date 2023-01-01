package com.harvest.oms.repository.client.rich;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.harvest.core.annotation.feign.HarvestService;
import com.harvest.core.domain.Page;
import com.harvest.core.utils.JsonUtils;
import com.harvest.oms.repository.client.order.rich.OrderRichQueryRepositoryClient;
import com.harvest.oms.repository.constants.HarvestOmsRepositoryApplications;
import com.harvest.oms.repository.domain.order.simple.OrderSimplePO;
import com.harvest.oms.repository.handler.order.OrderSectionRepositoryHandler;
import com.harvest.oms.repository.mapper.order.rich.OrderRichConditionQueryMapper;
import com.harvest.oms.repository.query.order.PageOrderConditionQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: Alodi
 * @Date: 2022/12/24 4:48 PM
 * @Description: TODO
 **/
@HarvestService(value = HarvestOmsRepositoryApplications.SERVICE_NAME, path = HarvestOmsRepositoryApplications.Path.ORDER_RICH)
public class OrderRichQueryRepositoryClientImpl implements OrderRichQueryRepositoryClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderRichQueryRepositoryClientImpl.class);

    /**
     * 查询超时时常警告
     */
    private final static long TIME_OUT = 1000L * 5;

    /**
     * 如果订单数超过200则采用并发查询，提高查询效率
     */
    private final static int ORDER_NUMS = 200;

    /**
     * 订单扩展信息查询线程池
     */
    private final static Executor OMS_SECTION_READ_EXECUTOR =
            new ThreadPoolExecutor(100, 100, 2000, TimeUnit.MILLISECONDS,
                    new SynchronousQueue<>(),
                    new ThreadFactoryBuilder()
                            .setNameFormat("harvest-oms-section-reading-%d")
                            .setUncaughtExceptionHandler((thread, e) -> LOGGER.error("ThreadPool:{} 发生异常", thread, e))
                            .build(),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    @Autowired
    private OrderRichConditionQueryMapper orderRichConditionQueryMapper;

    @Autowired
    private List<OrderSectionRepositoryHandler<?>> orderSectionRepositoryHandlers;

    @Override
    public Page<OrderSimplePO> pageQueryOrderRich(Long companyId, PageOrderConditionQuery condition) {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start("简化查询结构");
        Map<String, Object> paramsMap = this.conventParams(condition);
        stopWatch.stop();

        stopWatch.start("订单总数查询");
        Long count = orderRichConditionQueryMapper.countQueryOrderWithRichCondition(paramsMap);
        stopWatch.stop();

        Page<OrderSimplePO> page = new Page<>(condition.getPageNo(), condition.getPageSize());
        page.setCount(count);
        if (count == 0) {
            LOGGER.warn("订单查询为空, companyId:{}, condition:{}, \nstopWatch:{}", companyId, JsonUtils.object2Json(condition), stopWatch.prettyPrint());
            return page;
        }

        stopWatch.start("订单查询");
        Collection<OrderSimplePO> orders = orderRichConditionQueryMapper.pageQueryOrderWithRichCondition(paramsMap);
        page.setData(orders);
        stopWatch.stop();

        stopWatch.start("补充信息填充");
        this.sectionBatchFill(companyId, orders);
        stopWatch.stop();

        if (stopWatch.getTotalTimeMillis() > TIME_OUT) {
            LOGGER.warn("订单查询超时, companyId:{}, condition:{}, \nstopWatch:{}", companyId, JsonUtils.object2Json(condition), stopWatch.prettyPrint());
        }

        return page;
    }

    /**
     * 信息填充
     *
     * @param companyId
     * @param orders
     */
    private void sectionBatchFill(Long companyId, Collection<OrderSimplePO> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return;
        }

        if (orders.size() >= ORDER_NUMS) {
            try {
                CompletableFuture<?>[] futures = new CompletableFuture<?>[orderSectionRepositoryHandlers.size() + 1];
                for (int i = 0; i < orderSectionRepositoryHandlers.size(); i++) {
                    int finalI = i;
                    futures[i] = CompletableFuture.runAsync(() -> orderSectionRepositoryHandlers.get(finalI).batchFill(companyId, orders),
                            OMS_SECTION_READ_EXECUTOR);
                }
                futures[orderSectionRepositoryHandlers.size()] =
                        CompletableFuture.runAsync(() -> {
                        }, OMS_SECTION_READ_EXECUTOR);
                CompletableFuture.allOf(futures).get();
            } catch (Exception e) {
                LOGGER.error("并发补充订单信息失败", e);
            }
        } else {
            orderSectionRepositoryHandlers.forEach(section -> section.batchFill(companyId, orders));
        }
    }

    /**
     * 简化查询结构
     *
     * @param condition
     * @return
     */
    private Map<String, Object> conventParams(PageOrderConditionQuery condition) {
        Map<String, Object> paramsMap = Maps.newHashMap();

        if (StringUtils.isNotEmpty(condition.getOrderNo())) {
            paramsMap.put("orderNo", condition.getOrderNo());
        }

        return paramsMap;
    }
}
