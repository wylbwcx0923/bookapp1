package com.jxtc.bookapp.config;

/**
 * @author 不忘初心
 * 本类用于配置常用的静态常量
 */
public class ApiConstant {
    //访问OSS指定的文件类型
    public class FileType {
        public static final String IMG = "img";
        public static final String CHAPTER = "chapter";
    }

    //常用状态码
    public class StatusCode {
        public static final int OK = 20000;//成功
        public static final int ERROR = 20001;//失败
        public static final int LOGINERROR = 20002;//用户名或密码错误
        public static final int ACCESSERROR = 20003;//权限不足
        public static final int REMOTEERROR = 20004;//远程调用失败
        public static final int REPERROR = 20005;//重复操作
        public static final int SERVERERROR = 50000;//服务器异常
    }

    //用户类型
    public class UserType {
        public static final int GENNERAL_USER = 1;//普通用户
        public static final int VIP_MONTH_USER = 2;//包月VIP用户
        public static final int VIP_QUARTER_USER = 3;//包季VIP用户
        public static final int VIP_YEAR_USER = 4;//包年VIP用户
    }

    //小说类型
    public class BookType {
        public static final int VIP_BOOK = 1;//VIP小说
        public static final int NO_VIP_BOOK = 0;//非VIP小说
    }

    //时间间隔
    public class Timer {
        public static final long ONE_MIN = 60;//一分钟
        public static final long TEN_MIN = 60 * 10;//十分钟
        public static final long ONE_DAY = 86400;//一天
        public static final long THREE_DAY = 86400 * 3;//三天
        public static final long THREE_DAY_MSEC = 86400 * 1000 * 3;//三天的毫秒值
        public static final long ONE_DAY_MSEC = 86400 * 1000;//一天的毫秒值
    }

    //常用的一些配置
    public class Config {
        //书籍封面地址前缀
        public static final String IMGPATH = "https://jxxs-pic.oss-cn-hangzhou.aliyuncs.com/";
        //默认头像
        public static final String DEFULT_HEAD = "http://jxxs-app-img.oss-cn-hangzhou.aliyuncs.com/images/2019/07/05/15623071073296766100.png";
    }

    //收费标准千字0.25元即25阅币
    public class Price {
        public static final int UNIT_PRICE = 25;
    }

    //VIP开通状态
    public class VipStatus {
        public static final int DREDGE = 1;//开通状态
        public static final int EXPIRE = 2;//过期状态
    }

    //打赏类型
    public class RewardType {
        public static final int BIXIN = 1;//比心
        public static final int ROSE = 2;//玫瑰
        public static final int SIX = 3;//666
        public static final int CARS = 4;//豪车
    }

    //打赏金额
    public class RewardAmount {
        public static final int BIXIN = 188;//比心
        public static final int ROSE = 520;//玫瑰
        public static final int SIX = 666;//666
        public static final int CARS = 1888;//豪车
    }

    //用户等级类型
    public class UserGrade {
        public static final int GENERAL = 0;//普通用户
        public static final int COPPER = 1;//铜牌用户
        public static final int AGUSER = 2;//银牌用户
        public static final int GOLD = 3;//金牌用户
        public static final int DIAMOND = 4;//钻石用户
    }

    //等级用户折扣
    public class UserDiscount {
        public static final double GENERAL = 1;//普通用户
        public static final double COPPER = 1;//铜牌用户
        public static final double AGUSER = 0.9;//银牌用户
        public static final double GOLD = 0.8;//金牌用户
        public static final double DIAMOND = 0.7;//钻石用户
    }

    public class BookReviewPraiseType {
        public static final int LIKE = 1;//点赞
        public static final int NOLIKE = 2;//取消赞

    }

    public class BookBangDanType {
        public static final int BOY = 1;
        public static final int GRIL = 2;
        public static final int CHUBAN = 3;
    }

    public class WXPayConfig {
        //微信统一下单
        public static final String CREATE_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //微信支付中签约接口
        public static final String PAY_CONTRACTORDER = "https://api.mch.weixin.qq.com/pay/contractorder";
        //微信自动扣费,申请扣费接口
        public static final String PAY_PAPPAYAPPLY = "https://api.mch.weixin.qq.com/pay/pappayapply";
    }

    /**
     * 订单状态
     */
    public class OrderStatus {
        public static final int DOWN_ORDER = 0;//下单状态
        public static final int SUCCESS = 1;//支付成功
        public static final int FAIL = 2;//支付失败
    }

    /**
     * 订单类型
     */
    public class OrderType {
        public static final int GENNERAL_29 = 1;//普通29元充值
        public static final int GENNERAL_49 = 2;//普通49元充值
        public static final int GENNERAL_99 = 3;//普通99元充值
        public static final int GENNERAL_129 = 4;//普通129元充值
        public static final int VIP_MONTH_ORDER = 5;//包月VIP
        public static final int VIP_QUARTER_ORDER = 6;//包季VIP
        public static final int VIP_YEAR_ORDER = 7;//包年VIP
    }

    /**
     * 任务类型
     */
    public class TaskType {
        public static final int SIGN_IN = 1;//签到
        public static final int SHARE = 2;//分享
        public static final int READ = 3;//阅读
        public static final int ASSIGN_READ = 4;//指定阅读
        public static final int BIND_WX = 5;//绑定微信
        public static final int BIND_QQ = 6;//绑定QQ
    }

    /**
     * 优惠券状态
     */
    public class CouponStatus {
        public static final int UNUSE = 0;//未使用
        public static final int USED = 1;//已使用
        public static final int EXPIRE = 2;//过期
    }

    /**
     * 经验值临界值
     */
    public class EmpiricalCrisis {
        public static final int COPPER = 2000;//铜牌用户
        public static final int AGUSER = 3000;//银牌用户
        public static final int GOLD = 4000;//金牌用户
        public static final int DIAMOND = 5000;//钻石用户
    }

    /**
     * 优惠券类型
     */
    public class CouponType {
        public static final int DISCOUNT = 1;//折扣券
        public static final int VOUCHER = 2;//代金券
        public static final int REWARD = 4;//打赏券

    }

    /**
     * 定义留存时间常量
     */
    public class KeepTime {
        public static final int NEXT_DAY = 1;//次日留存
        public static final int SEVEN_DAY = 7;//七日留存
        public static final int MONTH_DAY = 30;//月留存

    }
}
