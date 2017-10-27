/**   
* @Title: Point.java 
* @Package com.canyin.db4o.renyuan 
* @Description: TODO(用一句话描述该文件做什么) 
* @author loongsun 
* @date 2014-7-29 上午1:06:02 
* @version V1.0   
*/
package bean.kitchenmanage.user;

import java.util.List;

/**
 * @ClassName: Point 
 * @Description: 公司类文件
 * @author loongsun
 * @date 2014-7-29 上午1:06:02 
 *  
 */
public class CompanyC {

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
	 *网点名称
	 */
	private String pointName;
	/**
	 *网点地址
	 */
	private String pointAdress;
	/**
	 * 百度地图坐标，经度
	 */
	private double  x;
	/**
	 * 百度地图坐标，纬度
	 */
	private double  y;
	/**
	 *联系电话
	 */
	private String telephone;
	/**
	 *联系人
	 */
	private String linkman;
	/**
	 *是否有效
	 */
	private boolean isvalid;
	/**
	 * 包含的多个部门对象
	 */
	private List<String> deplist;
	/**
	 * 营业启始时间  格式为  HH:mm:ss
	 */
	private String businessStartHours;
	/**
	 * 营业结束时间  格式为  HH:mm:ss
	 */
	private String businessEndHours;

	public CompanyC() {
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

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getPointAdress() {
		return pointAdress;
	}

	public void setPointAdress(String pointAdress) {
		this.pointAdress = pointAdress;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public boolean isvalid() {
		return isvalid;
	}

	public void setIsvalid(boolean isvalid) {
		this.isvalid = isvalid;
	}

	public List<String> getDeplist() {
		return deplist;
	}

	public void setDeplist(List<String> deplist) {
		this.deplist = deplist;
	}

	public String getBusinessStartHours() {
		return businessStartHours;
	}

	public void setBusinessStartHours(String businessStartHours) {
		this.businessStartHours = businessStartHours;
	}

	public String getBusinessEndHours() {
		return businessEndHours;
	}

	public void setBusinessEndHours(String businessEndHours) {
		this.businessEndHours = businessEndHours;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
}
