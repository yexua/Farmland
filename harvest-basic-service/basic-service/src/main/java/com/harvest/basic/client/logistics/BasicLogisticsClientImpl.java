package com.harvest.basic.client.logistics;

import com.harvest.basic.client.constants.HarvestBasicApplications;
import com.harvest.basic.service.logistics.PlatformLogisticsService;
import com.harvest.core.annotation.feign.HarvestService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Alodi
 * @Date: 2023/2/3 2:48 PM
 * @Description: 物流驱动
 **/
@HarvestService(path = HarvestBasicApplications.Path.LOGISTICS)
public class BasicLogisticsClientImpl implements BasicLogisticsClient {

    @Autowired
    private PlatformLogisticsService platformLogisticsService;

    @Override
    public void submitDeclaration(long companyId) {
        platformLogisticsService.submitDeclaration(companyId);
    }
}
