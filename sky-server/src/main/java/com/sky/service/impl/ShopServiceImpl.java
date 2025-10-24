package com.sky.service.impl;

import com.sky.constant.RedisConstant;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ShopServiceImpl implements ShopService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void setStatus(String status) {
        stringRedisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS, status);
    }

    @Override
    public Integer getStatus() {
        String status = stringRedisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS);
        return status == null ? null : Integer.valueOf(status);
    }
}
