package com.zm.order.view;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.zm.order.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;
import untils.MyLog;
import untils.Tool;

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
        final IDBManager idbManager = DBFactory.get(DatabaseSource.CouchBase,this);

        List<Document> list = idbManager.getByClassName("MembersC");
        if(!list.isEmpty()){

            Iterator iterator = list.iterator();
            while (iterator.hasNext()){


                Document document = (Document) iterator.next();

                MyLog.e(document.getString("name"));
                MyLog.e(document.getString("tel"));
                MyLog.e(document.getString("cardNum"));

            }

        }

        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, final int result, final Object data) {


                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //回调完成
                        MyLog.e("回调完成");
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //提交并验证验证码成功！

                            //读取数据库操作

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                 etcode.setText("");
                                 etcode.setCursorVisible(false);

                                   Document document = idbManager.getMembers(etAmountphone.getText().toString());

                                    if(Tool.isNotEmpty(document)){




                                        MyLog.e(document.getString("name"));
                                        MyLog.e(document.getString("cardNum"));
                                        MyLog.e(document.getString("cardTypeId"));
                                        MyLog.e(document.getInt("status")+"");


                                    }else {

                                        Toast.makeText(SaleActivity.this,"用户不存在！",Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });



                        }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                            //获取验证码成功

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(SaleActivity.this,"获取验证码成功！",Toast.LENGTH_SHORT).show();
                                }
                            });

                          //  MyLog.e("获取验证码成功");
                        }

                    }else if(data instanceof Throwable){


                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(throwable.getMessage());
                            final String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                MyLog.e("错误信息！"+des);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(SaleActivity.this,des,Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
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

                if(TextUtils.isEmpty(etcode.getText().toString())){

                    etcode.setError("验证码不能为空！");

                }else {

                    SMSSDK.submitVerificationCode("+86", etAmountphone.getText().toString(), etcode.getText().toString());

                }
                break;
        }
    }
    /**
     * 发起https 请求
     * @param
     * @param
     * @return
     */
    /*public  static String requestData(String address ,String params){

        HttpURLConnection conn = null;
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
                public X509Certificate[] getAcceptedIssuers(){return null;}
                public void checkClientTrusted(X509Certificate[] certs, String authType){}
                public void checkServerTrusted(X509Certificate[] certs, String authType){}
            }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());

            //ip host verify
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return urlHostName.equals(session.getPeerHost());
                }
            };

            //set ip host verify
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// POST
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            // set params ;post params
            if (params!=null) {
                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.write(params.getBytes(Charset.forName("UTF-8")));
                out.flush();
                out.close();
            }
            conn.connect();
            //get result
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String result = convertStreamToString(conn.getInputStream());
                return result;
            } else {
                System.out.println(conn.getResponseCode() + " "+ conn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return null;
    }


    public static String convertStreamToString(InputStream is) {
        *//*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          *//*
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }*/
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

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
