package view;

import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/18 15:26
 * 修改人：donghaifeng
 * 修改时间：2017/9/18 15:26
 * 修改备注：
 */

public interface IMainView {

    void initView();

    void showDishes(List<String> data,List<Integer> headList);

    void showKindName(List<String> data);


}
