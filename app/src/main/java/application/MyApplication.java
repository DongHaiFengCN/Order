package application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.bugly.crashreport.CrashReport;

import okhttp3.OkHttpClient;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 13:26
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 13:26
 * 修改备注：
 */

public class MyApplication extends Application implements ISharedPreferences{



    OkHttpClient okHttpClient;
    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(getApplicationContext(), "1b0a55dc94", true);

    }


    @Override
    public SharedPreferences getSharePreferences() {

        return getSharedPreferences("loginUser", Context.MODE_PRIVATE);
    }

    @Override
    public boolean cancleSharePreferences() {

        return getSharePreferences().edit().clear().commit();
    }


}
