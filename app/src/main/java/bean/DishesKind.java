package bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/13 10:01
 * 修改人：donghaifeng
 * 修改时间：2017/9/13 10:01
 * 修改备注：
 */

public class DishesKind {
    private String name ;
    private List<String> disheList;

    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setDisheList(List<String> disheList) {
        this.disheList = disheList;
    }


   public List<String> getDisheList(){

       return disheList;
   }

}