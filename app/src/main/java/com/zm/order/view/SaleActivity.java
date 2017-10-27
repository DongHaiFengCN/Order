package com.zm.order.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zm.order.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import untils.MyLog;

public class SaleActivity extends AppCompatActivity {

    EventHandler eventHandler;
    @BindView(R.id.submitphone)
    Button submitphone;
    @BindView(R.id.submitcode)
    Button submitcode;
    @BindView(R.id.etAmountphone)
    EditText etAmountphone;
    @BindView(R.id.etcode)
    EditText etcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 注册监听器

        //初始化SMSSDK

        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        //  SMSSDK.setAskPermisionOnReadContact(boolShowInDialog);

        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(SaleActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                        // 验证通过读取数据库

                        Toast.makeText(SaleActivity.this,"验证通过！",Toast.LENGTH_SHORT).show();



                    }
                }
            }
        };

        SMSSDK.registerEventHandler(eventHandler);

    }

    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @OnClick({R.id.submitphone, R.id.submitcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitphone:
                if(TextUtils.isEmpty(etAmountphone.getText().toString())){

                    etAmountphone.setError("号码不能为空");

                }else {

                    SMSSDK.getVerificationCode("86",etAmountphone.getText().toString());
                }



                break;
            case R.id.submitcode:

                if(!TextUtils.isEmpty(etcode.getText().toString())){

                    etcode.setError("验证码不能为空！");

                }else {

                    SMSSDK.submitVerificationCode("+86", etAmountphone.getText().toString(), etcode.getText().toString());

                }
                break;
        }
    }
}
