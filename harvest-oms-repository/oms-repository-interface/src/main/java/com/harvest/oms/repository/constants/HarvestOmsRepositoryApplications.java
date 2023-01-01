package com.harvest.oms.repository.constants;

/**
 * @Author: Alodi
 * @Date: 2022/12/24 5:18 PM
 * @Description: TODO
 **/
public interface HarvestOmsRepositoryApplications {

    String SERVICE_NAME = "harvest-oms-repository";

    interface Path {

        String ORDER_RICH = "/OrderRichQueryRepositoryClient";
        String ORDER_READ = "/OrderReadRepositoryClient";

    }
}
