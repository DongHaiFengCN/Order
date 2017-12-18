package bean.kitchenmanage.order;

/**
 * Class description goes here.
 * <p>
 * Created by loongsun on 2017/12/18.
 * <p>
 * email: 125736964@qq.com
 */

public class RetreatOrderC
{
    /**
     * 0,新产生退菜记录(点餐宝生成时赋值为0）。1，已分发到厨房（主程序打印完后赋值为1），便于liveQuery监听区分新记录
     */
    private int state;
    /**
     * 退菜的订单Id,便于找到该订单下退的菜品，分发到各厨房
     */
    private String orderCId;


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

    public RetreatOrderC() {
    }

    public RetreatOrderC(String channelId) {
        this.channelId = channelId;
        this.className = "RetreatOrderC";
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOrderCId() {
        return orderCId;
    }

    public void setOrderCId(String orderCId) {
        this.orderCId = orderCId;
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
}
