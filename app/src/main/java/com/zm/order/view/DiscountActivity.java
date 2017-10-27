package com.zm.order.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
    @BindView(R.id.total_tv)
    TextView totalTv;
    @BindView(R.id.discount_et)
    EditText discountEt;
    private float stashTotal;
    private CharSequence c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stashTotal = getIntent().getFloatExtra("Total", 0);

        totalTv.setText(stashTotal+"");

        discountEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {

                    if(unitTen.isChecked()){

                        unitTen.setChecked(false);
                    }
                    if(unitElement.isChecked()){

                        unitElement.setChecked(false);
                    }
                    if(unitHorn.isChecked()){

                        unitHorn.setChecked(false);
                    }
                    if(!discountEt.isCursorVisible()){

                        discountEt.setCursorVisible(true);
                    }
                    if(getTextTotal() != stashTotal){

                        totalTv.setText(stashTotal+"");
                    }

                }

                return false;
            }
        });



        discountEt.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                c = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if(!TextUtils.isEmpty(discountEt.getText().toString())){

                    if(getTextTotal() >= Float.valueOf(discountEt.getText().toString())){

                        totalTv.setText((stashTotal-Float.valueOf(discountEt.getText().toString()))+"");

                    }else {

                        //设置输入的长度
                        discountEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(c.length())});
                        Toast.makeText(DiscountActivity.this,"输入的数字超出当前总价！",Toast.LENGTH_SHORT).show();
                    }

                }else {


                    if(discountEt.isCursorVisible()){

                        totalTv.setText(stashTotal+"");
                    }
                }
            }
        });

    }

    public float getTextTotal(){

        return (TextUtils.isEmpty(totalTv.getText().toString()))?0:Float.valueOf(totalTv.getText().toString());
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

    @OnClick({R.id.unit_ten, R.id.unit_element, R.id.unit_horn,R.id.submit_area})
    public void onClick(View view) {

        if(discountEt.isCursorVisible()){

            discountEt.setCursorVisible(false);

            if(!TextUtils.isEmpty(discountEt.getText().toString())){
                discountEt.setText("");
            }

               /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(DiscountActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        }

        switch (view.getId()) {
            case R.id.unit_ten:

                compareTotal(100f);

                break;
            case R.id.unit_element:

                compareTotal(10f);

                break;
            case R.id.unit_horn:

                compareTotal(1f);

                break;
            case R.id.submit_area:

                Intent intent = new Intent();
                intent.putExtra("Total", getTextTotal());
                MyLog.e(getTextTotal()+"");
                setResult(RESULT_OK, intent);
                finish();

                break;
        }
    }

    /**
     * 判断是否满足折扣条件
     * @param t
     */
  public void compareTotal(float t){


      if(stashTotal > t){


          int stash = (int) (stashTotal/t);

          totalTv.setText(String.valueOf(stash*t));

      }else {

          Toast.makeText(DiscountActivity.this,"不满足条件！",Toast.LENGTH_SHORT).show();

      }
  }
}
