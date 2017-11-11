package bean;

import bean.kitchenmanage.order.GoodsC;

/**
 * Created by lenovo on 2017/11/11.
 */

public class Orders  {

    private GoodsC goodsC;

    private int price;

    public GoodsC getGoodsC() {
        return goodsC;
    }

    public void setGoodsC(GoodsC goodsC) {
        this.goodsC = goodsC;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
