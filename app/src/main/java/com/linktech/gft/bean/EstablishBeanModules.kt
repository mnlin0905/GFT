package com.linktech.gft.bean

/**************************************
 * function : 开户模块使用的bean
 *
 * Created on 2019/7/19  15:23
 * @author mnlin
 **************************************/

/**
 * function : 上传正反照片返回值
 *
 * [address]	Y	String	户籍地址
 * [nationality]	Y	String	民族
 * [num]	Y	String	身份证号
 * [sex]	Y	String	性别
 * [name]	Y	String	姓名
 * [birth]	Y	String	生日
 *
 * Created on 2019/7/19  15:24
 * @author mnlin
 */
data class UploadIdCardBean(
        var address: String? = null,
        var birth: String? = null,
        var config_str: String? = null,
        var face_rect: FaceRect = FaceRect(),
        var face_rect_vertices: List<FaceRectVertice> = listOf(),
        var name: String? = null,
        var nationality: String? = null,
        var num: String? = null,
        var request_id: String? = null,
        var sex: String? = null,
        var success: Boolean = true
) {
    data class FaceRect(
            var angle: Double = .0,
            var center: Center = Center(),
            var size: Size = Size()
    ) {
        data class Size(
                var height: Double = .0,
                var width: Double = .0
        )

        data class Center(
                var x: Double = .0,
                var y: Double = .0
        )
    }

    data class FaceRectVertice(
            var x: Double = .0,
            var y: Double = .0
    )
}

/**
 * function : 获取开户信息(所在流程等等)
 *
 * [idcardPositive]	Y	String	证明照
 * [idcardContrary]	Y	String	反面照
 * [name]	Y	String	姓名
 * [lastName]	Y	String	姓的拼音
 * [firstName]	Y	String	名的拼音
 * [sex]	Y	Int	性别 1：男 2：女
 * [birth]	Y	String	生日
 * [address]	Y	String	地址
 * [idcard]	Y	String	身份证号
 * [contactAddress]	Y	String	通讯地址
 * [email]	Y	String	邮箱
 * [proofOfAddress]	Y	String	住址证明
 * [educationType]	Y	Int	学历类型 1：中学或以下 2：大专 3：本科 4：硕士或以上
 * [jobType]	Y	Int	就业类型 1：受雇 2：自雇 3：退休 4：自由投资人
 * [industryCategory]	Y	Int	行业分类 1：计算机/互联网/通信/电子技术 2：生产制造/物流运输 3：销售/贸易 3：金融/银行/保险 4：医药/化工 5：餐饮/娱乐/美容 6：广告公关/媒体/艺术文化 7：教育/法律 8：建筑/房地产 9：政府/事业机构 10：其他
 * [companyName]	Y	String	公司名称
 * [dutyType]	Y	Int	职务类型 1：高管 2：中层 3：普通员工
 * [taxState]	Y	String	税务国家/地区
 * [taxNumber]	Y	String	税务编号
 * [bankName]	Y	String	银行名称
 * [cardNo]	Y	String	银行卡号
 * [personalStatement]	Y	String	个人声明 格式：1,2,3
 * [securitiesInvestmentExType]	Y	Int	证券投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
 * [fundInvestmentExType]	Y	Int	基金投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
 * [foreignInvestmentExType]	Y	Int	外汇投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
 * [fixedInvestmentExType]	Y	Int	固定收益产品投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
 * [otherInvestmentExType]	Y	Int	其他投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
 * [householdIncomeType]	Y	Int	家庭年收入 1：<20w  2：20-50w 3：50-100w  4：>100w
 * [householdNetWorthType]	Y	Int	家庭净资产 1：<50w  2：50-200w 3：200-1000w  4：>1000w
 * [riskToleranceType]	Y	Int	风险承受能力 1：高(30%以上損失)  2：中(20%-30%損失) 3：低(10%-20%損失) 4：很低(10%以下損失)
 * [investmentObjectiveType]	Y	Int	投资目标 1：資本增值 2：股息回报 3：投机 4：对冲 5：其他
 * [openAccountType]	Y	String	选择开通的账户类型 格式 1,2  1-融资账户 2-现金账户
 * [openMarketType]	Y	String	选择开通的市场类型 格式1,2 1：港股（必选）2：中华通 3：美股
 * [ifPromotionOfDerivatives]	Y	Int	是否推行衍生品投资 0-是 1-否
 * [derivativesInvestmentExperience] Y 衍生品投资经历 格式：1,2  1：我曾受過有關衍生品的培訓課程 2：具有與衍生品相關的工作經驗 3：在過去三年，至少產生過5次有關衍生品交易
 * [transactionPassword]	Y	String	交易密码
 * [idcardHord]	Y	String	手持证件照
 * [faceVerification]	Y	String	人脸识别照片
 * [signaturePic]	Y	String	签名文件图片
 * [status]	Y	Int	状态 1：申请中（信息填写中） 2：待审核 3：通过 4：未通過 5:已关闭 6:已开户
 * [checkTime]	Y	String	审核通过时间
 * [stepFillOut]	Y	Int	开户申请中所在步骤 12-表示已经填写完毕
 * [stepNotPassed]	Y	String	审核未通过所在步骤 格式 1,2
 * [notPassing]	Y	String	审核未通过原因
 *
 * Created on 2019/7/19  15:46
 * @author mnlin
 */
data class EstablishStatusBean(
        var imgServerUrl: String? = null,
        var address: String? = null,
        var adminUserId: String? = null,
        var adminUserName: String? = null,
        var bankName: String? = null,
        var birth: String? = null,
        var cardNo: String? = null,
        var checkTime: String? = null,
        var companyName: String? = null,
        var contactAddress: Int = 0,
        var createTime: String? = null,
        var dutyType: Int = 0,
        var educationType: Int = 0,
        var email: String? = null,
        var faceVerification: String? = null,
        var firstName: String? = null,
        var fixedInvestmentExType: Int = 0,
        var foreignInvestmentExType: Int = 0,
        var fundInvestmentExType: Int = 0,
        var householdIncomeType: Int = 0,
        var householdNetWorthType: Int = 0,
        var id: Int = 0,
        var idcard: String? = null,
        var idcardContrary: String? = null,
        var idcardHord: String? = null,
        var idcardPositive: String? = null,
        var ifPromotionOfDerivatives: Int = 1,
        var derivativesInvestmentExperience: String? = null,
        var industryCategory: Int = 0,
        var investmentObjectiveType: Int = 0,
        var jobType: Int = 0,
        var lastName: String? = null,
        var name: String? = null,
        var openAccountType: Int = 0,
        var openMarketType: String? = "1",
        var otherInvestmentExType: Int = 0,
        var personalStatement: String? = null,
        var proofOfAddress: String? = null,
        var riskToleranceType: Int = 0,
        var securitiesInvestmentExType: Int = 0,
        var sex: Int = 0,
        var signaturePic: String? = null,
        var status: Int = 0,
        var stepFillOut: Int = 0,
        var stepNotPassed: Int = 0,
        var taxNumber: String? = null,
        var taxState: String? = null,
        var transactionPassword: String? = null,
        var updateTime: String? = null,
        var userId: Int = 0,
        var notPassing: String? = null
)