package com.zm.order.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zm.order.R;

import application.ISharedPreferences;
import application.MyApplication;
import presenter.ILoginPresenter;
import presenter.LoginPresentImpl;

/**
 *
 *
 * @author 董海峰
 * @date 2017/10/25
 */
public class LoginActivity extends AppCompatActivity implements ILoginView, ISharedPreferences{
    private EditText name;
    private EditText password;
    private Button submit;
    private CheckBox saveloginstatueChk;
    private  MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //aaaaab

        setContentView(R.layout.activity_login);


         myApplication = (MyApplication) getApplication();

          //查看是否有缓存

         String flag = myApplication.getSharePreferences().getString("name","");

           if("".equals(flag)){

                initView();


           }else {

                this.success();
           }


           //设置登录用户名称全局变量

        myApplication.setLoginName(name.getText().toString());

}

    private void initView() {

        name = (EditText) findViewById(R.id.loginName_edtTxt);

        password = (EditText) findViewById(R.id.loginPassword_edtTxt);

        saveloginstatueChk = (CheckBox) findViewById(R.id.saveLoginStatue_chk);

        submit = (Button) findViewById(R.id.login_bt);

        final ILoginPresenter iLoginPresenter = new LoginPresentImpl(this,getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iLoginPresenter.doLogin();

            }
        });
    }

    @Override
    public String[] getLoginInfo() {

        String[] info = new String[2];
        info[0] = name.getText().toString();
        info[1]= password.getText().toString();
        return info;
    }

    @Override
    public void showError(String error) {

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void success() {

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean isSave() {

        return saveloginstatueChk.isChecked();
    }


    @Override
    public SharedPreferences getSharePreferences() {

        return myApplication.getSharePreferences();
    }

    @Override
    public boolean cancleSharePreferences() {

        return myApplication.cancleSharePreferences();
    }
}
