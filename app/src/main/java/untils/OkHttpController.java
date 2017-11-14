package untils;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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

   static boolean info = false;
    private final  static String url = "http://192.168.2.202:8080/pay/alipay/";
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
    public static boolean PostAlipayOrder(String total){

        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        //json为String类型的json数据
      //  RequestBody requestBody = RequestBody.create(JSON, "");

        final Request request = new Request.Builder()
                .url(url+total)
                .get()
                .build();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {



            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                info = true;
               // MyLog.e(response.body().string());


            }
        });
        return info;
    }




}
