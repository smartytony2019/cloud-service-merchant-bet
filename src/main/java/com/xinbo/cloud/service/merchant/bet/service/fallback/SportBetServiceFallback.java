package com.xinbo.cloud.service.merchant.bet.service.fallback;

import com.xinbo.cloud.common.constant.FallbackMessage;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.common.vo.PageVo;
import com.xinbo.cloud.common.vo.sport.SportBetSearchVo;
import com.xinbo.cloud.service.merchant.bet.service.SportBetService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @author 汉斯
 * @date 2020/3/31 15:07
 * @desc 熔断器
 */
@Component
public class SportBetServiceFallback implements SportBetService {
    @Override
    public ActionResult getBetPageList(PageVo<SportBetSearchVo> pageVo){
        return ResultFactory.fallback(MessageFormat.format(FallbackMessage.MSG_FORMAT, SportBetService.class.getSimpleName()));
    }
}
