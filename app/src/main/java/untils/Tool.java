package untils;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import bean.Order;
import bean.kitchenmanage.promotion.PromotionC;
import bean.kitchenmanage.promotion.PromotionRuleC;

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
    //减法
    public static float substrct(float a,float b){

        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.subtract(b2).floatValue();

    }
    //乘法
    public static float multiply(float a,float b){

        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.multiply(b2).floatValue();

    }
    //除法
    public static float divide(float a,float b){

        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.divide(b2).floatValue();

    }

    //排序
    public static List<PromotionRuleC> Sort(List<PromotionRuleC> list){

        if(list.size() == 1){
            return list;
        }

            //从大到小排序
            if(list.size()>0)
            {
                PromotionRuleC tmp = null;
                for(int i=0;i<list.size();i++)
                {
                    for(int j=i+1;j<list.size();j++)
                    {
                        if(list.get(i).getCounts()<list.get(j).getCounts())
                        {
                            tmp = list.get(i);
                            list.set(i,list.get(j));
                            list.set(j,tmp);
                        }
                    }
                }
            }


        return list;
    }




}
