package bean.kitchenmanage.dishes;

import java.io.Serializable;

/**
 * @ClassName: DishesTaste
 * @Description: 口味类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class DishesTasteC implements Serializable {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * 口味名称，口味不要重名，添加时要做判断
	 */
	/**
	 * 对象id,等于docmentid,一般用于Pojo操作时使用。
	 */
	private String _id;

	private String tasteName;
	/**
	 * 是否有效
	 */
	private boolean isValid;

	public DishesTasteC()
	{
	}
	public DishesTasteC(String channelId, String tasteName)
	{
		this.channelId = channelId;
		this.tasteName = tasteName;
		this.className="DishesTasteC";
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

	public String getTasteName() {
		return tasteName;
	}

	public void setTasteName(String tasteName) {
		this.tasteName = tasteName;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean valid) {
		isValid = valid;
	}
}
