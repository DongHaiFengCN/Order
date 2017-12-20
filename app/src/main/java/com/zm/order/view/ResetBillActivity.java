package com.zm.order.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zm.order.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.order.CheckOrderC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;

public class ResetBillActivity extends AppCompatActivity {
    @BindView(R.id.table_number)
    TextView tableNumberTv;
    @BindView(R.id.time)
    TextView timeTv;
    @BindView(R.id.item_info)
    ListView itemInfoLv;
    @BindView(R.id.pay_tv)
    TextView payTv;
    @BindView(R.id.needpay_tv)
    TextView needpayTv;


    private List<OrderC> orderCList;

    private List<GoodsC> goodsCList;

    private CheckOrderC checkOrderC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_bill);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置显示订单详情
     *
     * @param checkOrderC
     */

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setMessage(CheckOrderC checkOrderC) {

        this.checkOrderC = checkOrderC;

        tableNumberTv.setText(" 桌号：" + checkOrderC.getTableNo());

        timeTv.setText(" 结账时间：" + checkOrderC.getCheckTime());

        needpayTv.setText("实付：" + checkOrderC.getNeedPay());
        payTv.setText("总计："+checkOrderC.getPay());

        orderCList = checkOrderC.getOrderList();

        goodsCList = new ArrayList<>();

        for (int i = 0; i < orderCList.size(); i++) {

            goodsCList.addAll(orderCList.get(i).getGoodsList());
        }

        ShowParticularsAdapter showParticularsAdapter = new ShowParticularsAdapter(ResetBillActivity.this);

        showParticularsAdapter.setGoodsCs(goodsCList);

        itemInfoLv.setAdapter(showParticularsAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 重新设置order状态
     */

    public void onClick(View view) {

        for (int i = 0; i < orderCList.size(); i++) {

            OrderC orderC = orderCList.get(i);
            orderC.setOrderState(1);
            CDBHelper.createAndUpdate(getApplicationContext(), orderC);
        }

        //删除之前的checkorder记录
        CDBHelper.deleDocumentById(getApplicationContext(),checkOrderC.get_id());

        startActivity(new Intent(ResetBillActivity.this, PayActivity.class));
    }
}
