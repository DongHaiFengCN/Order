package bean.kitchenmanage.dishes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:DishesKind
 * @Description: 菜品类文件
 * @author loongsun
 * @date 2017-7-29 上午1:06:02
 *
 */

public class DishesKindC implements Serializable
{
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String _id;
	/**
	 * 菜品类别名称
	 */
	private String kindName;
	/**
	 * 是否是一级套餐
	 * * true:为一级套餐。false为正常菜类
	 */
	private boolean isSetMenu;//是否是一级套餐

	/**
	 *
	 * 该类下所包含的菜品
	 */
	private List<String> dishesListId;//下属菜品



	public DishesKindC() {
	}

	public DishesKindC(String channelId) {
		this.channelId = channelId;
		this.className="DishesKindC";
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getKindName() {
		return kindName;
	}

	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

	public boolean getisSetMenu() {
		return isSetMenu;
	}

	public void setisSetMenu(boolean is) {
		isSetMenu = is;
	}

	public List<String> getDishesListId() {
		if(dishesListId==null)
			dishesListId=new ArrayList<>();
		return dishesListId;
	}

	public void setDishesListId(List<String> dishesListId) {
		this.dishesListId = dishesListId;
	}

	public void addDishesId(String id)
	{
		if(dishesListId==null)
			dishesListId=new ArrayList<>();
		dishesListId.add(id);
	}



}
