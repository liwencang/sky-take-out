package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableTransactionManagement
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //插入菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);

        //获取插入之后的id
        Long dishId = dish.getId();

        //插入菜品关联的口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            for (DishFlavor dishFlavor : dishFlavors) {
                dishFlavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    @Override
    public PageResult getPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBatch(String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .filter(StringUtil::isNotEmpty)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        //启售中的菜品不能删除
        for (Long id : idList) {
            int status = dishMapper.getStatusById(id);
            if(status == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //被套餐关联的菜品不能被删除
        List<Long> setmealDishIds = setmealDishMapper.getSetmealDishIdByDishId(idList);
        if(setmealDishIds != null && !setmealDishIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteBatch(idList);
        dishFlavorMapper.deleteBatchByDishId(idList);
    }

    @Override
    public DishVO getDishWithFlavorByDishId(Long id) {
        Dish dish = dishMapper.getDishById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getFlavorsByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            dishFlavorMapper.deleteBatchByDishId(Collections.singletonList(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
