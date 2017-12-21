package com.zm.order.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Log;
import com.couchbase.lite.Ordering;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.zm.order.R;

import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;

import application.MyApplication;
import bean.kitchenmanage.kitchen.KitchenClientC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.OrderNum;
import bean.kitchenmanage.table.AreaC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;
import untils.AnimationUtil;
import untils.BluetoothUtil;
import untils.MyLog;
import untils.PrintUtils;

import static com.gprinter.service.GpPrintService.ACTION_CONNECT_STATUS;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_frame)
    FrameLayout activityFrame;
    private MyApplication myApp;
    private ListView order_lv;
    private TextView ok_tv;
    private TextView total_tv;
    private ImageView car_iv;
    private boolean flag = true;
    private ImageButton delet_bt;
    public  List<SparseArray<Object>> orderItem = new ArrayList<>();
    public  OrderAdapter o;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private int point = 0;
    private TextView point_tv;
    private float total = 0.0f;
    private Fragment seekT9Fragment;
    private Fragment orderFragment;
    private SeekT9Adapter seekT9Adapter;
    private FragmentManager fm;//获得Fragment管理器
    private FragmentTransaction ft; //开启一个事务
    private boolean isFlag = true;
    private OrderC orderC ;
    private List<GoodsC> goodsList = new ArrayList<>();
    private String gOrderId;
    private Document document;
    private Handler mHandler;
   //打印机连接
    private PrinterServiceConnection conn = null ;
    private GpService mGpService = null;
    private Map<String,ArrayList<GoodsC>> allKitchenClientGoods=new HashMap<String,ArrayList<GoodsC>>();
    private Map<String, String> allKitchenClientPrintNames=new HashMap<String, String>();
    private static String pIp = "192.168.1.249";
    private static int pPortNum = 9100;
    private String tableName,areaName,currentPersions,serNum;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private boolean  printerSat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myApp = (MyApplication) getApplication();
        orderC = new OrderC(myApp.getCompany_ID());
        String oId = CDBHelper.createAndUpdate(getApplicationContext(),orderC);
        orderC.set_id(oId);
        mHandler = new Handler();
        initView();
        //连接打印机服务
        registerPrinterBroadcast();
        connectPrinter();
        select(isFlag);
        MyLog.d("onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.d("onDestroy");
        unregisterReceiver(PrinterStatusBroadcastReceiver);
        // 2、注销打印消息
        if (conn != null) {
            unbindService(conn); // unBindService
        }


    }

    private void registerPrinterBroadcast()
    {
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_CONNECT_STATUS));
        // 注册实时状态查询广播
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
    }
    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            //  MyLog("NavigationMain--PrinterStatusBroadcastReceiver= " + action);
            if (action.equals(ACTION_CONNECT_STATUS))//连接状态
            {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);

                if (type == GpDevice.STATE_CONNECTING)//2
                {
                   MyLog.d("打印机正在连接");
                }
                else if (type == GpDevice.STATE_NONE)
                {
                    MyLog.d("打印机未连接");
                }
                else if (type == GpDevice.STATE_VALID_PRINTER)//连接成功 5
                {
                    MyLog.d("打印机连接成功");

                    //1、程序连接上厨房端打印机后要进行分厨房打印
                    if(goodsList==null||goodsList.size()<=0)
                        return ;


                    printGoodsAtRomoteByIndex(id);


                }
                else if (type == GpDevice.STATE_INVALID_PRINTER)
                {
                    MyLog.d("打印机已连接");

                }
            }
            else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE))//本地打印完成回调
            {


            }
          else  if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS))
            {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS)
                {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR)
                    {
                        str = "打印机正常";
                        printerSat = true;
                    }
                    else
                    {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0)
                        {
                            str += "查询超时";
                        }
                        printerSat = false;

                        Toast.makeText(getApplicationContext(), "厨房打印机："  + " 状态：" + str, Toast.LENGTH_SHORT)
                                .show();
                    }


                }
            }
        }
    };


    public void setOrderItem(SparseArray<Object> sparseArray){
        orderItem.add(sparseArray);
    }

    public void setT9Adapter(SeekT9Adapter seekT9Adapter){
        this.seekT9Adapter = seekT9Adapter;
    }

    public SeekT9Adapter getSeekT9Adapter(){
        return seekT9Adapter;
    }

    public void setOrderAdapter(OrderAdapter o) {
        this.o = o;

    }
    public OrderAdapter getOrderAdapter(){
        return o;
    }

    public List<SparseArray<Object>> getOrderItem(){
        return orderItem;
    }
    public List<GoodsC> getGoodsList(){
        return goodsList;
    }
    public void setTotal(float total){
        this.total = total;
        String to = MyBigDecimal.round(total+"",2);
        total_tv.setText(to + "元");
    }

    public float getTotal(){
        return total;
    }
    public void setPoint(int point){
        this.point = point;
        if (point > 0){
            point_tv.setText(point + "");
            point_tv.setVisibility(View.VISIBLE);
        }else{
            point_tv.setVisibility(View.GONE);
        }

    }
    public int getPoint(){
        return point;
    }
    public void initView() {

        total_tv = (TextView) findViewById(R.id.total_tv);

        point_tv = (TextView) findViewById(R.id.point);

        car_iv = (ImageView) findViewById(R.id.car);

        ok_tv = (TextView) findViewById(R.id.ok_tv);
        order_lv = (ListView) findViewById(R.id.order_lv);
        final ImageView imageView = (ImageView) findViewById(R.id.shade);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.orderList);
        //获取屏幕尺寸

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        //设置表单的容器的长度为视窗的一半高，由父类的节点获得

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout
                .getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h / 2;
        linearLayout.setLayoutParams(layoutParams);
        o = new OrderAdapter( getGoodsList(), MainActivity.this);
        order_lv.setAdapter(o);
        o.setListener(new OrderAdapter.setOnItemListener() {
            @Override
            public void setListener(final int position) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("是否赠菜")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //1\
                                GoodsC goodsC = goodsList.get(position);
                                //2\
                                //OrderC orderC = CDBHelper.getObjById(getApplicationContext(),goodsC.getOrder(),OrderC.class);
                                goodsC.setGoodsType(2);
                                goodsC.setDishesName(goodsC.getDishesName()+"(赠)");
                                total -= goodsC.getAllPrice();
                                setTotal(total);
                                goodsC.setAllPrice(0);
                                o.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
            }
        });
        car_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //初始化订单的数据，绑定数据源的信息。
                //o.notifyDataSetChanged();

                mHandler.post(new TimerTask() {
                    @Override
                    public void run() {
                        Iterator<GoodsC> iterator = getGoodsList().iterator();

                        while (iterator.hasNext()){
                            GoodsC goodsC = iterator.next();
                            if (goodsC.getDishesCount() == 0){
                                iterator.remove();
                                break;
                            }


                        }
                        for (int i = 0; 0 < getGoodsList().size();i++){
                            if (getGoodsList().get(i).getDishesCount() == 0 ){
                                getGoodsList().remove(i);
                            }
                            break;
                        }

                    }
                });

                if (flag) {

                    linearLayout.setAnimation(AnimationUtil.moveToViewLocation());
                    linearLayout.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setListener(null);
                    flag = false;

                } else {

                    linearLayout.setAnimation(AnimationUtil.moveToViewBottom());
                    linearLayout.setVisibility(View.GONE);

                    imageView.animate()
                            .alpha(0f)
                            .setDuration(400)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    imageView.setVisibility(View.GONE);
                                }
                            });

                    flag = true;

                }

                //监听orderItem的增加删除，设置总价以及总数量, flag ？+ ：-,price 单价 ,sum 当前item的个数。

                o.setOnchangeListener(new OrderAdapter.OnchangeListener() {
                    @Override
                    public void onchangeListener(boolean flag, float price, float sum) {

                        if (flag) {

                            total += price;
                            String to = MyBigDecimal.round(total+"",2);
                            total_tv.setText(to + "元");


                        } else {

                            total -= price;
                            String to = MyBigDecimal.round(total+"",2);
                            total_tv.setText(to + "元");

                            if (sum == 0) {

                                point--;

                                point_tv.setText(point + "");

                                if (point == 0) {

                                    point_tv.setVisibility(View.INVISIBLE);
                                }


                            }


                        }
                    }
                });

            }
        });




        //清空按钮
        delet_bt = (ImageButton) findViewById(R.id.delet);

        delet_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearOrder();
            }
        });


        //提交按钮
        ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                saveOrder();
                if (total == 0 || getGoodsList().size() > 0) {
                    //如果order列表开启状态就关闭
                    if (!flag) {
                        linearLayout.setAnimation(AnimationUtil.moveToViewBottom());
                        linearLayout.setVisibility(View.GONE);
                        imageView.animate()
                                .alpha(0f)
                                .setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        imageView.setVisibility(View.GONE);
                                    }
                                });

                        flag = true;
                    }


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view1 = getLayoutInflater().inflate(R.layout.view_pay_dialog,null);
                    builder.setView(view1);
                    builder.setCancelable(true);
                    final AlertDialog dialog = builder.create();
                    Button shi = view1.findViewById(R.id.view_pay_shi);
                    Button fou = view1.findViewById(R.id.view_pay_fou);
                    shi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(MainActivity.this, PayActivity.class);
                            startActivityForResult(intent,1);
                            finish();
                            dialog.dismiss();
                        }
                    });
                    fou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(MainActivity.this, DeskActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                            finish();
                        }
                    });

                    Button dy = view1.findViewById(R.id.view_pay_dy);
                    dy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (setPrintOrder().equals("")){
                                Toast.makeText(MainActivity.this,"没有链接蓝牙打印机",Toast.LENGTH_LONG).show();
                            }else {
                                Intent intent = new Intent(MainActivity.this, DeskActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                                finish();
                            }


                        }
                    });

                    dialog.show();

                } else {

                    Toast.makeText(MainActivity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                }


            }


        });
    }

    /**
     * 厨房分单打印
     */
    private void printOrderToKitchen()
    {


        //1\ 查询出所有厨房,并分配菜品
        List<KitchenClientC> kitchenClientList= CDBHelper.getObjByClass(getApplicationContext(), KitchenClientC.class);
        if(kitchenClientList.size()<=0)
        {
            Toast.makeText(getApplicationContext(),"未配置厨房数据",Toast.LENGTH_SHORT).show();
            return;
        }

        allKitchenClientGoods.clear();
        allKitchenClientPrintNames.clear();

        for(KitchenClientC kitchenClientObj:kitchenClientList)//1 for 遍历所有厨房
        {
            boolean findflag = false;
            ArrayList<GoodsC> oneKitchenClientGoods = new ArrayList<GoodsC>();

            for(String dishKindId:kitchenClientObj.getDishesKindIDList())//2 for 遍历厨房下所含菜系
            {

                Document dishKindDoc= CDBHelper.getDocByID(getApplicationContext(),dishKindId);
                String dishesKindName=dishKindDoc.getString("kindName");

                for(GoodsC goodsC:goodsList)//3 for 该厨房下所应得商品
                {
                    if(dishesKindName.equals(goodsC.getDishesKindName()))
                    {
                        findflag = true;
                        // g_printGoodsList.remove(goodsC);//为了降低循环次数，因为菜品只可能在一个厨房打印分发，故分发完后移除掉。
                        oneKitchenClientGoods.add(goodsC);
                    }

                }//end for 3
            }//end for 2


            if(findflag)  //如果有所属菜品，就去打印
            {
                String clientKtname=""+kitchenClientObj.getName();//厨房名称
                String printname=""+kitchenClientObj.getKitchenAdress();//打印机名称

                //String printId=printname.substring(printname.length()-1,printname.length());

                int printerId=0;//Integer.parseInt(printId)-1;

                allKitchenClientGoods.put(""+printerId,oneKitchenClientGoods);
                allKitchenClientPrintNames.put(""+printerId,clientKtname);
                if (!isPrinterConnected(printerId)) // 未连接
                {
                    if(connectClientPrint(printerId)==0)
                    {
                        MyLog.d( "***********打印机连接命令发送成功");
                    }
                    else
                    {
                        MyLog.d( "***********打印机连接命令发送失败");
                    }
                }
                else//已连接
                {
                    MyLog.d( "*******厨房打印机已连接，正在分发打印");
                    printGoodsAtRomoteByIndex(printerId);
                }


            }

        }//end for1


        //2\判断厨房打印机状态是否连接

        //3\如果是连接状态  直接判断打印
        //4\如果未连接  ，连接打印机  并在打印机连接成功信息接收后打印
    }
    private void printGoodsAtRomoteByIndex(int printerId)
    {
        //1、程序连接上厨房端打印机后要进行分厨房打印
        ArrayList<GoodsC> myshangpinlist= allKitchenClientGoods.get(""+printerId);

        //2、获得该打印机内容 打印机名称
        String printname= allKitchenClientPrintNames.get(""+printerId);
        String printcontent=getPrintContentforClient(myshangpinlist,printname);
        if( printContent(printcontent,printerId)==0)//打印成功，没有打印完成回调
        {
            MyLog.d(printname+"分单打印完成");
        }
        else
        {
            MyLog.d("厨房打印失败");
        }

        setOrderPrintState(gOrderId);

    }

    private void setOrderPrintState(String orderId)
    {

        OrderC obj = CDBHelper.getObjById(getApplicationContext(),orderId,OrderC.class);
        obj.setPrintFlag(1);
        CDBHelper.createAndUpdate(getApplicationContext(),obj);
    }

    private int printContent(String content, int printIndex)//0发送数据到打印机 成功 其它错误
    {
        int rel = 0;
        try {
            rel = mGpService.sendEscCommand(printIndex, content);
        } catch (RemoteException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -2;
        }
        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
        if (r != GpCom.ERROR_CODE.SUCCESS)
        {
            //Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            return -2;
        }
        else
            return 0;//把数据发送打印机成功
    }

    private String getPrintContentforClient(ArrayList<GoodsC> myshangpinlist, String clientname)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");// 设置日期格式
        String endtime = sdf.format(new Date());
        EscCommand esc = new EscCommand();
        try {
            // 打印标题
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 设置打印居中
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF); // 设置为倍高倍宽
            esc.addText(clientname + "\n");// 打印文字
            esc.addPrintAndLineFeed();
            // 打印文字
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
           // esc.addSetLeftMargin((short)10);
            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发
            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字
            esc.addText("人数:" + currentPersions + "\n");//流水号生成机制开发
            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间
            esc.addText("------------------------------------------\n");
            esc.addText("菜品名称         单价     数量    金额 \n"); // 菜品名称(14) 单价(6) 数量(5) 金额(7)
            esc.addText("\n");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < myshangpinlist.size(); i++)
        {
            float num = 1; // 数量 默认为1
            num = myshangpinlist.get(i).getDishesCount();
            esc.addText(myshangpinlist.get(i).getDishesName().toString());
            String temp = myshangpinlist.get(i).getDishesTaste();
            if (temp == null || "".equals(temp))
            {
                try
                {
                    for (int j = 0; j < (18 - myshangpinlist.get(i).getDishesName().toString().getBytes("gbk").length); j++)
                        esc.addText(" ");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else
            {
                esc.addText("(" + temp + ")");
                try {
                    for (int j = 0; j < (18 - myshangpinlist.get(i).getDishesName().toString().getBytes("gbk").length
                            - temp.getBytes("gbk").length - 2); j++)
                        esc.addText(" ");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // 查找菜品的单价

            String  strprice= ""+ MyBigDecimal.div(myshangpinlist.get(i).getAllPrice(),myshangpinlist.get(i).getDishesCount(),2);//myshangpinlist.get(i).getSinglePrice;
            esc.addText(strprice);
            for (int j = 0; j < 9 - strprice.length(); j++)
                esc.addText(" ");

            esc.addText("" + num);
            for (int j = 0; j < 7 - ("" + num).length(); j++)
                esc.addText(" ");

            esc.addText("" + (myshangpinlist.get(i).getAllPrice()) + "\n");

        }
        esc.addText("--------------------------------------------\n");
        esc.addPrintAndLineFeed();

        byte len = 0x01;
        esc.addCutAndFeedPaper(len);

/*
        for (int i = 0; i < myshangpinlist.size(); i++)
        {
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF); // 设置为倍高倍宽

            esc.addText("--------------------\n");
            esc.addText("房间:" + areaName + "\n");// 打印文字
            esc.addText("桌位："+ tableName +"\n");
            esc.addText("--------------------\n");
            esc.addPrintAndLineFeed();

            float num = 1; // 数量 默认为1
            num = myshangpinlist.get(i).getDishesCount();
            esc.addText(myshangpinlist.get(i).getDishesName().toString());
            String temp = myshangpinlist.get(i).getDishesTaste();
            if (temp == null || "".equals(temp))
            {
                esc.addPrintAndLineFeed();
            }
            else
            {
                esc.addText("(" + temp + ")");
                esc.addPrintAndLineFeed();
            }


            esc.addText("数量： " + num);
            esc.addPrintAndLineFeed();
            byte len1 = 0x01;
            esc.addPrintAndFeedLines(len1);
            esc.addCutAndFeedPaper(len1);


        }
*/

        Vector<Byte> datas = esc.getCommand();
        // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        return str;

    }

    Boolean CheckPortParamters(PortParameters param) {
        boolean rel = false;
        int type = param.getPortType();
        if (type == PortParameters.BLUETOOTH) {
            if (!param.getBluetoothAddr().equals("")) {
                rel = true;
            }
        } else if (type == PortParameters.ETHERNET) {
            if ((!param.getIpAddr().equals("")) && (param.getPortNumber() != 0)) {
                rel = true;
            }
        } else if (type == PortParameters.USB) {
            if (!param.getUsbDeviceName().equals("")) {
                rel = true;
            }
        }
        return rel;
    }
    private  int connectClientPrint(int index)
    {
        if (mGpService != null)
        {
            try {
              //  PortParamDataBase database = new PortParamDataBase(this);
                PortParameters mPortParam = new PortParameters();
                mPortParam.setPortType(PortParameters.ETHERNET);
                mPortParam.setIpAddr(pIp);
                mPortParam.setPortNumber(pPortNum);
                int rel = -1;

              if (CheckPortParamters(mPortParam))
                {
                    try {
                        mGpService.closePort(index);
                    } catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                   switch (mPortParam.getPortType())

                    {
                        case PortParameters.USB:
                            rel = mGpService.openPort(index, mPortParam.getPortType(),
                                    mPortParam.getUsbDeviceName(), 0);
                            break;
                        case PortParameters.ETHERNET:

                            try {
                                rel = mGpService.openPort(index, mPortParam.getPortType(),
                                        mPortParam.getIpAddr(), mPortParam.getPortNumber());
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;
                        case PortParameters.BLUETOOTH:
                            try {
                                rel = mGpService.openPort(index, mPortParam.getPortType(),
                                        mPortParam.getBluetoothAddr(), 0);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            break;
                    }
                }

                //database.close();
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                if (r != GpCom.ERROR_CODE.SUCCESS)
                {
                    if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN)
                    {
                        return 0;
                    } else {
                        return -1;
                    }
                }
                else
                    return 0;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
        }
        else
            return -1;
    }


    /**
     *打印机连接状态判断
     * @param index
     * @return
     */
    private Boolean isPrinterConnected( int index)
    {
        if(!printerSat)
            return false;
        // 一上来就先连接蓝牙设备
        int status = 0;
        if(mGpService==null)
            return false;
        try
        {
            status =mGpService.getPrinterConnectStatus(index);
            MyLog.d(  "printer statue="+status);
        }
        catch (RemoteException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return status == GpDevice.STATE_CONNECTED;
    }

    /**
     *
     */
    private void connectPrinter()
    {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        intent.setPackage(getPackageName());
        boolean ret = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        MyLog.e("connectPrinter ret="+ret);
    }
    class PrinterServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLog.e("PrinterServiceConnection onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mGpService = GpService.Stub.asInterface(service);
            //myapp.setmGpService(mGpService);
            MyLog.e("PrinterServiceConnection onServiceConnected() called");

            try {

                mGpService.queryPrinterStatus(0, 500, MAIN_QUERY_PRINTER_STATUS);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


        }
    }
    /**
     * 清空订单列表
     */

    private void clearOrder() {

        point = 0;
        point_tv.setVisibility(View.INVISIBLE);

        total_tv.setText("0元");
        total = 0;

        getGoodsList().clear();
        o.notifyDataSetChanged();
        seekT9Adapter.notifyDataSetChanged();
    }

    private   String getOrderSerialNum()
    {
        String orderNum=null;
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");

        List<OrderNum> orderNumList = CDBHelper.getObjByWhere(getApplicationContext(),Expression.property("className").equalTo("OrderNum")
                ,null
                ,OrderNum.class);
        if(orderNumList.size()<=0)//第一次使用
        {
            OrderNum obj = new OrderNum(myApp.getCompany_ID());
            String time=formatter.format(new Date());
            obj.setDate(time);
            obj.setNum(1);
            CDBHelper.createAndUpdate(getApplicationContext(),obj);
            orderNum =  "001";
        }
        else//有数据，判断是不是当天
        {
            OrderNum obj = orderNumList.get(0);
            String olderDate = obj.getDate();
            String newDate =  formatter.format(new Date());
            int num = obj.getNum();
            if(!newDate.equals(olderDate))//不是一天的，
            {
                obj.setNum(1);
                obj.setDate(newDate);
                CDBHelper.createAndUpdate(getApplicationContext(),obj);
                orderNum =  "001";
            }
            else//同一天
            {
                int newNum = num+1;
                obj.setNum(newNum);
                CDBHelper.createAndUpdate(getApplicationContext(),obj);
                orderNum = String.format("%3d", newNum).replace(" ", "0");
            }
        }

        return orderNum;

    }

    private String setPrintOrder(){

        btAdapter = BluetoothUtil.getBTAdapter();
        if(btAdapter != null){

            device = BluetoothUtil.getDevice(btAdapter);
            if (device != null){
                try {
                    socket = BluetoothUtil.getSocket(device);
                    PrintUtils.setOutputStream(socket.getOutputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                onPrint();
                return "打印成功";
            }


        }
        return "";
    }


    private void onPrint() {


            String waiter = myApp.getUsersC().getEmployeeName();

            String tableNumber = orderC.getTableNo();
            PrintUtils.selectCommand(PrintUtils.RESET);
            PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
            PrintUtils.printText("肴点点\n\n");
            PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
            PrintUtils.printText(tableNumber+"号桌\n\n");
            PrintUtils.selectCommand(PrintUtils.NORMAL);
            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
            PrintUtils.printText(PrintUtils.printTwoData("订单编号", OrderId()+"\n"));
            PrintUtils.printText(PrintUtils.printTwoData("下单时间", getFormatDate()+"\n"));
            PrintUtils.printText(PrintUtils.printTwoData("人数："+myApp.getTable_sel_obj().getCurrentPersions(), "收银员："+waiter+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD);
            PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

            List<GoodsC> goodsCList = orderC.getGoodsList();

            for (int j = 0; j < goodsCList.size(); j++) {

                GoodsC goodsC = goodsCList.get(j);

                PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName(),goodsC.getDishesCount()+"", goodsC.getAllPrice()+"\n"));


            }

            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("合计", total+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText("\n\n\n\n");
            PrintUtils.closeOutputStream();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    /**
     * @return 订单号
     */
    public String OrderId(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }

    /**
     *
     * @return 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public String getFormatDate(){
        Date date = new Date();
        if(date != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }

        return null;
    }


    private void saveOrder()
    {
        try {
            CDBHelper.db.inBatch(new TimerTask() {
                @Override
                public void run()
                {

                    List<OrderC> orderCList=CDBHelper.getObjByWhere(getApplicationContext(),
                            Expression.property("className").equalTo("OrderC")
                                    .and(Expression.property("orderState").equalTo(1))
                                    .and(Expression.property("tableNo").equalTo(myApp.getTable_sel_obj().getTableNum()))
                            , Ordering.property("createdTime").descending()
                            ,OrderC.class);



                    if (document == null)
                    {

                        if(orderCList.size()>0)
                        {
                            orderC.setOrderNum(orderCList.get(0).getOrderNum()+1);
                            orderC.setSerialNum(orderCList.get(0).getSerialNum());
                        }
                        else
                        {
                            orderC.setOrderNum(1);
                            orderC.setSerialNum(getOrderSerialNum());
                        }


                        BuglyLog.e("saveOrder", "goodsListSize="+goodsList.size());

                        for(GoodsC obj:goodsList)
                        {
                            obj.setOrder(orderC.get_id());
                            CDBHelper.createAndUpdate(getApplicationContext(),obj);
                        }
                        orderC.setGoodsList(goodsList);
                        orderC.setAllPrice(total);
                        orderC.setOrderState(1);
                        orderC.setOrderType(1);
                        orderC.setCreatedTime(getFormatDate());
                        orderC.setTableNo(myApp.getTable_sel_obj().getTableNum());
                        orderC.setTableName(myApp.getTable_sel_obj().getTableName());
                        AreaC areaC = CDBHelper.getObjById(getApplicationContext(),myApp.getTable_sel_obj().getAreaId(), AreaC.class);
                        orderC.setAreaName(areaC.getAreaName());
                        gOrderId = CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                        Log.e("id",gOrderId);

                        areaName = orderC.getAreaName();
                        tableName = orderC.getTableName();
                        currentPersions = ""+myApp.getTable_sel_obj().getCurrentPersions();
                        if(orderC.getOrderNum()==1)//第一次下单
                            serNum = orderC.getSerialNum();//流水号
                        else //多次下单
                            serNum = orderC.getSerialNum()+"_"+orderC.getOrderNum();


                    }
                }
            });
        } catch (CouchbaseLiteException e)
        {

            e.printStackTrace();
            CrashReport.postCatchedException(e);

        }

        printOrderToKitchen();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == 1)
        {

            //document = CDBHelper.getDocByID(getApplicationContext(),gOrderId);
            Log.e("document",""+document.getId());

        }



    }




    //隐藏所有Fragment
    private void hidtFragment(FragmentTransaction fragmentTransaction){


        if (seekT9Fragment != null){
            fragmentTransaction.hide(seekT9Fragment);
        }
        if (orderFragment != null){
            fragmentTransaction.hide(orderFragment);
        }
    }

    private void select(boolean isTrue) {
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        hidtFragment(ft);
        if(isTrue == true){
            if (seekT9Fragment == null){
                seekT9Fragment = new SeekT9Fragment();
                ft.add(R.id.activity_frame,seekT9Fragment);

            }else{
                ft.show(seekT9Fragment);;
            }
            isFlag = false;
        }else if (isTrue == false){
            if (orderFragment == null){
                orderFragment = new OrderFragment();
                ft.add(R.id.activity_frame,orderFragment);
            }else{
                ft.show(orderFragment);
            }
            isFlag = true;
        }
        ft.commit();

    }
    /**
     * 模拟原始数据
     *
     * @return
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            select(isFlag);

        } /*else if (id == R.id.action_cancel) {

            myApp.cancleSharePreferences();
            Intent itent = new Intent();
            itent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(itent);
            finish();

        }*/

        return super.onOptionsItemSelected(item);
    }



}
