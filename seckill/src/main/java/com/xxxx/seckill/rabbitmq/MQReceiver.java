package com.xxxx.seckill.rabbitmq;

import com.xxxx.seckill.pojo.SeckillMessage;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接受消息："+ msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("Queue01接受消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("Queue02接受消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("Queue01接受消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("Queue02接受消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg){
//        log.info("Queue01接受消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg){
//        log.info("Queue02接受消息" + msg);
//    }
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;
    //下单操作
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接受消息：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message,SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        //判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        if(goodsVo.getStockCount()<1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodId);
        if(seckillOrder != null){
            return ;
        }
        //下单
        orderService.seckill(user,goodsVo);
    }
}
