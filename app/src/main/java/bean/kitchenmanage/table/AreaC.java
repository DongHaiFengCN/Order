package bean.kitchenmanage.table;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: Area
 * @Description: 区域类文件
 * @author
 * @date 2017-05-15
 *
 */
public class AreaC implements Serializable {
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
	 * 区域名称，名称唯一，增加时判断唯一
	 */
	private String areaName;

	/**
	 * 区域号
	 */
	private String areaNum; //
	/**
	 * 是否有效
	 */
	private boolean isvalid;

	/**
	 * 桌位id列表
	 */
	private List<String> tableIDList;

	public AreaC() {
	}

	public AreaC(String company_id)
	{
		this.channelId = company_id;
		this.className="AreaC";
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

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaNum() {
		return areaNum;
	}

	public void setAreaNum(String areaNum) {
		this.areaNum = areaNum;
	}

	public boolean isvalid() {
		return isvalid;
	}

	public void setIsvalid(boolean isvalid) {
		this.isvalid = isvalid;
	}

	public List<String> getTableIDList() {
		return tableIDList;
	}

	public void setTableIDList(List<String> tableIDList) {
		this.tableIDList = tableIDList;
	}


}
