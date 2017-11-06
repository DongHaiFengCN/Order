package bean;

import bean.kitchenmanage.dishes.DishesC;

/**
 * Created by lenovo on 2017/11/6.
 */

public class Goods {
    private DishesC dishesC;
    private int count;

    public Goods() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DishesC getDishesC() {
        return dishesC;
    }

    public void setDishesC(DishesC dishesC) {
        this.dishesC = dishesC;
    }
}
