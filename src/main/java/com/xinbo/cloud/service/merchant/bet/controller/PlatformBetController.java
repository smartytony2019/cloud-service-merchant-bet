package com.xinbo.cloud.service.merchant.bet.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.xinbo.cloud.common.domain.common.Merchant;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.common.service.merchant.api.PlatformApiService;
import com.xinbo.cloud.common.vo.merchanta.api.PlatformApiRequestVo;
import com.xinbo.cloud.common.vo.merchanta.bet.QueryRequestVo;
import com.xinbo.cloud.service.merchant.bet.common.PlatformApiCommon;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author 汉斯
 * @date 2020/3/19 12:03
 * @desc file desc
 */
public class PlatformBetController {
    @Autowired
    @Qualifier("platformApiServiceImpl")
    private PlatformApiService platformApiService;

    @ApiOperation(value = "查询余额", notes = "")
    @PostMapping("queryBalance")
    public ActionResult queryBalance(@Valid @RequestBody QueryRequestVo queryRequestVo) {

        //Step 1: 验证渠道号
        Merchant merchant = platformApiService.getByMerchantCode(queryRequestVo.getChannel());
        if (merchant == null) {
            return ResultFactory.error("渠道不存在");
        }

        //Step 2: 验证签名
        boolean isValidate = PlatformApiCommon.validateSign(queryRequestVo, merchant.getMerchantKey());
        if (!isValidate) {
            return ResultFactory.error("验证签名失败");
        }

        Date startDate = DateUtil.parse(queryRequestVo.getStartTime());
        Date endDate = DateUtil.parse(queryRequestVo.getEndTime());
        if (startDate.compareTo(endDate)>0 )
            return ResultFactory.error("查询时间有误,开始时间不能大于结束时间");
        long betweenDay = DateUtil.between(startDate, endDate, DateUnit.DAY);
        if (betweenDay > 3)
            return ResultFactory.error("查询时间有误,查询时间间隔不得超过3天");



        return ResultFactory.success();
    }
}
