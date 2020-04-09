package com.xinbo.cloud.service.merchant.bet.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.PageDto;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.common.dto.common.MerchantDto;
import com.xinbo.cloud.common.dto.sport.MerchantSportBetDto;
import com.xinbo.cloud.common.enums.PlatGameTypeEnum;
import com.xinbo.cloud.common.service.api.*;
import com.xinbo.cloud.common.vo.PageVo;
import com.xinbo.cloud.common.vo.merchanta.api.QueryRequestVo;
import com.xinbo.cloud.common.vo.sport.SportBetSearchVo;
import com.xinbo.cloud.service.merchant.bet.common.PlatformBetCommon;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @author 汉斯
 * @date 2020/3/19 12:03
 * @desc 商户注单查询接口
 */
@RestController
@RequestMapping("platformBet")
public class PlatformBetController {

    @Reference(version = "1.0.0", mock = "com.xinbo.cloud.common.service.mock.MerchantServiceMock")
    private MerchantServiceApi merchantServiceApi;

    @Reference(version = "1.0.0", mock = "com.xinbo.cloud.common.service.mock.SportBetServiceMock")
    private SportBetServiceApi sportBetServiceApi;

    @ApiOperation(value = "查询注单", notes = "")
    @PostMapping("query")
    public ActionResult query(@Valid @RequestBody QueryRequestVo queryRequestVo) {
        try {
            //Step 1: 验证渠道号
            MerchantDto merchant = PlatformBetCommon.validateMerchant(merchantServiceApi, queryRequestVo.getChannel());
            //Step 2: 验证签名
            PlatformBetCommon.validateSign(queryRequestVo, merchant.getMerchantKey());
            //Step 3: 验证请求时间
            Date startDate = DateUtil.parse(queryRequestVo.getStartTime());
            Date endDate = DateUtil.parse(queryRequestVo.getEndTime());
            if (startDate.compareTo(endDate) > 0)
                return ResultFactory.error("查询时间有误,开始时间不能大于结束时间");
            if (endDate.compareTo(DateUtil.offsetDay(startDate, 3)) > 0)
                return ResultFactory.error("查询时间有误,查询时间间隔不得超过3天");
            SportBetSearchVo sportBetSearchVo = SportBetSearchVo.builder().dataNode(merchant.getDataNode()).merchantCode(merchant.getMerchantCode())
                    .startTimes(startDate).endTime(endDate).build();
            if (StrUtil.isNotBlank(queryRequestVo.getUsername())) {
                sportBetSearchVo.setUserName(MessageFormat.format("{0}_{1}", merchant.getMerchantCode(), queryRequestVo.getUsername()));
            }
            PageVo<SportBetSearchVo> pageVo = PageVo.<SportBetSearchVo>builder().pageNum(queryRequestVo.getPageIndex()).pageSize(queryRequestVo.getPageSize())
                    .model(sportBetSearchVo).build();
            if (Integer.parseInt(queryRequestVo.getGameId()) == PlatGameTypeEnum.Sport.getCode()) {
                PageDto<MerchantSportBetDto> list = sportBetServiceApi.getBetPageList(pageVo);
                return ResultFactory.success(list);
            }
            //彩票暂时没有处理
            if (Integer.parseInt(queryRequestVo.getGameId()) == PlatGameTypeEnum.Lottery.getCode()) {
                return ResultFactory.success();
            }
            return ResultFactory.error("游戏ID不存在");
        } catch (Exception ex) {
            return ResultFactory.error(ex.getMessage());
        }
    }
}
