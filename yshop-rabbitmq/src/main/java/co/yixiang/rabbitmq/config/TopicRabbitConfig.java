package co.yixiang.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujinlai
 * @date 2021-04-29
 */

@Configuration
public class TopicRabbitConfig {

    final static String message = "topic.message";
    final static String messages = "topic.messages";

    // 美德医队列名称
    @Value("${meideyi.queueName}")
    private String queueNameMeideyi;

    // 业务交换机
    @Value("${spring.rabbitmq.bizExchangeName}")
    private String bizExchangeName;



    // 业务延迟交换机
    @Value("${spring.rabbitmq.bizDelayExchangeName}")
    private String bizDelayExchangeName;

    // 延迟队列名称
    @Value("${spring.rabbitmq.delayQueueName}")
    private String delayQueueName;

    // 死信交换机
    @Value("${spring.rabbitmq.deadExchangeName}")
    private String deadExchangeName;

    // 死信队列名称
    @Value("${spring.rabbitmq.deadQueueName}")
    private String deadQueueName;

    // 死信队列绑定死信交换机的routeKey
    @Value("${spring.rabbitmq.deadQueueName}")
    private String deadRoutekey;

    // 药联队列名称
    @Value("${yaolian.queueName}")
    private String queueNameYaolian;

    // 众安普药队列名称
    @Value("${zhonganpuyao.queueName}")
    private String queueNameZhonganpuyao;

    // 蚂蚁队列名称
    @Value("${ant.queueName}")
    private String queueNameAnt;

    // 蚂蚁订单运费队列名称
    @Value("${ant.orderFreightQueueName}")
    private String orderFreightQueueNameAnt;

    // 益药宝订单队列名称
    @Value("${yiyaobao.queueName}")
    private String queueNameYiyaobao;

    @Value("${yiyaobao.refundQueueName}")
    private String refundQueueNameYiyaobao;

    @Value("${meditrust.queueName}")
    private String queueNameMeditrust;

    @Value("${msh.queueName}")
    private String queueNameMsh;

    @Value("${junling.queueName}")
    private String queueNameJunling;

    //-------------------------延迟队列--------------------------------
    // 美德医延迟队列名称
    @Value("${meideyi.delayQueueName}")
    private String delayQueueNameMeideyi;

    // 药联延迟队列名称
    @Value("${yaolian.delayQueueName}")
    private String delayQueueNameYaolian;

    //众安延迟队列名称
    @Value("${zhonganpuyao.delayQueueName}")
    private String delayQueueNameZhonganpuyao;

    // 蚂蚁延迟队列名称
    @Value("${ant.delayQueueName}")
    private String delayQueueNameAnt;

    // 蚂蚁订单运费延迟队列名称
    @Value("${ant.delayOrderFreightQueueName}")
    private String delayOrderFreightQueueNameAnt;

    // 益药宝订单延迟队列名称
    @Value("${yiyaobao.delayQueueName}")
    private String delayQueueNameYiyaobao;

    // 美信订单延迟队列名称
    @Value("${meditrust.delayQueueName}")
    private String delayQueueNameMeditrust;

    //// Msh延迟队列名称
    @Value("${msh.delayQueueName}")
    private String delayQueueNameMsh;

    //君玲延迟队列名称
    @Value("${junling.delayQueueName}")
    private String delayQueueNameJunling;

    //定义队列
    @Bean
    public Queue queueMessage() {
        return new Queue(TopicRabbitConfig.message);
    }

    @Bean
    public Queue queueMessages() {
        return new Queue(TopicRabbitConfig.messages);
    }

