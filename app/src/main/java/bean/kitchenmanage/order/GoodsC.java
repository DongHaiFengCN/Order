package bean.kitchenmanage.order;


/**
 * @ClassName: Goods
 * @Description: 商品类文件
 * @author loongsun
 * @date 2017-01-06
 *
 */
public class GoodsC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * docId
	 */
	private String _id;


	/**
	 * 所属订单对象
	 */
	private String order;//订单号
	/**
	 * 所包含菜品对象
	 */
//	private DishesC dishes;
	private String dishesId;
	/**
	 * 所选菜品口味
	 */
	private String dishesTaste;
	/**
	 * 所选菜品个数
	 */
	private int DishesCount;
	/**
	 * 所选菜品个数总价
	 */
	private float allPrice;
	/**
	 * 所选菜品名称
	 */
	private String dishesName;
	/**
	 * 所选菜品名称
	 */
	private String dishesKindName;
	/**
	 * 是否是待叫商品,0，代表正常上菜；1代表待叫菜品，打印时加备注
	 */
    private int isWaitCall;

	/**
	 * 点餐时间
	 */
	private String createdTime;

	public GoodsC() {
	}

	public GoodsC(String company_id) {
		this.channelId = company_id;
		this.className="GoodsC";
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getDishesKindName() {
		return dishesKindName;
	}

	public void setDishesKindName(String dishesKindName) {
		this.dishesKindName = dishesKindName;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
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

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

//	public DishesC getDishes() {
//		return dishes;
//	}
//
//	public void setDishes(DishesC dishes) {
//		this.dishes = dishes;
//	}


	public String getDishesId() {
		return dishesId;
	}

	public void setDishesId(String dishesId) {
		this.dishesId = dishesId;
	}

	public String getDishesTaste() {
		return dishesTaste;
	}

	public void setDishesTaste(String dishesTaste) {
		this.dishesTaste = dishesTaste;
	}

	public int getDishesCount() {
		return DishesCount;
	}

	public void setDishesCount(int dishesCount) {
		DishesCount = dishesCount;
	}

	public float getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(float allPrice) {
		this.allPrice = allPrice;
	}

	public String getDishesName() {
		return dishesName;
	}

	public void setDishesName(String dishesName) {
		this.dishesName = dishesName;
	}

	public int getIsWaitCall() {
		return isWaitCall;
	}

	public void setIsWaitCall(int isWaitCall) {
		this.isWaitCall = isWaitCall;
	}
}
