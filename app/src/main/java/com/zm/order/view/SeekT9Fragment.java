package com.zm.order.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Fragment extends Fragment implements IMainView{

    @BindView(R.id.activity_seek_list)
    ListView activitySeekList;
    @BindView(R.id.activity_seek_edit)
    EditText activitySeekEdit;
    @BindView(R.id.ibtn_key_1)
    ImageView ibtnKey1;
    @BindView(R.id.ibtn_key_2)
    ImageView ibtnKey2;
    @BindView(R.id.ibtn_key_3)
    ImageView ibtnKey3;
    @BindView(R.id.ibtn_key_4)
    ImageView ibtnKey4;
    @BindView(R.id.ibtn_key_5)
    ImageView ibtnKey5;
    @BindView(R.id.ibtn_key_6)
    ImageView ibtnKey6;
    @BindView(R.id.ibtn_key_7)
    ImageView ibtnKey7;
    @BindView(R.id.ibtn_key_8)
    ImageView ibtnKey8;
    @BindView(R.id.ibtn_key_9)
    ImageView ibtnKey9;
    @BindView(R.id.ibtn_key_l)
    ImageView ibtnKeyL;
    @BindView(R.id.ibtn_key_0)
    ImageView ibtnKey0;
    @BindView(R.id.ibtn_key_r)
    ImageView ibtnKeyR;
    @BindView(R.id.ibtn_key_del)
    ImageView ibtnKeyDel;
    Unbinder unbinder;
    private ArrayList<String> list = new ArrayList<>();

    private boolean flag = true;

    private float total = 0.0f;

    private OrderAdapter o;

    private int point = 0;

    private List<SparseArray<Object>> orderItem = new ArrayList<>();

    private SeekT9Adapter seekT9Adapter ;
    private List<String> mData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_seek, container, false);

        unbinder = ButterKnife.bind(this, view);
        seekT9Adapter = new SeekT9Adapter(getActivity());
        mData = new ArrayList<>();
        IMainPresenter iMainView = new MainPresenterImpl(this);
        iMainView.init();
        activitySeekList.setAdapter(seekT9Adapter);

        return view;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // 查询方法
    public void search(String search) {
        mData.clear();

        seekT9Adapter.notifyDataSetChanged();
    }

        @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.activity_seek_edit, R.id.ibtn_key_1, R.id.ibtn_key_2, R.id.ibtn_key_3, R.id.ibtn_key_4, R.id.ibtn_key_5, R.id.ibtn_key_6, R.id.ibtn_key_7, R.id.ibtn_key_8, R.id.ibtn_key_9, R.id.ibtn_key_l, R.id.ibtn_key_0, R.id.ibtn_key_r, R.id.ibtn_key_del})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.activity_seek_edit:

                break;

            case R.id.ibtn_key_1:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "1");

                break;

            case R.id.ibtn_key_2:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "2");

                break;

            case R.id.ibtn_key_3:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "3");

                break;

            case R.id.ibtn_key_4:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "4");

                break;

            case R.id.ibtn_key_5:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "5");

                break;

            case R.id.ibtn_key_6:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "6");

                break;

            case R.id.ibtn_key_7:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "7");

                break;

            case R.id.ibtn_key_8:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "8");

                break;

            case R.id.ibtn_key_9:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "9");

                break;

            case R.id.ibtn_key_l:

                break;

            case R.id.ibtn_key_0:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "0");

                break;

            case R.id.ibtn_key_r:

                break;
            case R.id.ibtn_key_del:
                int length = activitySeekEdit.getSelectionEnd();

                if (length > 1)

                {

                    activitySeekEdit.getText().delete(length - 1, length);

                }

                if (length == 1)

                {

                    activitySeekEdit.getText().delete(length - 1, length);

                }
                break;
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void showDishes(List<String> data, List<Integer> headList) {



    }

    @Override
    public void showKindName(List<String> data) {
        seekT9Adapter.setmData(data);
        seekT9Adapter.notifyDataSetChanged();
    }

    @Override
    public Context getIMainViewActivity() {

        return getActivity();
    }
}
