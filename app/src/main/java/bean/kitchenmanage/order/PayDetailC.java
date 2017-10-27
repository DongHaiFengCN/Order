package bean.kitchenmanage.order;

/**
 * Class 支付详情类
 *
 * Created by loongsun on 17/1/7.
 *
 * email: 125736964@qq.com
 */

public class PayDetailC
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
     * docId
     */
    private String _id;


    /**
     * 支付方式
     */
    //private PayType payType;
    /**
     * 支付方式 支付类型名称 1\现金、2\银行卡、3\微信、4\支付宝、5\美团、6\会员卡 7\抹零 8\赠卷\9\
     */
    private int payTypes;
    /**
     * 支付金额
     */
    private float subtotal;//支付钱数

    public PayDetailC() {
    }

    public PayDetailC(String company_id, int payTypes, float subtotal) {
        this.channelId = company_id;
        this.payTypes = payTypes;
        this.subtotal = subtotal;
        this.className="PayDetailC";
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

    public int getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(int payTypes) {
        this.payTypes = payTypes;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }
}
