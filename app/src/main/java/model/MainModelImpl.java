package model;

import android.content.Context;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/18 15:52
 * 修改人：donghaifeng
 * 修改时间：2017/9/18 15:52
 * 修改备注：
 */

public class MainModelImpl<T>   {

    private IDBManager idbManager;



    private List<String> all = new ArrayList<>();




    private List<Integer> headList = new ArrayList<>();

    public MainModelImpl(Context context)
    {


        idbManager =  DBFactory.get(DatabaseSource.CouchBase,context);
    }




}
