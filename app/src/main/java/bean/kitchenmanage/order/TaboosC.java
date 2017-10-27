package bean.kitchenmanage.order;

/**
 * Class 忌口类.
 * <p>
 * Created by loongsun on 17/1/7.
 * <p>
 * email: 125736964@qq.com
 */

public class TaboosC {
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
     * 忌口描述
     */
    private String name;
    /**
     * 是否有效 1，有效，0无效
     */
    private int isValid;

    public TaboosC() {
    }

    public TaboosC(String company_id, String name, int isValid) {
        this.channelId = company_id;
        this.name = name;
        this.isValid = isValid;
        this.className="TaboosC";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }
}
