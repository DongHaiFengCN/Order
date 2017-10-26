package com.zm.order.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.zm.order.R;

import java.util.List;

import bean.table.AreaC;

public class DeskActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewArea;
    private List<AreaC> areaCList;
    //private AreaAdapter areaAdapter;

    private RecyclerView listViewDesk;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);
        initWidget();
    }
    private void initWidget()
    {
        listViewArea = (ListView)findViewById(R.id.lv_area);

    }
}
