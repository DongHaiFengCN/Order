package com.zm.order.view.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zm.order.R;
import com.zm.order.view.OrderBean;

import java.util.ArrayList;
import java.util.List;

import model.CDBHelper;

/**
 * Created by zyf on 2017/5/8.
 * 右边RecyclerView适配器
 */

public class AdapterRight extends RecyclerView.Adapter {

    private static final String TAG = AdapterRight.class.getSimpleName();
    private Context mContext;
    private List<String> mDataList = new ArrayList<>();
    private List<Integer> mTitleIntList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private boolean mShouldScroll = false;

    public AdapterRight(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
    }

    public void setmDataList(List<String> dataList){
        mDataList = dataList;
    }

    public void setmTitleIntList( List<Integer> titleIntList){
        mTitleIntList = titleIntList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_right, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (null != mDataList && mDataList.size() > 0) {
            viewHolder.textContent.setText(CDBHelper.getDocByID(mContext,mDataList.get(position)).getString("dishesName"));
        } else {
            Log.i(TAG, "getView: null == mDataList");
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setSelection(int pos) {
        if (pos < mDataList.size()) {
            moveToPosition(pos);
        }
    }

    private void moveToPosition(final int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int pos = mTitleIntList.get(n);
        mShouldScroll = false;
        mRecyclerView.setOnScrollListener(new RecyclerViewListener(n));
        //然后区分情况
        if (pos <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            mRecyclerView.scrollToPosition(pos);
        } else if (pos <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = mRecyclerView.getChildAt(pos - firstItem).getTop() - 50;
            mRecyclerView.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时,调用scrollToPosition只会将该项滑动到屏幕上。需要再次滑动到顶部
            mRecyclerView.scrollToPosition(pos);
            //这里这个变量是用在RecyclerView滚动监听里面的
            mShouldScroll = true;
        }
    }

    /**
     * 滚动监听
     */
    class RecyclerViewListener extends RecyclerView.OnScrollListener{
        private int n = 0;
        RecyclerViewListener(int n) {
            this.n = n;
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动
            if (mShouldScroll ){
                mShouldScroll = false;
                moveToPosition(n);
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        ViewHolder(View itemView) {
            super(itemView);
            textContent = (TextView) itemView.findViewById(R.id.text_content);
        }
    }
}
