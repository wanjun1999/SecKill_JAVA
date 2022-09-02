package com.xxxx.seckill.controller;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.vo.DetailVo;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;
    //为了查商品
    @Autowired
    private IGoodsService goodsService;

    //引入redis
    @Autowired
    private RedisTemplate redisTemplate;
    //手动渲染页面
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    //window优化前QPS为1571
    //linux优化前QPS为440
    //redis页面缓存后windows为2312
    //加respondbody目的是为了返回对象
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    //session，通过cookie获取用户信息,model为商品页面展示信息,ticket为cookie的值
    public String toList(Model model,User user,
                         HttpServletRequest request,HttpServletResponse response){
        //redis获取页面，如果不为空，则返回
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //都没问题则更新状态
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        //return "goodsList";
        //返回一个页面
        //如果redis为空，则手动渲染，并存入
        //根据process需要的参数创建相应的值
        //map参数是想放在页面里的数据，类似上面的model
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return html;
    }

    //跳转商品详情页（原）
    @RequestMapping(value = "/toDetail2/{goodsId}",produces = "text/html;charset=utf-8")
    //这个responseBody的作用不太理解
    @ResponseBody
    public String toDetail2(Model model,User user,@PathVariable Long goodsId,
                           HttpServletRequest request,HttpServletResponse response){
        //与之前的操作基本相同
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //redis如果不为空则直接返回
        String html = (String) valueOperations.get("goodsDetail:"+goodsId);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //获取开始时间
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        }else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            //秒杀中
            remainSeconds = 0;
            secKillStatus = 1;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",goodsVo);

        //如果为空则手动渲染
        //返回一个页面
        //如果redis为空，则手动渲染，并存入
        //根据process需要的参数创建相应的值
        //map参数是想放在页面里的数据，类似上面的model
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
        //return "goodsDetail";
    }

    //跳转商品详情页（新）
    @RequestMapping("/detail/{goodsId}")
    //这个responseBody的作用不太理解
    @ResponseBody
    public RespBean toDetail(Model model, User user, @PathVariable Long goodsId){

        //model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //获取开始时间
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        }else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            //秒杀中
            remainSeconds = 0;
            secKillStatus = 1;
        }
        //model.addAttribute("remainSeconds",remainSeconds);
        //model.addAttribute("secKillStatus",secKillStatus);
        //model.addAttribute("goods",goodsVo);

        //如果为空则手动渲染
        //返回一个页面
        //如果redis为空，则手动渲染，并存入
        //根据process需要的参数创建相应的值
        //map参数是想放在页面里的数据，类似上面的model

        //return "goodsDetail";
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);
    }
}
