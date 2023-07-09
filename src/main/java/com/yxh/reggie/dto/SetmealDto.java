package com.yxh.reggie.dto;
import com.yxh.reggie.entity.Setmeal;
import com.yxh.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
