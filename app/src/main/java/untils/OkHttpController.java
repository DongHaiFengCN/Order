package untils;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/10/12 14:59
 * 修改人：donghaifeng
 * 修改时间：2017/10/12 14:59
 * 修改备注：
 */

public class OkHttpController {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    /**
     * 设置链接读写超时
     */
    static{
        mOkHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }





}
