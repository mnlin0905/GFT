package com.linktech.gft.bean

/**
 * function : 判断是否是国内的 ip
 *
 * Created on 2019/7/4  16:59
 * @author mnlin
 */
interface IpAddressInterface {
    /**
     * 是否是国内大陆ip地址
     */
    val isChinaInner: Boolean
}

/**
 * function : ip地址解析信息
 *
 * Created on 2019/7/3  16:46
 * @author mnlin
 */
data class IpAddressResponse(
        var data: Data? = Data(),
        var code: Int = 0
) {
    data class Data(
            var area: String? = null,
            var area_id: String? = null,
            var city: String? = null,
            var city_id: String? = null,
            var country: String? = null,
            var country_id: String? = null,
            var county: String? = null,
            var county_id: String? = null,
            var ip: String? = null,
            var isp: String? = null,
            var isp_id: String? = null,
            var region: String? = null,
            var region_id: String? = null
    ) : IpAddressInterface {
        /**
         * 是否是中国大陆
         */
        override val isChinaInner: Boolean
            get() = "中国" == country && region != "香港" && region != "台湾" && region != "澳门"
    }
}

/**
 * function : 聚合返回的ip地址信息
 *
 * Created on 2019/7/4  16:58
 * @author mnlin
 */
data class JuHeIpAddress(
        var data: Data? = Data(),
        var massagesEn: String?,
        var messages: String?,
        var stateCode: Int?
) {
    data class Data(
            private var City: String? = null,
            var Country: String? = null,
            var Isp: String? = null,
            var Province: String? = null
    ) : IpAddressInterface {
        /**
         * 是否是中国大陆
         */
        override val isChinaInner: Boolean
            get() = "中国" == Country && Province != "香港" && Province != "台湾" && Province != "澳门"
    }
}