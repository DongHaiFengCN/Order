package com.zm.order.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import untils.AnimationUtil;

/**
 * Created by lenovo on 2017/10/25.
 */

public class SeekT9Activity extends Activity {


    @BindView(R.id.activity_seek_list)
    ListView activitySeekList;
    @BindView(R.id.activity_seek_edit)
    EditText activitySeekEdit;
    @BindView(R.id.ibtn_key_1)
    ImageView ibtnKey1;
    @BindView(R.id.ibtn_key_2)
    ImageView ibtnKey2;
    @BindView(R.id.ibtn_key_3)
    ImageView ibtnKey3;
    @BindView(R.id.ibtn_key_4)
    ImageView ibtnKey4;
    @BindView(R.id.ibtn_key_5)
    ImageView ibtnKey5;
    @BindView(R.id.ibtn_key_6)
    ImageView ibtnKey6;
    @BindView(R.id.ibtn_key_7)
    ImageView ibtnKey7;
    @BindView(R.id.ibtn_key_8)
    ImageView ibtnKey8;
    @BindView(R.id.ibtn_key_9)
    ImageView ibtnKey9;
    @BindView(R.id.ibtn_key_l)
    ImageView ibtnKeyL;
    @BindView(R.id.ibtn_key_0)
    ImageView ibtnKey0;
    @BindView(R.id.ibtn_key_r)
    ImageView ibtnKeyR;
    @BindView(R.id.ibtn_key_del)
    ImageView ibtnKeyDel;
    @BindView(R.id.seek_shade)
    ImageView seekShade;
    @BindView(R.id.seek_textView5)
    TextView seekTextView5;
    @BindView(R.id.seek_delet)
    ImageView seekDelet;
    @BindView(R.id.seek_order_lv)
    ListView seekOrderLv;
    @BindView(R.id.seek_orderList)
    LinearLayout seekOrderList;
    @BindView(R.id.seek_total_tv)
    TextView seekTotalTv;
    @BindView(R.id.seek_ok_tv)
    TextView seekOkTv;
    @BindView(R.id.seek_car)
    ImageView seekCar;
    @BindView(R.id.seek_point)
    TextView seekPoint;
    private ArrayList<String> list = new ArrayList<>();
    private boolean flag = true;
    private float total = 0.0f;
    private OrderAdapter o;
    private int point = 0;
    private List<SparseArray<Object>> orderItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        //初始化订单的数据，绑定数据源的信息。

        o = new OrderAdapter(orderItem, SeekT9Activity.this);
        //seekOrderLv.setAdapter(o);
        //监听orderItem的增加删除，设置总价以及总数量, flag ？+ ：-,price 单价 ,sum 当前item的个数。

