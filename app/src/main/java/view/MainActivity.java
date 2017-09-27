package view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import Untils.AnimationUtil;
import Untils.MyLog;
import application.MyApplication;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;

public class MainActivity extends AppCompatActivity implements IMainView{
    private ListView dishesKind_lv;

    private ListView order_lv;
    private RecyclerView dishes_rv;
    private List<String> dishesKindName;
    private DishesKindAdapter leftAdapter;
    private MyApplication myApp;

    private TextView ok_tv;
    private TextView total_tv;

    private DishesAdapter dishesAdapter;
    private List<Integer> headPosition;
    private List<Integer> footPosition;
    private ImageView  car_iv;
    private boolean flag = true ;
    private ImageButton delet_bt;
    private List<SparseArray<Object>> orderItem = new ArrayList<>();
    private  OrderAdapter o;
    private View view;
    private int point = 0;

    private  String taste = "默认";

    private TextView point_tv;

    private float total = 0.0f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *董海峰
         */
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        myApp = (MyApplication) getApplication();

        IMainPresenter iMainView = new MainPresenterImpl(this);

        iMainView.init();

    }

    /**
     * 菜品选择弹出框编辑模块
     *
     * @param name 传入的菜品的名称
     * @param price 传入的菜品的价格
     *
     */
    private void showDialog(final String name,final float price) {

        final float[] l = {0.0f};

        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_item_dialog,null);

        final TextView price_tv = view.findViewById(R.id.price);

        final AmountView amountView = view.findViewById(R.id.amount_view);

        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(int ls,boolean flag) {


                l[0] = ls*price;//实时计算当前菜品选择不同数量后的单品总价

                price_tv.setText("总计 "+ l[0] +" 元");

            }
        });

        RadioGroup group = view.findViewById(R.id.radioGroup);

        //选择口味
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

             if(i == R.id.Spicy ){

                 taste = "微辣";

                }else if(i == R.id.hot){

                 taste = "辣";

                }
            }
        });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog
                .Builder(this);
        builder.setTitle(name);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(amountView.getAmount() != 0){//如果选择器的数量不为零，当前的选择的菜品加入订单列表

                    SparseArray<Object> s = new SparseArray<>();
                    s.put(0,name);
                    s.put(1,taste);
                    s.put(2,amountView.getAmount()+"");
                    s.put(3,price);
                    orderItem.add(s);

                    //刷新订单数据源
                    o.notifyDataSetChanged();

                    //购物车计数器数据更新
                    point++;
                    point_tv.setText(point+"");
                    point_tv.setVisibility(View.VISIBLE);

                    //计算总价
                    total+=l[0];
                    total_tv.setText(total+"元");

                }else {

                    Toast.makeText(MainActivity.this,"没有选择商品数量！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public void initView() {

        total_tv = (TextView) findViewById(R.id.total_tv);

        point_tv = (TextView) findViewById(R.id.point);

        dishesKind_lv = (ListView) findViewById(R.id.dishesKind_lv);

        dishes_rv = (RecyclerView) findViewById(R.id.dishes_rv);

        car_iv = (ImageView) findViewById(R.id.car);

        ok_tv = (TextView) findViewById(R.id.ok_tv);

        final ImageView imageView = (ImageView) findViewById(R.id.shade);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.orderList);

       //初始化订单的数据，绑定数据源的信息。

        o = new OrderAdapter(orderItem,MainActivity.this);

        order_lv = (ListView) findViewById(R.id.order_lv);

        order_lv.setAdapter(o);

        //监听orderItem的增加删除，设置总价以及总数量, flag ？+ ：-,price 单价 ,sum 当前item的个数。

        o.setOnchangeListener(new OrderAdapter.OnchangeListener() {
            @Override
            public void onchangeListener(boolean flag,float price,int sum) {

                if(flag){

                    total += price;

                    total_tv.setText(total+"元");


                }else {

                    total -= price;

                    total_tv.setText(total+"元");

                    if(sum == 0){

                        point --;

                        point_tv.setText(point+"");

                        if(point == 0){

                            point_tv.setVisibility(View.INVISIBLE);
                        }


                    }




                }
            }
        });

        //获取屏幕尺寸

        DisplayMetrics  dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        //设置表单的容器的长度为视窗的一半高，由父类的节点获得

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout
                .getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h/2;
        linearLayout.setLayoutParams(layoutParams);


        car_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag){

                    linearLayout.setAnimation(AnimationUtil.moveToViewLocation());
                    linearLayout.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setListener(null);
                    flag = false;

                }else {

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
            }
        });

        dishesKindName = new ArrayList<>();

        leftAdapter = new DishesKindAdapter();

        dishesKind_lv.setAdapter(leftAdapter);

        //左侧点击事件监听
        dishesKind_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                leftAdapter.changeSelected(position);

            }

        });

        dishesKind_lv.performItemClick(dishesKind_lv.getChildAt(0),0, dishesKind_lv
                .getItemIdAtPosition(0));

        dishesAdapter = new DishesAdapter(MainActivity.this);

        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 3);//设置每行展示3个

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    return dishesAdapter.getItemViewType(position);
                }
            });
            dishes_rv.setLayoutManager(manager);
            dishes_rv.setAdapter(dishesAdapter);
            dishesAdapter.setOnItemClickListener(new DishesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, String name, float price) {
                    showDialog(name,price);
                }

            });


        //清空按钮
        delet_bt = (ImageButton) findViewById(R.id.delet);

        delet_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                point = 0;
                point_tv.setVisibility(View.INVISIBLE);

                total_tv.setText("0元");
                total = 0;
                orderItem.clear();
                o.notifyDataSetChanged();
            }
        });

        //提交按钮
        ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,PayActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(resultCode == RESULT_OK){

            MyLog.e("RESULT_OK");



        }else { //清空数据






        }
    }



    @Override
    public void showDishes(List<String> data,List<Integer> headPosition) {

        dishesAdapter.setmData(data);
        dishesAdapter.setHeadPosition(headPosition);
        dishesAdapter.notifyDataSetChanged();

    }

    @Override
    public void showKindName(List<String> data) {

        leftAdapter.setNames(data);
        leftAdapter.notifyDataSetChanged();
    }
    /**
     * 模拟原始数据
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
        if(id == R.id.action_search){


        }else if(id == R.id.action_cancel){

            myApp.cancleSharePreferences();
            Intent itent = new Intent();
            itent.setClass(MainActivity.this,LoginActivity.class);
            startActivity(itent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public class DishesKindAdapter extends BaseAdapter{

        private LayoutInflater listContainerLeft;
        private int mSelect = 0; //选中项

        public void setNames(List<String> names) {
            this.names = names;
        }

        private  List<String> names;
        public DishesKindAdapter(List<String> names){
            this.names = names ;
        }
        public DishesKindAdapter(){
        }

        @Override
        public int getCount() {
            return names == null?0:names.size();
        }

        @Override
        public Object getItem(int i) {
            return names.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            listContainerLeft = LayoutInflater.from(MainActivity.this);
            ListItemView listItemView = null;
            if(view == null){
                listItemView = new ListItemView();
                view = listContainerLeft.inflate(R.layout.view_kindname_lv, null);
                listItemView.tv_title = view.findViewById(R.id.title);
                listItemView.imageView = view.findViewById(R.id.imageView);
                view.setTag(listItemView);
            }
            else{
                listItemView = (ListItemView)view.getTag();
            }
            if(mSelect == i){
                view.setBackgroundResource(R.color.md_grey_50);  //选中项背景
                listItemView.imageView.setVisibility(View.VISIBLE);
            }else{
                view.setBackgroundResource(R.color.md_grey_100);  //其他项背景
                listItemView.imageView.setVisibility(View.INVISIBLE);
            }
            listItemView.tv_title.setText(names.get(i));

            return view;

        }


        public void changeSelected(int positon){ //刷新方法
            mSelect = positon;
            notifyDataSetChanged();
        }

       class ListItemView {

            TextView tv_title;
            ImageView imageView;
        }

    }

}
