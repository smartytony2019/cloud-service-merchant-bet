package com.xinbo.cloud.service.merchant.bet.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.xinbo.cloud.common.dto.common.MerchantDto;
import com.xinbo.cloud.common.service.api.MerchantServiceApi;
import com.xinbo.cloud.common.utils.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author 汉斯
 * @date 2020/3/23 13:40
 * @desc 商户拉到注取接口通用方法类
 */
@Slf4j
public class PlatformBetCommon {
    /**
     * 验证签名
     *
     * @param obj
     * @param merchantKey
     * @return
     */
    public static void validateSign(Object obj, String merchantKey) {
        Map map = MapperUtil.to(obj, Map.class);
        String strSign = MapUtil.getStr(map, "sign");
        map.remove("sign");
        String str = StringUtils.join(MapUtil.sort(map).values(), "");
        str += merchantKey;
        String strMd5 = DigestUtil.md5Hex(str).toLowerCase();
        if (!strSign.equals(strMd5)) {
            log.debug(MessageFormat.format("验证签名失败,签名字符串：{0},签名结果：{1},第三方签名串:{2}",str,strMd5,strSign));
            throw new RuntimeException("验证签名失败");
        }
    }

    /**
     * 验证商户是否存在，并返回商户信息
     *
     * @param merchantServiceApi
     * @param merchantCode
     * @return
     */
    public static MerchantDto validateMerchant(MerchantServiceApi merchantServiceApi, String merchantCode) {
        MerchantDto dto = merchantServiceApi.getByMerchantCode(merchantCode);
        if (dto == null)
            throw new RuntimeException("渠道不存在");
        return dto;
    }
}
