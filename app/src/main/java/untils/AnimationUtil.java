package untils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/14 14:51
 * 修改人：donghaifeng
 * 修改时间：2017/9/14 14:51
 * 修改备注：
 */

public class AnimationUtil {

    private static final String TAG = AnimationUtil.class.getSimpleName();

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(150);
        return mHiddenAction;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(150);
        return mHiddenAction;
    }

}
