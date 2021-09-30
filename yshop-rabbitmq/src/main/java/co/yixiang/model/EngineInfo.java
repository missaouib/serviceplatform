package co.yixiang.model;

public interface EngineInfo {
    // 以下均为变量名
    // 积分队列
    public  final static String CORE_POINT_QUEUE = "core_point";
    // 成长值队列
    public  final static String CORE_GROWTH_QUEUE = "core_growth";
    // 优惠券队列
    public  final static String CORE_COUPON_QUEUE = "core_coupon";
    //产业队列
    public  final static String BRAND_MEMBER_QUEUE = "brand_member";
    // 会员等级 队列别名
    public  final static String CORE_LEVEL_QUEUE = "core_level";

    public final static String FANOUT_EXCHANGE= "fanoutExchange";

    public final static String BRAND_FANOUT_EXCHANGE = "brandFanoutExchange";

    public final static String BRAND_POINT = "brand_point";

    //死信交换机
    public final static String EXCHANGE_DEAD_LETTER = "deadLetterExchange";
    //死信队列
    public final static String DEAD_QUEUE = "deadQueue";
    // 死信路由键
    public final static String DEAD_ROUTINGKEY ="routingKey_dead";






}
