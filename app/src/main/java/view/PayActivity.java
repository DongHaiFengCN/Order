package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import Untils.MyLog;
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

    private int flag;

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
        total_tv.setText("消费金额/"+intent.getFloatExtra("total",0)+"元");

        TextView fact_tv = (TextView) findViewById(R.id.fact_tv);

        cash_cb = (AppCompatCheckBox) findViewById(R.id.cash_cb);
        alipay_cb = (AppCompatCheckBox) findViewById(R.id.alipay_cb);
        wechatpay_cb = (AppCompatCheckBox) findViewById(R.id.wechatpay_cb);



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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param view
     */
    public void onClick(View view){

        ProgressBarasyncTask progressBarasyncTask = new ProgressBarasyncTask(PayActivity.this);
        progressBarasyncTask.setDate(intent);

        progressBarasyncTask.execute();

    }

    public void showDialog(){

        dg = dialog.show();
      }
    public void closeDialog(){


        dg.dismiss();
     }
}

