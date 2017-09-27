package bean;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 10:11
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 10:11
 * 修改备注：
 */

public class LoginUserBean {

    private String mName ;
    private String mPassword ;



    private String className;
    private String channelId;

    public LoginUserBean () {


        this.className = "LoginUserBean";
        channelId = "11";
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
