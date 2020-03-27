package com.xinbo.cloud.service.merchant.bet.service.fallback;

import com.xinbo.cloud.common.constant.FallbackMessage;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.service.merchant.bet.service.MerchantServiceInterface;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @author 熊二
 * @date 2020/3/24 21:07
 * @desc 熔断器
 */
@Component
public class MerchantServiceInterfaceFallback implements MerchantServiceInterface {


    @Override
    public ActionResult getByMerchantCode(String merchantCode) {
        return ResultFactory.fallback(MessageFormat.format(FallbackMessage.MSG_FORMAT, MerchantServiceInterface.class.getSimpleName()));
    }

    @Override
    public ActionResult getGameAddressList(int gameType){
        return ResultFactory.fallback(MessageFormat.format(FallbackMessage.MSG_FORMAT, MerchantServiceInterface.class.getSimpleName()));
    }
}
