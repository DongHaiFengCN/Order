package view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zm.order.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import Untils.BluetoothUtil;

public class PayActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private  BluetoothDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         btAdapter = BluetoothUtil.getBTAdapter();

         if (btAdapter == null) {
            Toast.makeText(getBaseContext(),"请打开蓝牙设备!", Toast.LENGTH_LONG)
                    .show();
            return;
         }
         device = BluetoothUtil.getDevice(btAdapter);
         if (device == null) {
            Toast.makeText(getBaseContext(),"请确保有InnterPrinter 蓝牙打印设备!",
                    Toast.LENGTH_LONG).show();
            return;
         }

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


        try {

            BluetoothUtil.printText("??? 青青杨柳陌，陌上别离人。",BluetoothUtil.getSocket(device));

        } catch (IOException e) {
            e.printStackTrace();
        }

  /*      byte[] data = null;
        try {
            data = name.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

    /*    BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
     /*   try {
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

/*        Intent intent = new Intent(PayActivity.this,MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();*/

    }

}

