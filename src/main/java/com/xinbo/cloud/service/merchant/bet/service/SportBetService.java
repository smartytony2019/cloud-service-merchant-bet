package com.xinbo.cloud.service.merchant.bet.service;

import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.vo.PageVo;
import com.xinbo.cloud.common.vo.sport.SportBetSearchVo;
import com.xinbo.cloud.service.merchant.bet.service.fallback.SportBetServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 汉斯
 * @date 2020/3/23 13:17
 * @desc 商户信息接口
 */
@FeignClient(name = "cloud-service-sport-bet", fallback = SportBetServiceFallback.class)
public interface SportBetService {
    /**
     * 通过用户名称和数据接口获取用户信息
     * @param pageVo
     * @return
     */
    @PostMapping("/gw-sport-bet/merchant-sport-bet/getBetPageList")
    ActionResult getBetPageList(@RequestBody PageVo<SportBetSearchVo> pageVo);
}
