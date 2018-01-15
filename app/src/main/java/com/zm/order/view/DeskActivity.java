package com.zm.order.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.zm.order.BuildConfig;
import com.zm.order.R;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import application.MyApplication;
import bean.kitchenmanage.order.CheckOrderC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.qrcode.qrcodeC;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.table.TableC;
import model.AreaAdapter;
import model.CDBHelper;
import model.LiveTableRecyclerAdapter;
import untils.MyLog;
import untils.Tool;

import static untils.Tool.isFastDoubleClick;

public class DeskActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewArea;
    private List<AreaC> areaCList;
    private AreaAdapter areaAdapter;
    private RecyclerView listViewDesk;
    private LiveTableRecyclerAdapter tableadapter;
    private int flag = 0;


    private MyApplication myapp;
    private Handler uiHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what) {
                case 1: //语音播放
                    String id = (String)msg.obj;
                    showDeskListView(id);
                    break;
                case 2: //没有订单
                   Toast.makeText(DeskActivity.this,"没有订单！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        myapp= (MyApplication) getApplicationContext();
      //  myapp.initDishesData();

        initWidget();

        Log.e("DeskActivity","oncreate");


}

    private void initWidget()
    {

        db = myapp.getDatabase();
        if(db == null) throw new IllegalArgumentException();
        areaAdapter = new AreaAdapter(this, db);

        listViewArea = (ListView)findViewById(R.id.lv_area);
        listViewArea.setAdapter(areaAdapter);
        listViewArea.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                areaAdapter.setSelectItem(i);
                final String id = areaAdapter.getItem(i);
                Message msg = Message.obtain();
                msg.obj = id;
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
        });
        if(areaAdapter.getCount()>0)
        {
            areaAdapter.setSelectItem(0);
            showDeskListView(areaAdapter.getItem(0));
        }

    }

    private void showDeskListView(String areaId)
    {

        if(tableadapter!=null)
            tableadapter.StopQuery();

        tableadapter=new LiveTableRecyclerAdapter(this,db,areaId);
        tableadapter.setOnItemClickListener(new LiveTableRecyclerAdapter.onRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClick(View view,Object data)
            {

                String tableId= (String)data;
                final TableC  tableC =  CDBHelper.getObjById(getApplicationContext(),tableId,TableC.class);
                myapp.setTable_sel_obj(tableC);
                if(tableC.getState()!=2)
                {
                    final EditText  editText = new EditText(DeskActivity.this);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                    LinearLayout linearLayout =new LinearLayout(DeskActivity.this);

                    //设置控件居中显示
                    linearLayout.setGravity(Gravity.CENTER);

                    //设置子控件在线性布局下的参数设置对象（什么布局就用什么的）

                    LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(

                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    //设置margins属性

                    params.setMargins(10,10,10,10);

                    //设置控件参数
                    editText.setLayoutParams(params);

                    //设置输入类型为数字
                    editText.setInputType((InputType.TYPE_CLASS_NUMBER));

                    //添加控件到布局
                    linearLayout.addView(editText);

                    editText.setHint("最多人数："+tableC.getMaxPersons()+"最小人数 : "+tableC.getMinConsum());


                    AlertDialog.Builder builder = new AlertDialog.Builder(DeskActivity.this);

                    builder.setTitle("设置就餐人数");
                    builder.setView(linearLayout);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(editText.getText().toString())){

                                editText.setError("人数不能为空");

                            }else if("0".equals(editText.getText().toString())){

                                editText.setError("人数不能为0");

                            }else {

                                //设置就餐人数，转跳

                                tableC.setState(2);
                                tableC.setCurrentPersions(Integer.valueOf(editText.getText().toString()));
                                //设置全局Table
                                myapp.setTable_sel_obj(tableC);
                                CDBHelper.createAndUpdate(getApplicationContext(),tableC);

                                dialog.dismiss();

                                //转跳点餐界面
                                turnMainActivity();


                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    final AlertDialog alertDialog = builder.create();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        public void onShow(DialogInterface dialog) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                    alertDialog.show();

                }else {
                    List<String> orderCList= CDBHelper.getIdsByWhere(getApplicationContext(),
                            Expression.property("className").equalTo("OrderC")
                                    .and(Expression.property("tableNo").equalTo(tableC.getTableNum()))
                                    .and(Expression.property("orderState").equalTo(1))
                                    ,null
                            );

                    Log.e("orderCList","orderCList.size()"+orderCList.size()+"-----"+tableC.getTableNum());
                    if (orderCList.size() > 0 )
                    {
                        //使用状态下跳到查看订单界面
                        Intent mainIntent = new Intent();
                        mainIntent.setClass(DeskActivity.this, ShowParticularsActivity.class);
                        startActivity(mainIntent);
                    }else {
                        //转跳点餐界面
                        turnMainActivity();

                    }

                }

            }
            @Override
            public void onItemLongClick(View view, final Object data)
            {
                String tableId = (String)data;
                final TableC tableC = CDBHelper.getObjById(getApplicationContext(),tableId,TableC.class);
                myapp.setTable_sel_obj(tableC);

                //空闲状态下重置上一次未买单状态
                if(tableC.getState()==0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(DeskActivity.this);
                    builder.setTitle("重置最近一次账单");

                    builder.setNegativeButton("确定",null);
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                   final AlertDialog alertDialog = builder.show();

                   alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {

                           final ProgressDialog proDialog = android.app.ProgressDialog.show(DeskActivity.this, "重置", "正在配置订单请稍等~");

                           myapp.mExecutor.execute(new Runnable() {
                               @Override
                               public void run() {

                                   try {
                                       CDBHelper.db.inBatch(new TimerTask() {
                                                                @Override
                                                                public void run() {
                                                                    CheckOrderC checkOrderC = null;
                                                                    //老数据没有字段遍历查询
                                                                    if(tableC.getLastCheckOrderId() == null || tableC.getLastCheckOrderId().isEmpty()){

                                                                        Date date = new Date();
                                                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


                                                                        //查询当日的订单
                                                                        List<CheckOrderC> checkOrderCS = CDBHelper.getObjByWhere(getApplicationContext()
                                                                                , Expression.property("className").equalTo("CheckOrderC")
                                                                                        .and(Expression.property("checkTime").like(formatter.format(date)+"%"))
                                                                                , null, CheckOrderC.class);

                                                                        Iterator<CheckOrderC> iterator = checkOrderCS.iterator();

                                                                        //移除不是当前桌的订单
                                                                        while (iterator.hasNext()){
                                                                            CheckOrderC c = iterator.next();
                                                                            if(!c.getTableNo().equals(tableC.getTableNum())){
                                                                                iterator.remove();
                                                                            }
                                                                        }
                                                                        if(checkOrderCS.size() > 0){
                                                                            List<String> dateList = new ArrayList<>();
                                                                            //获取当前桌订单今日时间集合
                                                                            for (int i1 = 0; i1 < checkOrderCS.size(); i1++) {
                                                                                dateList.add(checkOrderCS.get(i1).getCheckTime());
                                                                            }

                                                                            //得到最近订单的坐标
                                                                            int f =  Tool.getLastCheckOrder(dateList);
                                                                            checkOrderC = checkOrderCS.get(f);
                                                                            for (int i = 0; i < checkOrderC.getOrderList().size(); i++) {

                                                                                OrderC orderC = checkOrderC.getOrderList().get(i);
                                                                                orderC.setOrderState(1);
                                                                                CDBHelper.createAndUpdate(getApplicationContext(), orderC);
                                                                            }

                                                                            //删除之前的checkorder记录
                                                                            CDBHelper.deleDocumentById(getApplicationContext(),checkOrderC.get_id());

                                                                            tableC.setState(2);
                                                                            CDBHelper.createAndUpdate(getApplicationContext(), tableC);

                                                                        }else {

                                                                            Message msg = Message.obtain();
                                                                            msg.what = 2;
                                                                            uiHandler.sendMessage(msg);
                                                                        }

                                                                    }else {

                                                                        //新数据查询


                                                                        checkOrderC = CDBHelper.getObjById(getApplicationContext(),tableC.getLastCheckOrderId(),CheckOrderC.class);
                                                                        if (checkOrderC == null&&checkOrderC.getOrderList().size()==0){
                                                                            return;
                                                                        }

                                                                        for (int i = 0; i < checkOrderC.getOrderList().size(); i++) {

                                                                            OrderC orderC = checkOrderC.getOrderList().get(i);
                                                                            orderC.setOrderState(1);
                                                                            CDBHelper.createAndUpdate(getApplicationContext(), orderC);
                                                                        }

                                                                        //删除之前的checkorder记录
                                                                        CDBHelper.deleDocumentById(getApplicationContext(),checkOrderC.get_id());

                                                                        tableC.setState(2);
                                                                        CDBHelper.createAndUpdate(getApplicationContext(), tableC);

                                                                    }

                                                                }
                                                            });
                                   } catch (CouchbaseLiteException e) {
                                       e.printStackTrace();
                                   }
                                   proDialog.dismiss();//关闭proDialog

                               }
                           });

                           //获取今天日期

                           alertDialog.dismiss();

                       }
                   });
                }else {
                    //使用&&预定状态
                    List<String> orderCList = CDBHelper.getIdsByWhere(getApplicationContext(),
                            Expression.property("className").equalTo("OrderC")
                                    .and(Expression.property("tableNo").equalTo(tableC.getTableNum()))
                                    .and(Expression.property("orderState").equalTo(1))
                                    .and(Expression.property("orderCType").notEqualTo(1))
                            ,null);

                    if(orderCList.size()>0)//有未买单订单，可以买单
                    {
                        android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(DeskActivity.this);
                        dialog1.setTitle("是否买单？").setCancelable(false);
                        dialog1.setNegativeButton("是",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent mainIntent = new Intent();
                                        mainIntent.setClass(DeskActivity.this, PayActivity.class);
                                        startActivity(mainIntent);
                                    }
                                }).setPositiveButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1)
                                    {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                    }
                    else
                    {
                        android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(DeskActivity.this);
                        dialog1.setTitle("是否消台？").setCancelable(false);
                        dialog1.setNegativeButton("是",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        tableC.setState(0);
                                        CDBHelper.createAndUpdate(getApplicationContext(),tableC);
                                        myapp.setTable_sel_obj(tableC);
                                        List<Document> orderCList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                                                Expression.property("className").equalTo("OrderC")
                                                        .and(Expression.property("tableNo").equalTo(tableC.getTableNum()))
                                                        .and(Expression.property("orderState").equalTo(1))
                                                ,null
                                                );
                                        for (Document doc : orderCList){
                                            doc.setInt("orderState",0);
                                            CDBHelper.saveDocument(getApplicationContext(),doc);

                                        }
                                    }
                                }).setPositiveButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1)
                                    {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                    }
                }
            }
        });

        listViewDesk = (RecyclerView)findViewById(R.id.lv_desk);
        listViewDesk.setItemAnimator(new DefaultItemAnimator());
        listViewDesk.setLayoutManager(new GridLayoutManager(this,3));
        listViewDesk.setAdapter(tableadapter);

    }

    private void turnMainActivity() {
        Intent mainIntent = new Intent();
        mainIntent.setClass(DeskActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_desk, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
            case R.id.action_alipay:

                flag = 1;
                turnScan();


                break;
            case R.id.action_wechat:

                flag = 2;
                turnScan();
                break;
                default:
                    break;
        }
        return true;
    }



    private void turnScan() {

        IntentIntegrator intentIntegrator =  new IntentIntegrator(this);

        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("请扫描二维码");
        intentIntegrator.setCaptureActivity(ScanActivity.class); // 设置自定义的activity是ScanActivity
        intentIntegrator.initiateScan(); // 初始化扫描
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("DeskActivity","onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        List<qrcodeC> qrcodeCS = CDBHelper.getObjByClass(getApplicationContext(),qrcodeC.class);


        if (result != null) {

            String authCode = result.getContents();

            //修改二维码
            if(qrcodeCS.size()>0){


                //支付宝
                if(flag == 1){


                    qrcodeCS.get(0).setZfbUrl(authCode);


                }else if(flag == 2){ //微信

                    qrcodeCS.get(0).setWxUrl(authCode);
                }

                CDBHelper.createAndUpdate(getApplicationContext(),qrcodeCS.get(0));


            }else if(qrcodeCS.isEmpty()){//添加二维码

                qrcodeC qrcodeCS1 = new qrcodeC();
                qrcodeCS1.setChannelId(myapp.getCompany_ID());
                qrcodeCS1.setClassName("qrcodeC");

                //支付宝
                if(flag == 1){

                    qrcodeCS1.setZfbUrl(authCode);


                }else if(flag == 2){ //微信

                    qrcodeCS1.setWxUrl(authCode);

                }
                CDBHelper.createAndUpdate(getApplicationContext(),qrcodeCS1);

            }

        }else {

            Toast.makeText(DeskActivity.this,"扫描失败请重试！",Toast.LENGTH_LONG).show();
        }

    }
}
