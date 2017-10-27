package model;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.table.TableC;


/**
 * Class description goes here.
 * <p>
 * Created by loongsun on 2017/5/28.
 * <p>
 * email: 125736964@qq.com
 */
public class LiveTableRecyclerAdapter extends RecyclerView.Adapter<LiveTableRecyclerAdapter.TestHolderView> {

    private Database  db;
    private LiveQuery listsLiveQuery = null;
    protected Context context;
    private List<String> documentList;
    private onRecyclerViewItemClickListener itemClickListener = null;

    public LiveTableRecyclerAdapter(Context context,Database db,String areaId)
    {
        if(db == null) throw new IllegalArgumentException();
        this.db = db;
        this.context = context;
        this.listsLiveQuery = listsLiveQuery(areaId);
        this.listsLiveQuery.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change)
            {
                clear();
                ResultSet rs = change.getRows();
                Result row;
                while ((row = rs.next()) != null)
                {
                    documentList.add(row.getString(0));
                }
                notifyDataSetChanged();
            }
        });
        this.listsLiveQuery.run();
    }
    private LiveQuery listsLiveQuery( String areaId)
    {
        return Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo("TableC")
                        .and(Expression.property("areaId").equalTo(areaId)))
                .orderBy(Ordering.property("tableNum").ascending())
                .toLive();
    }
    @Override
    public LiveTableRecyclerAdapter.TestHolderView onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_table, parent, false);

        view.setOnClickListener(new View.OnClickListener()
        {   //为每一个item绑定监听
            public void onClick(View v)
            {
                // TODO 自动生成的方法存根
                if (itemClickListener != null)
                itemClickListener.onItemClick(v,v.getTag());
            }
        });

    TestHolderView testHolderView = new TestHolderView(view);
        return testHolderView;
    }

    private void clear()
    {
     if(documentList==null)
         documentList=new ArrayList<String>();
        documentList.clear();
    }
    @Override
    public void onBindViewHolder(LiveTableRecyclerAdapter.TestHolderView holder, int position)
    {
//        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
//        params.height = 100;
//        params.width = 100;
//        holder.itemView.setLayoutParams(params);//把params设置item布局


        String docId=documentList.get(position);
        TableC tableobj=CDBHelper.getObjById(context.getApplicationContext(),docId, TableC.class);
        int state=tableobj.getState();
        switch (state)
        {
            case 0:
                holder.cardView.setCardBackgroundColor(Color.rgb(86,209,109));
                break;
            case 1:
                holder.cardView.setCardBackgroundColor(Color.rgb(255,193,17));
                break;
            case  2:
                holder.cardView.setCardBackgroundColor(Color.rgb(253,117,80));
                break;
            default:
                break;
        }

        holder.tv.setText(tableobj.getTableName());
        holder.itemView.setTag(tableobj);
    }

    @Override
    public int getItemCount()
    {
        return documentList != null ? documentList.size() : 0;
    }
    public class TestHolderView extends RecyclerView.ViewHolder
    {
        protected ImageView img;
        protected TextView tv;
        protected CardView  cardView;
        public TestHolderView(View itemView)
        {
            super(itemView);
            tv = itemView.findViewById(R.id.item_tablestate_name);
            cardView= itemView.findViewById(R.id.table_state_cardview);
        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v, Object tag);
    }
}
