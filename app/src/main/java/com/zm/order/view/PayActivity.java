package com.zm.order.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zm.order.R;

import model.ProgressBarasyncTask;

public class PayActivity extends AppCompatActivity {

private AlertDialog.Builder dialog;

    private final static int CASH = 1;
    private final static int ALIPAY = 2;
    private final static int WECHATPAY = 3;

    private  AlertDialog dg;
    private  Intent intent;
    private AppCompatCheckBox cash_cb;
    private AppCompatCheckBox alipay_cb;
    private AppCompatCheckBox wechatpay_cb;

    private TextView tableNumber_tv;
    private String tableNumber = null;

    private int flag;
    private TextView factPay_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();

        TextView total_tv = (TextView) findViewById(R.id.total_tv);
        total_tv.setText("消费金额: "+intent.getFloatExtra("total",0)+"元");
        tableNumber_tv = (TextView) findViewById(R.id.tableNumber_tv);
        tableNumber_tv.setText("桌号/");


        factPay_tv = (TextView) findViewById(R.id.fact_tv);

        cash_cb = (AppCompatCheckBox) findViewById(R.id.cash_cb);
        alipay_cb = (AppCompatCheckBox) findViewById(R.id.alipay_cb);
        wechatpay_cb = (AppCompatCheckBox) findViewById(R.id.wechatpay_cb);


        alipay_cb.setChecked(true);

        cash_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = CASH;

                if(alipay_cb.isChecked()){
                    alipay_cb.setChecked(false);
                }

                if(wechatpay_cb.isChecked()){
                    wechatpay_cb.setChecked(false);
                }
            }
        });
        alipay_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = ALIPAY;

                if(cash_cb.isChecked()){
                    cash_cb.setChecked(false);
                }

                if(wechatpay_cb.isChecked()){
                    wechatpay_cb.setChecked(false);
                }
            }
        });
        wechatpay_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = WECHATPAY;

                if(cash_cb.isChecked()){
                    cash_cb.setChecked(false);
                }

                if(alipay_cb.isChecked()){
                    alipay_cb.setChecked(false);
                }

            }
        });
        dialog = new AlertDialog.Builder(PayActivity.this);
        dialog.setView(getLayoutInflater().inflate(R.layout.view_print_dialog,null));
    }




    /**
     *
     * @param view
     */
    public void onClick(View view){

        if(tableNumber == null){

            Toast.makeText(this,"未选择座号!",Toast.LENGTH_LONG).show();

        }else {

            ProgressBarasyncTask progressBarasyncTask = new ProgressBarasyncTask(PayActivity.this);
            progressBarasyncTask.setDate(intent);
            progressBarasyncTask.execute();

        }


    }

    public void showDialog(){

        dg = dialog.show();
      }
    public void closeDialog(){


        dg.dismiss();
     }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                break;

            case R.id.action_sm:

                new IntentIntegrator(this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class) // 设置自定义的activity是CustomActivity
                        .initiateScan(); // 初始化扫描

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
// 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null) {

            if(intentResult.getContents() == null) {

                Toast.makeText(this,"扫描失败，请重新尝试。",Toast.LENGTH_LONG).show();
            } else {

                tableNumber = intentResult.getContents();
                intent.putExtra("tableNumber",tableNumber);

                tableNumber_tv.setText("桌号/ "+tableNumber);
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    public void turnMainActivity(){

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK,null);//携带参数返回到MainActivity

        finish();
    }

}

