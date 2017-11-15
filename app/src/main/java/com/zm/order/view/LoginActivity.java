package com.zm.order.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.couchbase.lite.Expression;
import com.zm.order.R;

import java.util.List;

import application.ISharedPreferences;
import application.MyApplication;
import bean.kitchenmanage.user.UsersC;
import model.CDBHelper;
import presenter.ILoginPresenter;
import presenter.LoginPresentImpl;
import untils.MyLog;

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

    private String userNumber;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


         myApplication = (MyApplication) getApplication();
      //  myApplication.cancleSharePreferences();

          //查看是否有缓存

         String name = myApplication.getSharePreferences().getString("name","");

          MyLog.e(name);

          //无缓存
           if("".equals(name)){

                initView();

           }else {

               //有缓存
               userNumber = name;

               this.success();
           }



}

    private void initView() {

        name = (EditText) findViewById(R.id.loginName_edtTxt);

        password = (EditText) findViewById(R.id.loginPassword_edtTxt);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()==6){

                    if(inputMethodManager.isActive()){
                        inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }
        });

        saveloginstatueChk = (CheckBox) findViewById(R.id.saveLoginStatue_chk);

        submit = (Button) findViewById(R.id.login_bt);

        final ILoginPresenter iLoginPresenter = new LoginPresentImpl(this,getApplicationContext());

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()==3){

                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                    password.requestFocus();
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myApplication.mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        iLoginPresenter.doLogin();
                    }
                });


            }
        });
    }

    @Override
    public String[] getLoginInfo() {

        String[] info = new String[2];
        info[0] = name.getText().toString();
        info[1]= password.getText().toString();
        userNumber = info[0];

        return info;
    }

    @Override
    public void showError(String error) {

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void success() {

        List<UsersC> usersCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className")
                        .equalTo("UsersC"),null, UsersC.class);
        for(UsersC u : usersCList){

            if(u.getUserName().equals(userNumber)){
                myApplication.setUsersC(u);
                break;
            }
        }
        Intent intent = new Intent(this,DeskActivity.class);
        startActivity(intent);
        finish();
;

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
