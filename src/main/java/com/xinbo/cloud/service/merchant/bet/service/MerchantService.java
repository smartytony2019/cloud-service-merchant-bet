package com.xinbo.cloud.service.merchant.bet.service;

import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.service.merchant.bet.service.fallback.MerchantServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 汉斯
 * @date 2020/3/23 13:17
 * @desc 商户信息接口
 */
@FeignClient(name = "cloud-service-merchant", fallback = MerchantServiceFallback.class)
public interface MerchantService {
    /**
     * 通过用户名称和数据接口获取用户信息
     * @param merchantCode
     * @return
     */
    @PostMapping("/gw-merchant/merchant/getByMerchantCode")
    ActionResult getByMerchantCode(@RequestBody String merchantCode);
}
