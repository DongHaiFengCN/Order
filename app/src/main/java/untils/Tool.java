package untils;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/10/30 10:07
 * 修改人：donghaifeng
 * 修改时间：2017/10/30 10:07
 * 修改备注：
 */

public class Tool {

    public static boolean isNotEmpty(Object c){

        if(c == null){

            return false;
        }
        if(c instanceof String){

            if(TextUtils.isEmpty((CharSequence) c)){

                return false;
            }
        }
        if(c instanceof List){

            if(((List) c).isEmpty()){

                return false;
            }
        }
        return true;
    }

    public static boolean bindView(View view,String value){


     if(view instanceof TextView){


        if(TextUtils.isEmpty(value)){

             ((TextView) view).setText("");

            return false;

         }else{

             ((TextView) view).setText(value);

         }


     }else if(view instanceof EditText) {


         if(TextUtils.isEmpty(value)){

             ((EditText) view).setText("");
             return false;

         }else {

             ((EditText) view).setText(value);


         }
     }

        return true;
    }
}
