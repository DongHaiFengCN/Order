package bean.kitchenmanage.member;


import java.util.Date;

import bean.kitchenmanage.user.UsersC;

/**
 * @ClassName: ConsumLog
 * @Description: 会员卡刷卡记录
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */

public class ConsumLogC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 *
	 * docId
	 */
	private String _id;


	/**
	 * 会员id
	 */
	private String membersId;
	/**
	 * 卡号
	 */
	private String cardNo;
	/**
	 *订单号
	 */
	private String orderNo;
	/**
	 *消费金额
	 */
	private float cardConsum;       //卡消费金额
	/**
	 * 创建日期
	 */
	private Date time;
	/**
	 *录入人
	 */
	private UsersC operator;

	public ConsumLogC() {

	}

	public ConsumLogC(String company_id) {
		this.channelId = company_id;
		this.className="ConsumLogC";
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

	public String getMembersId() {
		return membersId;
	}

	public void setMembersId(String membersId) {
		this.membersId = membersId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public float getCardConsum() {
		return cardConsum;
	}

	public void setCardConsum(float cardConsum) {
		this.cardConsum = cardConsum;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public UsersC getOperator() {
		return operator;
	}

	public void setOperator(UsersC operator) {
		this.operator = operator;
	}
}
