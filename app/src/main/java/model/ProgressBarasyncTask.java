package model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import untils.BluetoothUtil;
import untils.MyLog;
import untils.OkHttpController;
import untils.PrintUtils;
import com.zm.order.view.PayActivity;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/28 10:13
 * 修改人：donghaifeng
 * 修改时间：2017/9/28 10:13
 * 修改备注：
 */

public class ProgressBarasyncTask extends AsyncTask<Integer, Integer, String> {
    private  Date date;

    private String flag = "";

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private PayActivity payActivity;
    private Intent intent;
    private String ml =".0";
    private String str; //临时变量
    private float total;

    public ProgressBarasyncTask(PayActivity payActivity) {

        this.payActivity = payActivity;

    }
    //首先执行的是onPreExecute方法

    //该方法运行在Ui线程内，可以对UI线程内的控件设置和修改其属性

    @Override
    protected void onPreExecute() {

         this.payActivity.showDialog();
    }

    //其次是执行doInBackground方法
    @Override
    protected String doInBackground(Integer... integers) {

        btAdapter = BluetoothUtil.getBTAdapter();

       if (btAdapter == null) {

            return "本机没有找到蓝牙硬件或驱动!";
        }

        device = BluetoothUtil.getDevice(btAdapter);

       if (device == null) {

            return "请确保InnterPrinter 蓝牙打印设备打开!";
        }

        try {
            socket = BluetoothUtil.getSocket(device);
            PrintUtils.setOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        total = intent.getFloatExtra("total",0);
        str = String.valueOf(total);
        MyLog.e(str);

        //抹零
       // setMl();

        onPrint();

        return flag;
    }

    private void onPrint() {

        if(true){ //支付成功

            flag = "ok";
            String waiter ="董海峰";
            String peopleSum = "8";
            String tableNumber = intent.getStringExtra("tableNumber");

            List list = (ArrayList<SparseArray<Object>>) intent.getSerializableExtra("Order");

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
            PrintUtils.printText(PrintUtils.printTwoData("人数："+peopleSum, "收银员："+waiter+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD);
            PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
            for (int i = 0; i < list.size(); i++) {

                SparseArray<Object> s = (SparseArray<Object>) list.get(i);
                PrintUtils.printText(PrintUtils.printThreeData(s.get(0).toString(), s.get(2).toString(), s.get(4).toString()+"\n"));
            }
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("合计", total+"\n"));
            PrintUtils.printText(PrintUtils.printTwoData("抹零", "0"+ml+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("实收", str+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
            PrintUtils.printText("备注：");
            PrintUtils.printText("\n\n\n\n\n");
            PrintUtils.closeOutputStream();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };


    }


    //在doInBackground方法当中，每次调用publishProgrogress()方法之后，都会触发该方法
    @Override
    protected void onProgressUpdate(Integer... values) {


    }
    //在doInBackground方法执行结束后再运行，并且运行在UI线程当中
    //主要用于将异步操作任务执行的结果展示给用户
    @Override
    protected void onPostExecute(String result) {

          if(!result.equals("ok")){

              Toast.makeText(payActivity,"支付失败！", Toast.LENGTH_LONG).show();
          }else {
              payActivity.turnMainActivity();
          }
        payActivity.closeDialog();


    }

    /**
     *
     * @return 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public String getFormatDate(){

        if(date != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }

        return null;
    }
    /**
     * @return 订单号
     */
    public String OrderId(){
        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }

    /**
     *
     * @param intent 需要打印的参数
     */
    public void setDate(Intent intent){

        this.intent = intent;
    }

    public void setMl(){

        String l[] = str.split("\\.");//抹零功能
        ml = l[1];

    }
}
