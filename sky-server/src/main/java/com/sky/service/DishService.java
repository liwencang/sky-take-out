package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);

    PageResult getPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(String ids);

    DishVO getDishWithFlavorByDishId(Long id);

    void update(DishDTO dishDTO);

    void enableOrDisable(String status);

    List<Dish> getDishListByCategoryId(Long categoryId);
}
