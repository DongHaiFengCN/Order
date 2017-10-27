package bean.kitchenmanage.order;


import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.user.UsersC;


/**
 * @ClassName: Order
 * @Description: 订单类
 * @author loongsun
 * @date 2014-7-28 下午11:44:16
 * 
 */
public class OrderC {
    //1、
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



	//2、
	/**
	 * 订单号，生成规则 datetime+桌位号
	 */
	private String orderID;
	/**
	 * 订单序号，只做同桌多次点餐时使用,第一次下单为序号为1，第二次下单序号为2
	 */
	private int orderNum;
	/**
	 * 每天的流水号，从001开始，按序向后排。按营业时间重新产生序号。
	 */
	private int serialNum;

	/**
	 *打印标志flag为0,未打印，1，代表本地打印完毕,2,代表厨房打印机打印，3，代表两者都已打印
	 */
	private int printFlag;
	/**
	 * 订单金额
	 */
	private float allPrice;//
	/**
	 *订单产生时间
	 *yyyy-MM-dd HH:mm:ss
	 *之所以定义成String型，是因为 DB4o 按该字段排序时不支持 Date型
	 *
	 */
	private String createdTime;// 日期时间****
	/**
	 *排队号，保留
	 */
	private String queueNum;//排队号
	/**
	 * 下单手机的mac地址
	 */
	private String mac;
	/**
	 *订单状态 0：已买单；1:刚下单未买单；2：消台订单；
	 */
	private int orderState;


	//3、
	private List<GoodsC> goodsList;
	/**
	 *点餐员姓名
	 */
	private UsersC operator;
	private String comapnyName;//网点名称
	private String areaName;
	private String tableName;
	/**
	 *桌位号
	 */
	private String tableNo;
	/**
	 * 订单备注
	 */
    private String note;
	/**
	 *
	 */
	private List<TaboosC> taboosList;

	/**
	 *桌位状态记录对象
	 */
	private String  tableLogId;
	/**
	 * 预定者信息
	 */
	private String reserverId;
	/**
	 *0,前台主程序所下订单;1、点餐宝所下订单;2、手机所下订单
	 */
	private int orderType;


	public OrderC()
	{
	}
	public OrderC(String company_id) {
		this.channelId = company_id;
		this.className="OrderC";
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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



	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	public int getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(int printFlag) {
		this.printFlag = printFlag;
	}

	public float getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(float allPrice) {
		this.allPrice = allPrice;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getQueueNum() {
		return queueNum;
	}

	public void setQueueNum(String queueNum) {
		this.queueNum = queueNum;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getOrderState() {
		return orderState;
	}

	public void setOrderState(int orderState) {
		this.orderState = orderState;
	}

	public List<GoodsC> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<GoodsC> goodsList) {
		this.goodsList = goodsList;
	}

	public void addGoods(GoodsC obj)
	{
		if(this.goodsList==null)
			this.goodsList=new ArrayList<GoodsC>();
		this.goodsList.add(obj);
	}

	public UsersC getOperator() {
		return operator;
	}

	public void setOperator(UsersC operator) {
		this.operator = operator;
	}

	public String getComapnyName() {
		return comapnyName;
	}

	public void setComapnyName(String comapnyName) {
		this.comapnyName = comapnyName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<TaboosC> getTaboosList() {
		return taboosList;
	}

	public void setTaboosList(List<TaboosC> taboosList) {
		this.taboosList = taboosList;
	}

	public String getTableNo() {
		return tableNo;
	}

	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}

	public String getTableLogId() {
		return tableLogId;
	}

	public void setTableLogId(String tableLogId) {
		this.tableLogId = tableLogId;
	}

	public String getReserverId() {
		return reserverId;
	}

	public void setReserverId(String reserverId) {
		this.reserverId = reserverId;
	}
}
