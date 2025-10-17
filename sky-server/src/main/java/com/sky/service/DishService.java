package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);

    PageResult getPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(String ids);

    DishVO getDishWithFlavorByDishId(Long id);

    void update(DishDTO dishDTO);
}
