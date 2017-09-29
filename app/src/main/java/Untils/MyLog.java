package Untils;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 12:03
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 12:03
 * 修改备注：
 */

public class MyLog {

    static final String TAG ="DOAING";

    static boolean FLAG =false;

    public static void e(String info){


        if(FLAG){

            android.util.Log.e(TAG,info);

        }

    }
}
