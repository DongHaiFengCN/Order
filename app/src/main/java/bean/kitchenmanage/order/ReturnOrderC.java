package bean.kitchenmanage.order;


import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.user.UsersC;

/**
 * Created by lenovo on 2017/12/12.
 */

public class ReturnOrderC {

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
     * 包含的菜品
     */
    private List<GoodsC> goodsList;
    /**
     * 退菜日期
     *  yyyy-MM-dd HH:mm:ss
     *  之所以定义成String型，是因为 DB4o 按该字段排序时不支持 Date型
     */
    private String checkTime;
    /**
     * 所属桌位号
     */
    private String tableNo;
    /**
     * 所属房间名
     */
    private String tableName;
    /**
     *退菜金额
     */
    private float pay;

    /**
     * 操作员
     */
    private UsersC operator;



    public ReturnOrderC() {
    }

    public ReturnOrderC(String company_id)
    {
        this.channelId = company_id;
        this.className ="ReturnOrderC";
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

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public float getPay() {
        return pay;
    }

    public void setPay(float pay) {
        this.pay = pay;
    }

    public UsersC getOperator() {
        return operator;
    }

    public void setOperator(UsersC operator) {
        this.operator = operator;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
