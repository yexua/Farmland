package com.harvest.logistics.cainiao.client;

import com.harvest.basic.domain.logistics.DeclarationDataFile;
import com.harvest.basic.domain.logistics.DeclarationResponse;
import com.harvest.core.annotation.feign.HarvestService;
import com.harvest.logistics.cainiao.HarvestCainiaoLogisticsApplications;
import com.harvest.oms.request.order.declare.SubmitDeclarationRequest;

/**
 * @Author: Alodi
 * @Date: 2023/2/4 2:27 PM
 * @Description: TODO
 **/
@HarvestService(path = HarvestCainiaoLogisticsApplications.SERVICE_PATH)
public class PlatformCainiaoLogisticsClientImpl implements PlatformCainiaoLogisticsClient {

    @Override
    public void getToken(Long companyId) {
        System.out.println("获取授权");
    }

    @Override
    public void refreshToken(Long companyId) {
        System.out.println("刷新授权");
    }

    @Override
    public void validAccount(Long companyId) {
        System.out.println("验证账户");
    }

    @Override
    public DeclarationResponse submitDeclaration(Long companyId, SubmitDeclarationRequest request) {
        System.out.println("菜鸟申报");
        return null;
    }

    @Override
    public void getDeliveryNo(Long companyId, SubmitDeclarationRequest request) {
        System.out.println("获取运单号");
    }

    @Override
    public DeclarationDataFile print(Long companyId, SubmitDeclarationRequest request) {
        return null;
    }

    @Override
    public void cancelDeclaration(Long companyId, SubmitDeclarationRequest request) {
        System.out.println("取消申报");
    }

    @Override
    public void queryPickTime(Long companyId) {
        System.out.println("查询揽收时间");

    }

}
