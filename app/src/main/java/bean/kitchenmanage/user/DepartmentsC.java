/**   
 * @Title: Departments.java
 * @Package com.kitchenmanage.userM
 * @Description: 部门类文件
 * @author loongsun 
 * @date
 * @version V1.0   
 */
package bean.kitchenmanage.user;

import java.util.ArrayList;
import java.util.List;

public class DepartmentsC {

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
	 *
	 * 公司id
	 */
    private String companyId;
	/**
	 * 部门名称
	 */
	private String departmentName;
	/**
	 * 是否有效
	 */
	private boolean isvalid;
	/**
	 * 创建时间
	 */
	private String createdTime;

	/**
	 * 包含多个岗位对象
	 */
	private List<String> stationsIdlist;

	public DepartmentsC(String companyId) {
		this.companyId = companyId;
		this.className="DepartmentsC";
	}

	public DepartmentsC() {
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

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public boolean isvalid() {
		return isvalid;
	}

	public void setIsvalid(boolean isvalid) {
		this.isvalid = isvalid;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public List<String> getStationsIdlist() {
		return stationsIdlist;
	}

	public void setStationsIdlist(List<String> stationsIdlist) {
		this.stationsIdlist = stationsIdlist;
	}
	public void addStationsId(String id)
	{
		if(this.stationsIdlist==null)
			this.stationsIdlist=new ArrayList<>();
		this.stationsIdlist.add(id);
	}
}
