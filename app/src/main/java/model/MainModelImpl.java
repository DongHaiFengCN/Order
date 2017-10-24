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

public class MainModelImpl<T> implements IMainModel {

    private IDBManager idbManager;



    private List<String> all = new ArrayList<>();




    private List<Integer> headList = new ArrayList<>();

    public MainModelImpl(Context context)
    {


        idbManager =  DBFactory.get(DatabaseSource.CouchBase,context);
    }

    public List<String> getAll() {
        return all;
    }
    @Override
    public void addTestData() throws CouchbaseLiteException {

        Document document = new Document();
        document.setString("className","DishesKind");
        document.setString("name","特色菜");
        Array array =new Array();

        Document d = new Document();
        d.setString("className","Dishes");
        d.setString("name","黄焖鸡");
        d.setFloat("price",10f);
        idbManager.save(d);
        array.addString(d.getId());

        Document d1 = new Document();
        d1.setString("className","Dishes");
        d1.setString("name","鸡公煲");
        d1.setFloat("price",11f);
        idbManager.save(d1);
        array.addString(d1.getId());

        Document d2 = new Document();
        d2.setString("className","Dishes");
        d2.setString("name","白斩鸡");
        d2.setFloat("price",12f);
        idbManager.save(d2);
        array.addString(d2.getId());

        Document d3 = new Document();
        d3.setString("className","Dishes");
        d3.setString("name","烤鸭");
        d3.setFloat("price",13f);
        idbManager.save(d3);
        array.addString(d3.getId());

        document.setArray("dishesList",array);
        idbManager.save(document);





        Document documentA = new Document();
        documentA.setString("className","DishesKind");
        documentA.setString("name","主食");
        Array array1 =new Array();

        Document d11 = new Document();
        d11.setString("className","Dishes");
        d11.setString("name","米饭");
        d11.setFloat("price",2f);
        idbManager.save(d11);
        array1.addString(d11.getId());

        Document d12 = new Document();
        d12.setString("className","Dishes");
        d12.setString("name","包子");
        d12.setFloat("price",3f);
        idbManager.save(d12);
        array1.addString(d12.getId());

        Document d13 = new Document();
        d13.setString("className","Dishes");
        d13.setString("name","馒头");
        d13.setFloat("price",4f);
        idbManager.save(d13);
        array1.addString(d13.getId());


        documentA.setArray("dishesList",array1);
        idbManager.save(documentA);



        idbManager.Testshow();


    }

    @Override
    public List<String> getKindName() {


      List<Document>  kinds = idbManager.getDishesKindsByClassName("DishesKind");

        List<String> names = new ArrayList<>();
        headList.add(0);

        for(int j = 0 ; j<kinds.size();j++){

            names.add(kinds.get(j).getString("name"));
            all.add(kinds.get(j).getString("name"));

            List<T> list = (List<T>) kinds.get(j).getArray("dishesList").toList();

            int len = list.size();

            for (int i = 0; i < list.size() ; i++) {


                all.add((String) list.get(i));

            }
            if(j<kinds.size()-1){

                headList.add(headList.get(j)+len+1);
            }

        }


        return names;
    }



    public List<Integer> getHeadList() {
        return headList;
    }

}
