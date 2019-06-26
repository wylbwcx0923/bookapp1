package com.jxtc.bookapp.config;

/**
 * @author wyl
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
        public static final long ONE_DAY = 86400;//一天
        public static final long THREE_DAY = 86400 * 3;//三天

    }

    //常用的一些配置
    public class Config {
        //书籍封面地址前缀
        public static final String IMGPATH = "https://jxxs-pic.oss-cn-hangzhou.aliyuncs.com/";
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


}
