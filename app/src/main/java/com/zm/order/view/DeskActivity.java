package com.zm.order.view;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.zm.order.R;

import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.table.TableC;
import model.AreaAdapter;
import model.LiveTableRecyclerAdapter;

public class DeskActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewArea;
    private List<AreaC> areaCList;
    private AreaAdapter areaAdapter;
    private RecyclerView listViewDesk;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initWidget();
    }
    private void initWidget()
    {
        MyApplication application = (MyApplication) getApplication();
        db = application.getDatabase();
        if(db == null) throw new IllegalArgumentException();
        areaAdapter = new AreaAdapter(this, db);
        listViewArea = (ListView)findViewById(R.id.lv_area);
        listViewArea.setAdapter(areaAdapter);
        listViewArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                areaAdapter.setSelectItem(i);
                String id = areaAdapter.getItem(i);
                showDeskListView(id);
            }
        });
        areaAdapter.setSelectItem(0);
        showDeskListView(areaAdapter.getItem(0));



    }
  private void showDeskListView(String areaId)
  {
      LiveTableRecyclerAdapter tableadapter=new LiveTableRecyclerAdapter(this,db,areaId);
      tableadapter.setOnItemClickListener(new LiveTableRecyclerAdapter.onRecyclerViewItemClickListener()
      {
          @Override
          public void onItemClick(View view,Object data)
          {
              TableC tableC=(TableC)data;
              Intent mainIntent = new Intent();
              mainIntent.putExtra("state", tableC.getState());
              mainIntent.putExtra("tableId", tableC.get_id());
              mainIntent.setClass(DeskActivity.this, MainActivity.class);
              startActivityForResult(mainIntent, 1);
          }
      });
      //3,recyclerview created
      //RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
      listViewDesk = (RecyclerView)findViewById(R.id.lv_desk);
      //listViewDesk.setLayoutParams(params);
      listViewDesk.setItemAnimator(new DefaultItemAnimator());
      listViewDesk.setLayoutManager(new GridLayoutManager(this,3));
      listViewDesk.setAdapter(tableadapter);

  }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
