package com.zm.order.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.zm.order.R;
import com.zm.order.view.adapter.AdapterLeft;
import com.zm.order.view.adapter.AdapterRight;

import java.util.ArrayList;
import java.util.List;

import model.CDBHelper;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;

/**
 * Created by lenovo on 2017/10/30.
 */

public class OrderDeom<T> extends Fragment  implements IMainView{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListViewLeft;

    private AdapterLeft mAdapterLeft;



    private RecyclerView mListViewRight;

    private AdapterRight mAdapterRight;



    private ArrayList<OrderBean> dataList = new ArrayList<>();

    private List<String> titleList = new ArrayList<>();

    private ArrayList<Integer> titlePosList = new ArrayList<>();
    private IDBManager idbManager;
    List<Object> DishesIdList;

    private String mCurTitle = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frame_order,null);
        mListViewLeft = (ListView) view.findViewById(R.id.order_list);
        mListViewRight = (RecyclerView) view.findViewById(R.id.dishes_rv);

        initView();
        IMainPresenter iMainView = new MainPresenterImpl(this);
        iMainView.init();
        //initData();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




    }



    public void initView(){



        mAdapterLeft = new AdapterLeft(getActivity());
        //从数据库获取菜品类别
        titleList = CDBHelper.getIdsByWhere(getActivity(),
                Expression.property("className").equalTo("DishesKindC")
                        .and(Expression.property("isSetMenu").equalTo(false)),
                Ordering.property("kindName").ascending());
        mAdapterLeft.setmDataList(titleList);
        mAdapterLeft.notifyDataSetChanged();

        mListViewLeft.setAdapter(mAdapterLeft);

        mListViewLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                mAdapterLeft.setSelection(pos);

                if (null != titleList && titleList.size()>pos)

                    mAdapterRight.setSelection(pos);


                final List<String> dishesIdList=new ArrayList<>();
                //获取点击的菜品类别的Document
                Document KindDocument= CDBHelper.getDocByID(getActivity(),titleList.get(pos));
                //获取此Document下的菜品Id号
                if(KindDocument.getArray("dishesListId")!=null)
                {
                    DishesIdList= KindDocument.getArray("dishesListId").toList();
                    //增强for循环读取id
                    for(Object DishesId:DishesIdList)
                    {
                        if(DishesId==null)
                            continue;
                        dishesIdList.add(DishesId.toString());
                    }
                }

            }

        });





        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mListViewRight.setLayoutManager(linearLayoutManager);

        mAdapterRight = new AdapterRight(getActivity(), mListViewRight);

        mListViewRight.addItemDecoration(new ItemDecoration(getActivity(), dataList, new ItemDecoration.OnDecorationCallback() {

            @Override

            public String onGroupId(int pos) {

                if (dataList.get(pos).getTitle() != null)

                    return dataList.get(pos).getTitle();

                return "-1";

            }



            @Override

            public String onGroupFirstStr(int pos) {

                if (dataList.get(pos).getTitle() != null)

                    return dataList.get(pos).getTitle();

                return "";

            }



            @Override

            public void onGroupFirstStr(String title) {

                for (int i=0; i<titleList.size(); i++){

                    if (!mCurTitle.equals(title) && title.equals(titleList.get(i))){

                        mCurTitle = title;

                        mAdapterLeft.setSelection(i);

                        Log.i(TAG, "onGroupFirstStr: i = "+i);

                    }

                }

            }

        }));

        mListViewRight.setAdapter(mAdapterRight);

    }



    @Override
    public void showDishes(List<String> data, List<Integer> headList) {


    }

    @Override
    public void showKindName(List<String> data) {

    }

    @Override
    public Context getIMainViewActivity() {
        return getActivity();
    }


    /**

     * 数据

     */

    private void initData(){



        List<Document>  kinds = idbManager.getDishesKindsByClassName("DishesKind");
        List<String> all = new ArrayList<>();
        List<String> names = new ArrayList<>();
        titlePosList.add(0);

        for(int j = 0 ; j<kinds.size();j++){

            titleList.add(kinds.get(j).getString("name"));
            all.add(kinds.get(j).getString("name"));

            List<T> list = (List<T>) kinds.get(j).getArray("dishesList").toList();

            int len = list.size();

            for (int i = 0; i < list.size() ; i++) {


                all.add((String) list.get(i));

            }
            if(j<kinds.size()-1){

                titlePosList.add(titlePosList.get(j)+len+1);
            }

        }

        titlePosList.add(dataList.size());

        for (int i=0; i<15; i++){

            OrderBean bean = new OrderBean();

            bean.setTitle("1");

            bean.setName("xxxx");

            dataList.add(bean);

        }

        titleList.add(dataList.get(dataList.size()-1).getTitle());

        titlePosList.add(dataList.size());

        for (int i=0; i<20; i++){

            OrderBean bean = new OrderBean();

            bean.setTitle("2");

            bean.setName("cccc");

            dataList.add(bean);

        }

        titleList.add(dataList.get(dataList.size()-1).getTitle());

        titlePosList.add(dataList.size());

        for (int i=0; i<10; i++){

            OrderBean bean = new OrderBean();

            bean.setTitle("3");

            bean.setName("dddd");

            dataList.add(bean);

        }

        titleList.add(dataList.get(dataList.size()-1).getTitle());

        mAdapterLeft.notifyDataSetChanged();

        mAdapterRight.notifyDataSetChanged();

    }

}
