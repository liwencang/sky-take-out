package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@EnableTransactionManagement
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealService setmealService;

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        //提取setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //获取套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //保存套餐
        setmealMapper.insert(setmeal);
        //保存菜品
        //设置菜品中的套餐id
        Long setmealId = setmeal.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insert(setmealDishes);

    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNo = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        PageHelper.startPage(pageNo, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            int status = setmealMapper.queryStatusById(id);
            if(status == StatusConstant.ENABLE){
                throw new RuntimeException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //取出套餐Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //取出套餐中的菜品SetmealDish
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        //修改Setmeal
        setmealMapper.update(setmeal);

        //根据套餐id删除套餐菜品的关联
        setmealDishMapper.deleteBySetmealIds(Collections.singletonList(setmeal.getId()));

        //把setmealId放进去
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        //插入修改后的套餐菜品关联
        setmealDishMapper.insert(setmealDishes);
    }

    @Override
    public SetmealVO getSetmealWithDishs(Long id) {
        //查询setmeal，合并到setmealVO中
        SetmealVO setmealVO = new SetmealVO();
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        BeanUtils.copyProperties(setmeal, setmealVO);

        //查询setmeal与dish的关联
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishIdBySetmealId(id);

        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    public void startOrStopSetmeal(Long id, int status) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }
}
