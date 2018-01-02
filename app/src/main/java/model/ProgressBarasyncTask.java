package model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.Expression;
import com.zm.order.view.MyBigDecimal;
import com.zm.order.view.PayActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.order.CheckOrderC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.PayDetailC;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.user.CompanyC;
import untils.BluetoothUtil;
import untils.MyLog;
import untils.PrintUtils;

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
    private CheckOrderC checkOrderC;
    private String str; //临时变量
    private float total;
    private List<GoodsC> goodsCList = new ArrayList<>();

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
        total = checkOrderC.getPay();
        str = String.valueOf(total);
        MyLog.e(str);

        onPrint();

        return flag;
    }

    private void onPrint() {

        if(true){ //支付成功

            String waiter = "默认";
            MyApplication m = (MyApplication) payActivity.getApplicationContext();

            if(m.getUsersC().getEmployeeName() != null && !m.getUsersC().getEmployeeName().isEmpty()){

                waiter =m.getUsersC().getEmployeeName();
            }


            String tableNumber = checkOrderC.getTableNo();


            setAll();
            //List<OrderC> list = checkOrderC.getOrderList();
            List<CompanyC> companyCs = CDBHelper.getObjByClass(payActivity.getApplicationContext(),CompanyC.class);
            AreaC areaCs = CDBHelper.getObjById(payActivity.getApplicationContext(),m.getTable_sel_obj().getAreaId(),AreaC.class);
            PrintUtils.selectCommand(PrintUtils.RESET);
            PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
            if (companyCs.size() != 0){
                PrintUtils.printText(companyCs.get(0).getPointName()+"\n\n");
            }
            PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
            PrintUtils.printText(areaCs.getAreaName()+"/"+m.getTable_sel_obj().getTableName()+"桌\n\n");
            PrintUtils.selectCommand(PrintUtils.NORMAL);
            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
            PrintUtils.printText(PrintUtils.printTwoData("订单编号", OrderId()+"\n"));
            PrintUtils.printText(PrintUtils.printTwoData("下单时间", checkOrderC.getCheckTime()+"\n"));
            PrintUtils.printText(PrintUtils.printTwoData("人数："+m.getTable_sel_obj().getCurrentPersions(), "收银员："+waiter+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD);
            PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

            //for (int i = 0; i < list.size(); i++) {


                for (int j = 0; j < goodsCList.size(); j++) {

                    GoodsC goodsC = goodsCList.get(j);

                    PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName(),goodsC.getDishesCount()+"", MyBigDecimal.mul(goodsC.getPrice(),goodsC.getDishesCount(),2)+"\n"));


                }

            //}
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("合计", total+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("实收", checkOrderC.getNeedPay()+"\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);

            List<PayDetailC> payDetailCList = checkOrderC.getPromotionDetail().getPayDetailList();
            StringBuffer stringBuffer = new StringBuffer("");

            if(payDetailCList != null && !payDetailCList.isEmpty()){


                for (int i = 0; i < payDetailCList.size(); i++) {

                    PayDetailC p = payDetailCList.get(i);

                    switch (p.getPayTypes()){

                        case 1:
                            stringBuffer.append("现金 ");
                            break;
                        case 2:
                            stringBuffer.append("银行卡 ");
                            break;
                        case 3:
                            stringBuffer.append("微信 ");
                            break;
                        case 4:
                            stringBuffer.append("支付宝 ");
                            break;
                        case 5:
                            stringBuffer.append("美团 ");
                            break;
                        case 6:
                            stringBuffer.append("会员卡 ");
                            break;
                        case 7:
                            stringBuffer.append("抹零 ");
                            break;
                        case 8:
                            stringBuffer.append("赠卷 ");
                            break;

                        default:
                            break;

                    }

                }

            }
            PrintUtils.printText("支付方式："+stringBuffer.toString());
            PrintUtils.printText("\n\n\n\n\n");
            PrintUtils.printText("\n\n\n\n");
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
        Log.e("Test",result);

        payActivity.closeDialog();
        payActivity.turnDesk();




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
     * @param checkOrderC 需要打印的参数
     */
    public void setDate(CheckOrderC checkOrderC){

        this.checkOrderC = checkOrderC;
    }

    private void setAll() {
        boolean flag = false;
        for (OrderC orderC : checkOrderC.getOrderList()) {
            for (GoodsC goodsb : orderC.getGoodsList()) {
                flag = false;

                for (GoodsC goodsC : goodsCList) {
                    if (goodsC.getDishesName().equals(goodsb.getDishesName())) {

                        if (goodsb.getDishesTaste() != null) {

                            if (goodsb.getDishesTaste().equals(goodsC.getDishesTaste())) {

                                float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                                goodsC.setDishesCount(count);
                                flag = true;
                            }

                        } else {

                            float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                            goodsC.setDishesCount(count);

                            flag = true;
                        }

                        break;
                    }
                }
                if (!flag) {
                    GoodsC objClone = null;
                    try {
                        objClone = (GoodsC) goodsb.clone();
                    } catch (CloneNotSupportedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    goodsCList.add(objClone);

                }

            }

        }
    }

}
