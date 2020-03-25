package com.xinbo.cloud.service.merchant.bet.controller;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinbo.cloud.common.constant.ApiStatus;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.PageDto;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.common.dto.common.MerchantDto;
import com.xinbo.cloud.common.dto.merchant.api.MerchantSportBetDto;
import com.xinbo.cloud.common.enums.PlatGameTypeEnum;
import com.xinbo.cloud.common.service.merchant.bet.PlatformBetService;
import com.xinbo.cloud.common.vo.merchanta.api.QueryRequestVo;
import com.xinbo.cloud.service.merchant.bet.service.MerchantService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;

/**
 * @author 汉斯
 * @date 2020/3/19 12:03
 * @desc 商户注单查询接口
 */
@RestController
@RequestMapping("platformBet")
public class PlatformBetController {
    @Autowired
    @Qualifier("platformBetServiceImpl")
    private PlatformBetService platformBetService;
    @Autowired
    @SuppressWarnings("all")
    private MerchantService merchantService;

    @ApiOperation(value = "查询注单", notes = "")
    @PostMapping("query")
    public ActionResult query(@Valid @RequestBody QueryRequestVo queryRequestVo) {
        //Step 1: 验证渠道号
        ActionResult merchantActionResult = merchantService.getByMerchantCode(queryRequestVo.getChannel());
        if (merchantActionResult.getCode() == ApiStatus.FALLBACK) {
            return ResultFactory.error(merchantActionResult.getMsg());
        }
        if (merchantActionResult.getCode() != ApiStatus.SUCCESS) {
            return ResultFactory.error("渠道不存在");
        }
        MerchantDto merchant = new ObjectMapper().convertValue(merchantActionResult.getData(), MerchantDto.class);
        //Step 2: 验证签名
        boolean isValidate = platformBetService.validateSign(queryRequestVo, merchant.getMerchantKey());
        if (!isValidate) {
            return ResultFactory.error("验证签名失败");
        }
        //Step 3: 验证请求时间
        Date startDate = DateUtil.parse(queryRequestVo.getStartTime());
        Date endDate = DateUtil.parse(queryRequestVo.getEndTime());
        if (startDate.compareTo(endDate) > 0)
            return ResultFactory.error("查询时间有误,开始时间不能大于结束时间");
        if ( endDate.compareTo(DateUtil.offsetDay(startDate,3)) > 0)
            return ResultFactory.error("查询时间有误,查询时间间隔不得超过3天");

        if (Integer.parseInt(queryRequestVo.getGameId()) == PlatGameTypeEnum.Sport.getCode()) {
            PageDto<MerchantSportBetDto> list = platformBetService.getSportBetList(queryRequestVo);
            return ResultFactory.success(list);
        }
        if (Integer.parseInt(queryRequestVo.getGameId()) == PlatGameTypeEnum.Lottery.getCode()) {

            return ResultFactory.success();
        }
        return ResultFactory.error("游戏ID不存在");
    }
}
