package com.zm.order.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zm.order.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import untils.MyLog;


public class DiscountActivity extends AppCompatActivity {

    @BindView(R.id.unit_ten)
    RadioButton unitTen;
    @BindView(R.id.unit_element)
    RadioButton unitElement;
    @BindView(R.id.unit_horn)
    RadioButton unitHorn;
    @BindView(R.id.unit)
    RadioGroup unit;
    @BindView(R.id.submit_area)
    Button submitArea;

    private float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        total = getIntent().getFloatExtra("Total",0);
        MyLog.e(total+"");
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

    @OnClick({R.id.unit_ten, R.id.unit_element, R.id.unit_horn, R.id.unit, R.id.submit_area})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unit_ten:
                Toast.makeText(DiscountActivity.this,"unit_ten",Toast.LENGTH_SHORT).show();
                break;
            case R.id.unit_element:
                Toast.makeText(DiscountActivity.this,"unit_element",Toast.LENGTH_SHORT).show();
                break;
            case R.id.unit_horn:
                Toast.makeText(DiscountActivity.this,"unit_horn",Toast.LENGTH_SHORT).show();
                break;
            case R.id.submit_area:

                Intent intent = new Intent();
                intent.putExtra("Total",total-1);
                setResult(RESULT_OK,intent);
                finish();

                break;
        }
    }
}