    //交换机
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("exchange");
    }

    //将队列和交换机绑定
    @Bean
    Binding bindingExchangeMessage(Queue queueMessage, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).with("topic.message");
    }

    @Bean
    Binding bindingExchangeMessages(Queue queueMessages, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");
    }


    // 1.申明死信队列
    @Bean
    public Queue deadQueue() {
        return new Queue(deadQueueName,true,false,false);
    }

    // 2.申明死信交换机
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(deadExchangeName,true,false);
    }

    // 3.死信队列与死信交换机绑定
    @Bean
    public Binding deadQueueBinding(Queue deadQueue,DirectExchange deadExchange) {
        return BindingBuilder.bind(deadQueue).to(deadExchange).with(deadRoutekey);
    }

    // 4.申请业务交换机
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(bizExchangeName,true,false);
    }

    // 4.申请业务延迟交换机
    @Bean
    public CustomExchange customExchange() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(bizDelayExchangeName, "x-delayed-message", true, false, args);
    }


    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueName,true,false,false,map);

        return queue;
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingDelay(Queue queueDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueDelay).to(customExchange).with(delayQueueName).noargs();
    }


    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueMeideyi() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameMeideyi,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingMeideyi(Queue queueMeideyi,DirectExchange directExchange) {
        return BindingBuilder.bind(queueMeideyi).to(directExchange).with(queueNameMeideyi);
    }

    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueYaolian() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameYaolian,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingYaolian(Queue queueYaolian,DirectExchange directExchange) {
        return BindingBuilder.bind(queueYaolian).to(directExchange).with(queueNameYaolian);
    }

    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueZhonganpuyao() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameZhonganpuyao,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingZhonganpuyao(Queue queueZhonganpuyao,DirectExchange directExchange) {
        return BindingBuilder.bind(queueZhonganpuyao).to(directExchange).with(queueNameZhonganpuyao);
    }

    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueAnt() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameAnt,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingAnt(Queue queueAnt,DirectExchange directExchange) {
        return BindingBuilder.bind(queueAnt).to(directExchange).with(queueNameAnt);
    }

    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueAntorderFreight() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(orderFreightQueueNameAnt,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingAntorderFreight(Queue queueAntorderFreight,DirectExchange directExchange) {
        return BindingBuilder.bind(queueAntorderFreight).to(directExchange).with(orderFreightQueueNameAnt);
    }

    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueYiyaobao() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameYiyaobao,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingYiyaobao(Queue queueYiyaobao,DirectExchange directExchange) {
        return BindingBuilder.bind(queueYiyaobao).to(directExchange).with(queueNameYiyaobao);
    }


    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue refundQueueYiyaobao() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(refundQueueNameYiyaobao,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding refundQueueBandingYiyaobao(Queue refundQueueYiyaobao,DirectExchange directExchange) {
        return BindingBuilder.bind(refundQueueYiyaobao).to(directExchange).with(refundQueueNameYiyaobao);
    }


    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueMeditrust() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameMeditrust,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingMeditrust(Queue queueMeditrust,DirectExchange directExchange) {
        return BindingBuilder.bind(queueMeditrust).to(directExchange).with(queueNameMeditrust);
    }

    // 5。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueJunling() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameJunling,true,false,false,map);
    }

    // 6.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingJunling(Queue queueJunling,DirectExchange directExchange) {
        return BindingBuilder.bind(queueJunling).to(directExchange).with(queueNameJunling);
    }

    // 7。申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueMsh() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        return new Queue(queueNameMsh,true,false,false,map);
    }

    // 8.业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingMsh(Queue queueMsh,DirectExchange directExchange) {
        return BindingBuilder.bind(queueMsh).to(directExchange).with(queueNameMsh);
    }


    //-------------------------------------延迟队列-------------------------------------------------
    // 美德医申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueMeideyiDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameMeideyi,true,false,false,map);

        return queue;
    }

    // 美德医业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingMeideyiDelay(Queue queueMeideyiDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueMeideyiDelay).to(customExchange).with(delayQueueNameMeideyi).noargs();
    }

    // 药联申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueYaolianDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameYaolian,true,false,false,map);

        return queue;
    }

    // 药联业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingYaolianDelay(Queue queueYaolianDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueYaolianDelay).to(customExchange).with(delayQueueNameYaolian).noargs();
    }


    // 众安申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueZhonganpuyaoDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameZhonganpuyao,true,false,false,map);

        return queue;
    }

    // 众安业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingZhonganpuyaoDelay(Queue queueZhonganpuyaoDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueZhonganpuyaoDelay).to(customExchange).with(delayQueueNameZhonganpuyao).noargs();
    }

    // 蚂蚁申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueAntDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameAnt,true,false,false,map);

        return queue;
    }

    // 蚂蚁业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingAntDelay(Queue queueAntDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueAntDelay).to(customExchange).with(delayQueueNameAnt).noargs();
    }


    // 蚂蚁申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueAntOrderFreightDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayOrderFreightQueueNameAnt,true,false,false,map);

        return queue;
    }

    // 蚂蚁业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingAntOrderFreightDelay(Queue queueAntOrderFreightDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueAntOrderFreightDelay).to(customExchange).with(delayOrderFreightQueueNameAnt).noargs();
    }

    // 益药宝订单申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueYiyaobaoDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameYiyaobao,true,false,false,map);

        return queue;
    }

    // 益药宝订单业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingYiyaobaoDelay(Queue queueYiyaobaoDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueYiyaobaoDelay).to(customExchange).with(delayQueueNameYiyaobao).noargs();
    }


    //美信申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueMeditrustDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameMeditrust,true,false,false,map);

        return queue;
    }

    // 美信业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingMeditrustDelay(Queue queueMeditrustDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueMeditrustDelay).to(customExchange).with(delayQueueNameMeditrust).noargs();
    }

    //msh申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueMshDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameMsh,true,false,false,map);

        return queue;
    }

    // msh业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingMshDelay(Queue queueMshDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueMshDelay).to(customExchange).with(delayQueueNameMsh).noargs();
    }

    //君玲申明业务队列，并指定对应的死信交换机
    @Bean
    public Queue queueJunlingDelay() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("x-dead-letter-exchange",deadExchangeName);  //指定死信交换机
        map.put("x-dead-letter-routing-key",deadRoutekey);  // 指定死信routeKey
        map.put("x-message-ttl", 5 * 60*1000); // 消息在队列中超时时间，超时后将消息移到死信队列，5*60秒= 5分钟
        Queue queue = new Queue(delayQueueNameJunling,true,false,false,map);

        return queue;
    }

    // 君玲业务队列与以业务交换机绑定
    @Bean
    public Binding queueBandingJunlingDelay(Queue queueJunlingDelay,CustomExchange customExchange) {
        return BindingBuilder.bind(queueJunlingDelay).to(customExchange).with(delayQueueNameJunling).noargs();
    }

}