        o.setOnchangeListener(new OrderAdapter.OnchangeListener() {
            @Override
            public void onchangeListener(boolean flag, float price, int sum) {

                if (flag) {

                    total += price;

                    seekTotalTv.setText(total + "元");


                } else {

                    total -= price;

                    seekTotalTv.setText(total + "元");

                    if (sum == 0) {

                        point--;

                        seekPoint.setText(point + "");

                        if (point == 0) {

                            seekPoint.setVisibility(View.INVISIBLE);
                        }


                    }

                }
            }
        });

    }

    /**
     * 清空订单列表
     */

    private void clearOrder() {

        point = 0;
        seekPoint.setVisibility(View.INVISIBLE);

        seekTotalTv.setText("0元");
        total = 0;

        orderItem.clear();
        o.notifyDataSetChanged();
    }

    @OnClick({ R.id.activity_seek_edit, R.id.ibtn_key_1, R.id.ibtn_key_2, R.id.ibtn_key_3, R.id.ibtn_key_4, R.id.ibtn_key_5, R.id.ibtn_key_6, R.id.ibtn_key_7, R.id.ibtn_key_8, R.id.ibtn_key_9, R.id.ibtn_key_l, R.id.ibtn_key_0, R.id.ibtn_key_r, R.id.ibtn_key_del, R.id.seek_shade, R.id.seek_textView5, R.id.seek_delet, R.id.seek_total_tv, R.id.seek_ok_tv, R.id.seek_car, R.id.seek_point})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_seek_edit:
                break;
            case R.id.ibtn_key_1:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "1");
                break;
            case R.id.ibtn_key_2:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "2");
                break;
            case R.id.ibtn_key_3:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "3");
                break;
            case R.id.ibtn_key_4:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "4");
                break;
            case R.id.ibtn_key_5:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "5");
                break;
            case R.id.ibtn_key_6:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "6");
                break;
            case R.id.ibtn_key_7:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "7");
                break;
            case R.id.ibtn_key_8:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "8");
                break;
            case R.id.ibtn_key_9:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "9");
                break;
            case R.id.ibtn_key_l:
                break;
            case R.id.ibtn_key_0:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "0");
                break;
            case R.id.ibtn_key_r:
                break;
            case R.id.ibtn_key_del:
                int length = activitySeekEdit.getSelectionEnd();
                if (length > 1)
                {
                    activitySeekEdit.getText().delete(length - 1, length);
                }
                if (length == 1)
                {
                    activitySeekEdit.getText().delete(length - 1, length);
                }
                break;
            case R.id.seek_textView5:
                break;
            case R.id.seek_delet:
                clearOrder();
                break;
            case R.id.seek_orderList:
                break;
            case R.id.seek_total_tv:
                break;
            case R.id.seek_ok_tv:

                if (total > 0) {

                    Intent intent = new Intent(SeekT9Activity.this, PayActivity.class);
                    //intent.putExtra("Order", (Serializable) orderItem);
                    intent.putExtra("total", total);
                    startActivityForResult(intent, 1);

                    //如果order列表开启状态就关闭
                    if (!flag) {
                        seekOrderList.setAnimation(AnimationUtil.moveToViewBottom());
                        seekOrderList.setVisibility(View.GONE);
                        seekShade.animate()
                                .alpha(0f)
                                .setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        seekShade.setVisibility(View.GONE);
                                    }
                                });

                        flag = true;
                    }

                } else {

                    Toast.makeText(SeekT9Activity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.seek_car:

                if (flag) {

                    seekOrderList.setAnimation(AnimationUtil.moveToViewLocation());
                    seekOrderList.setVisibility(View.VISIBLE);
                    seekShade.setVisibility(View.VISIBLE);
                    seekShade.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setListener(null);
                    flag = false;

                } else {


                    seekOrderList.setAnimation(AnimationUtil.moveToViewBottom());
                    seekOrderList.setVisibility(View.GONE);

                    seekShade.animate()
                            .alpha(0f)
                            .setDuration(400)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    seekShade.setVisibility(View.GONE);
                                }
                            });

                    flag = true;

                }
                break;
            case R.id.seek_point:
                break;
        }
    }

    /*@OnClick({R.id.activity_seek_list, R.id.activity_seek_edit, R.id.ibtn_key_1, R.id.ibtn_key_2, R.id.ibtn_key_3, R.id.ibtn_key_4, R.id.ibtn_key_5, R.id.ibtn_key_6, R.id.ibtn_key_7, R.id.ibtn_key_8, R.id.ibtn_key_9, R.id.ibtn_key_l, R.id.ibtn_key_0, R.id.ibtn_key_r, R.id.ibtn_key_del,  R.id.seek_textView5, R.id.seek_delet, R.id.seek_order_lv, R.id.seek_orderList, R.id.seek_total_tv, R.id.seek_ok_tv, R.id.seek_car, R.id.seek_point})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_seek_list:
                break;
            case R.id.activity_seek_edit:
                break;
            case R.id.ibtn_key_1:
                //activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "1");
                activitySeekEdit.setText("1");
                break;
            case R.id.ibtn_key_2:
                activitySeekEdit.setText("2");
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "2");
                break;
            case R.id.ibtn_key_3:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "3");
                break;
            case R.id.ibtn_key_4:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "4");
                break;
            case R.id.ibtn_key_5:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "5");
                break;
            case R.id.ibtn_key_6:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "6");
                break;
            case R.id.ibtn_key_7:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "7");
                break;
            case R.id.ibtn_key_8:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "8");
                break;
            case R.id.ibtn_key_9:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "9");
                break;
            case R.id.ibtn_key_l:
                break;
            case R.id.ibtn_key_0:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "0");
                break;
            case R.id.ibtn_key_r:
                break;
            case R.id.ibtn_key_del:
                break;
            case R.id.seek_textView5:
                break;
            case R.id.seek_delet:
                clearOrder();
                break;
            case R.id.seek_order_lv:
                break;
            case R.id.seek_orderList:
                break;
            case R.id.seek_total_tv:
                break;
            case R.id.seek_ok_tv:

                if (total > 0) {

                    Intent intent = new Intent(SeekT9Activity.this, PayActivity.class);
                    //intent.putExtra("Order", (Serializable) orderItem);
                    intent.putExtra("total", total);
                    startActivityForResult(intent, 1);

                    //如果order列表开启状态就关闭
                    if (!flag) {
                        seekOrderList.setAnimation(AnimationUtil.moveToViewBottom());
                        seekOrderList.setVisibility(View.GONE);
                        seekShade.animate()
                                .alpha(0f)
                                .setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        seekShade.setVisibility(View.GONE);
                                    }
                                });

                        flag = true;
                    }

                } else {

                    Toast.makeText(SeekT9Activity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.seek_car:

                if (flag) {

                    seekOrderList.setAnimation(AnimationUtil.moveToViewLocation());
                    seekOrderList.setVisibility(View.VISIBLE);
                    seekShade.setVisibility(View.VISIBLE);
                    seekShade.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setListener(null);
                    flag = false;

                } else {

                    seekOrderList.setAnimation(AnimationUtil.moveToViewBottom());
                    seekOrderList.setVisibility(View.GONE);

                    seekShade.animate()
                            .alpha(0f)
                            .setDuration(400)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    seekShade.setVisibility(View.GONE);
                                }
                            });

                    flag = true;

                }
                break;
            case R.id.seek_point:
                break;
        }
    }*/
}