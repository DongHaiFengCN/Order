package com.zm.order.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Expression;
import com.zm.order.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.ReturnOrderC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CDBHelper;

/**
 * Created by lenovo on 2017/12/13.
 */

public class ShowParticularsActivity extends Activity {

    @BindView(R.id.show_listView)
    ListView showListView;
    @BindView(R.id.show_but_dc)
    Button showButDc;
    @BindView(R.id.show_but_md)
    Button showButMd;
    @BindView(R.id.show_tv_sl)
    TextView showTvSl;
    @BindView(R.id.show_img)
    ImageView showImg;

    ShowParticularsAdapter adatper;
    private List<GoodsC> goodsCList;
    private MyApplication myapp;
    private float all = 0f;
    private ImageView getShowImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        myapp = (MyApplication) getApplication();
        goodsCList = new ArrayList<>();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setAll();
        adatper = new ShowParticularsAdapter(this);
        adatper.setGoodsCs(goodsCList);
        showListView.setAdapter(adatper);

        adatper.setLinClickListener(new ShowParticularsAdapter.OnLinClickListener() {
            @Override
            public void getLinClick(ImageView imageView) {
                //getShowImg = imageView;
            }
        });

        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowParticularsActivity.this);
                View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog,null);
                alertDialog.setView(view1);
                TextView title = view1.findViewById(R.id.dialog_tuicai_title);
                title.setText("是否退菜");
                final AlertDialog builder = alertDialog.create();
                Button shi = view1.findViewById(R.id.dialog_tuicai_qd);
                shi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //1\
                        GoodsC goodsC = goodsCList.get(position);
                        //2\
                        OrderC orderC = CDBHelper.getObjById(getApplicationContext(),goodsC.getOrder(),OrderC.class);
                        //3\
                        //打印goodslist
                        {
                            Log.e("orderC.getGoodsList()",orderC.getGoodsList().size()+"");
                        }
                        for (int i = 0;i<orderC.getGoodsList().size();i++){

                            orderC.getGoodsList().get(i).setRetreatGreens(1);

                            if (orderC.getGoodsList().get(i).getRetreatGreens() == 1){
                                float all = MyBigDecimal.sub(orderC.getAllPrice(),orderC.getGoodsList().get(i).getAllPrice(),1);
                                orderC.setAllPrice(all);
                            }

                        }


                        //打印goodslist
                        {
                            Log.e("orderC.getGoodsList()",orderC.getGoodsList().size()+"");
                        }
                        //4\

                        if (orderC.getGoodsList().size() == 0){
                            CDBHelper.deleDocumentById(getApplicationContext(),orderC.get_id());
                        }else{
                            CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                        }

                        CDBHelper.deleDocumentById(getApplicationContext(),goodsC.get_id());
                        //5\

                        ReturnOrderC returnOrderC = new ReturnOrderC(myapp.getCompany_ID());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strtime = df.format(new Date());
                        returnOrderC.setCheckTime(strtime);
                        returnOrderC.addGoods(goodsC);
                        returnOrderC.setTableNo(myapp.getTable_sel_obj().getTableNum());
                        returnOrderC.setTableName(myapp.getTable_sel_obj().getTableName());
                        returnOrderC.setOperator(myapp.getUsersC());
                        returnOrderC.setPay(goodsCList.get(position).getAllPrice());
                        CDBHelper.createAndUpdate(getApplicationContext(),returnOrderC);
                        //6

                        if (getShowImg != null){
                            getShowImg.setBackgroundResource(R.mipmap.icon_show_tui);
                        }

                        //goodsCList.remove(position);
                        setAll();
                        adatper.notifyDataSetChanged();
                        builder.dismiss();
                    }
                });
                Button fou = view1.findViewById(R.id.dialog_tuicai_qx);
                fou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                builder.show();

            }
        });

    }

    private void setAll(){
        goodsCList.clear();
        all = 0f;
        List<OrderC> orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo("OrderC")
                        .and(Expression.property("tableNo").equalTo(myapp.getTable_sel_obj().getTableNum()))
                        .and(Expression.property("orderState").equalTo(1))
                , null
                , OrderC.class);
        for (OrderC orderC : orderCList) {

            for (int i = 0; i < orderC.getGoodsList().size(); i++) {
                goodsCList.add(orderC.getGoodsList().get(i));
            }
            all += orderC.getAllPrice();
        }
        showTvSl.setText("共：" + goodsCList.size() + "道菜，总价："+all+"元");
    }

    @OnClick({R.id.show_but_dc, R.id.show_but_md,R.id.show_img})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.show_but_dc:
                intent = new Intent(ShowParticularsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.show_but_md:
                intent = new Intent(ShowParticularsActivity.this, PayActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.show_img:
                finish();
                break;

            default:

                break;
        }
    }
}
