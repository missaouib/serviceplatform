/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.monitor.service.impl;

import co.yixiang.modules.monitor.domain.vo.RedisVo;
import co.yixiang.modules.monitor.service.RedisService;
import co.yixiang.utils.PageUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate redisTemplate;

    @Value("${loginCode.expiration}")
    private Long expiration;

    @Override
    public Page<RedisVo> findByKey(String key, Pageable pageable){
        List<RedisVo> redisVos = new ArrayList<>();
        if(!"*".equals(key)){
            key = "*" + key + "*";
        }
        for (Object s : redisTemplate.keys(key)) {
            // 过滤掉权限的缓存
            if (s.toString().indexOf("role::loadPermissionByUser") != -1 || s.toString().indexOf("user::loadUserByUsername") != -1) {
                continue;
            }
            RedisVo redisVo = new RedisVo(s.toString(),redisTemplate.opsForValue().get(s.toString()).toString());
            redisVos.add(redisVo);
        }
        Page<RedisVo> page = new PageImpl<RedisVo>(
                PageUtil.toPage(pageable.getPageNumber(),pageable.getPageSize(),redisVos),
                pageable,
                redisVos.size());
        return page;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void flushdb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public String getCodeVal(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key).toString();
            return value;
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public Object getObj(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveCode(String key, Object val,Long time) {
        redisTemplate.opsForValue().set(key,val);
        redisTemplate.expire(key,time, TimeUnit.SECONDS);
    }

    /*
    * longitude 经度
    * latitude 纬度
    * */
    @Override
    public void addGeo(String key, double longitude, double latitude, String member) {
       // RedisGeoCommands.GeoLocation geoLocation= new RedisGeoCommands.GeoLocation(member,new Point(longitude,latitude));
        redisTemplate.opsForGeo().add(key,new Point(longitude,latitude),member);
    }

    /*
    *
    *
    * */
    @Override
    public void geoRadiusByCoordinate(String key, double range,long limit,double longitude,double latitude) {
        Distance distance = new Distance(range, RedisGeoCommands.DistanceUnit.KILOMETERS);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance().sortAscending().limit(limit);
        Circle circle = new Circle(new Point(longitude,latitude),distance);
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults  = redisTemplate.opsForGeo().radius(key,circle,args);

        Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> result = geoResults.iterator();
        while(result.hasNext()){
            GeoResult<RedisGeoCommands.GeoLocation<String>> geoLocation = result.next();
            java.text.DecimalFormat df=new java.text.DecimalFormat("#.########");
             String value = df.format(geoLocation.getDistance().getValue());
             System.out.println("geoLocation.getDistance().getValue()="+value);
             System.out.println("geoLocation.getContent().getName()="+geoLocation.getContent().getName());
           // String[]  sts = geoLocation.getContent().getName().split("#");//拼接拆开

        }


    }

    @Override
    public List<Point> geoops(String key,String member) {
        List<Point>  pointList =  redisTemplate.opsForGeo().position(key,member);
        return pointList;
    }

}
