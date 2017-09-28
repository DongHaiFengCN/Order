package view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.zm.order.R;

import java.util.List;

import Untils.MyLog;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/12 15:57
 * 修改人：donghaifeng
 * 修改时间：2017/9/12 15:57
 * 修改备注：
 */

public class DishesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    public static final int TYPE_ITEM_ONE = 1; //itemTitle一列
    public static final int TYPE_ITEM_THIRD = 3; //item多列
    public List<Integer> headPosition;
    private Context context;
    private OnItemClickListener mOnItemClickListener = null;
    private IDBManager idbManager;
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public DishesAdapter(Context context) {

        idbManager = DBFactory.get(DatabaseSource.CouchBase,context);
    }

    public void setmData(List<String> mData) {
        this.mData = mData;
    }

    public void setHeadPosition(List<Integer> headPosition) {
        this.headPosition = headPosition;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = null;

        if(viewType == TYPE_ITEM_THIRD){

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_title_recl, parent, false);

            return new TitleHolder(v);

        }else{

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_recl, parent, false);

            return new ItemHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

       if(holder instanceof TitleHolder){

        ((TitleHolder) holder).mTv.setText(mData.get(position));

       }else if (holder instanceof ItemHolder){

          Document document = (Document) idbManager.getById(mData.get(position));

           ((ItemHolder) holder).info.setText(document.getString("name"));
           ((ItemHolder) holder).price.setText(document.getFloat("price")+" 元/份");
       }
    }

    /**
     * 一行展示几个
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {


        for (int i = 0; i < headPosition.size(); i++) {

            if(position == headPosition.get(i) ){

                return TYPE_ITEM_THIRD;
            }
        }

        return TYPE_ITEM_ONE;

    }

    @Override
    public int getItemCount() {

        return mData == null?0:mData.size();

    }

    public class TitleHolder extends RecyclerView.ViewHolder{
        public  TextView mTv;
        public TitleHolder(View itemView) {
            super(itemView);

            mTv = itemView.findViewById(R.id.item_title);
        }
    }
    /**
     *  设置点击事件，LinearLayout布局文件要设置  android:focusable="true" android:clickable="true"
     *  android:addStatesFromChildren="true"否则select无效
     */
    public class ItemHolder extends RecyclerView.ViewHolder{
        public  TextView info;

        private  TextView price;
        public ItemHolder(View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.item_info);
            price = itemView.findViewById(R.id.price_tv);


            LinearLayout linearLayout = itemView.findViewById(R.id.select_ln);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mOnItemClickListener != null) {

                        String p[]= price.getText().toString().split("\\s+");//截取获取当前单价

                        mOnItemClickListener.onItemClick(view,info.getText().toString(),Float.parseFloat(p[0]));

                    }
                }
            });

        }
    }

    public  interface OnItemClickListener {

        void onItemClick(View view,String name,float price);
    }

}
