package model;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Query;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.MyApplication;
import untils.MyLog;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 14:31
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 14:31
 * 修改备注：
 */

public class CouchBaseManger<T> implements IDBManager {
    private  Database database = null;

    public CouchBaseManger(){}

 /*   public CouchBaseManger(Context context){

        MyApplication application = (MyApplication)context.getApplicationContext();
        database = application.getDatabase();
    }*/

    private static  class SingletonHolder{


        private static final  CouchBaseManger c = new CouchBaseManger();
    }

    public static  CouchBaseManger getInstance(){


        return SingletonHolder.c;
    }



    @Override
    public void Testshow() {

        MyLog.e(getDishesKindsByClassName("DishesKind").size()+"~~~~~~");



    }

    @Override
    public boolean isLogin(String name, String pass) {

      Document document = new Document();

        document.setString("className","LoginUserBean");
        document.setString("mName",name);
        document.setString("mPassword",pass);

        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }



        Query query= Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("LoginUserBean").and(Expression.property("mName").equalTo(name).and(Expression.property("mPassword").equalTo(pass))));
        try {
            ResultSet resultSet= query.run();
            Result result;

            if (resultSet.next() != null){

              //   String id = result.getString(0);
                // Document doc=database.getDocument(id);
          /*       MyLog.e("数据库查询结果 mName "+doc.getString("mName"));
                 MyLog.e("数据库查询结果 mPassword "+doc.getString("mPassword"));*/
                return  true;

            }
           //

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public List<Document> getDishesKindsByClassName(String name) {

        List<Document> documentList = new ArrayList<>();
        Query query= Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(name));
        try {
            ResultSet resultSet= query.run();
            Result result;

            while ((result=resultSet.next())!=null){

                String id = result.getString(0);
                Document doc=database.getDocument(id);
                documentList.add(doc);
              // MyLog.e("数据库查询结果 Name "+doc.getString("name"));
                //MyLog.e("数据库查询结果 mPassword "+doc.getString("mPassword"));


            }
            //

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return documentList;
    }

    @Override
    public List<T> search(Object object) throws IllegalAccessException {

        List<T> list = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);

            //获取变量名

            String fieldName = field.getName();

            //获取变量值

            Object value = field.get(object);

            map.put(fieldName, value);
        }




        return list;
    }

    @Override
    public void save(Document document) throws CouchbaseLiteException {

        if(database != null){

            database.save(document);

        }else {

            MyLog.e("数据库不存在！");
        }


    }

    @Override
    public Document getMembers(String tel) {
        Document doc = null;
        Query query= Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("MembersC").and(Expression.property("tel").equalTo(tel)));

        try {
            ResultSet resultSet= query.run();
            Result result;
    
            if ((result=resultSet.next())!=null){

                String id = result.getString(0);
                 doc=database.getDocument(id);
                // MyLog.e("数据库查询结果 Name "+doc.getString("name"));
                //MyLog.e("数据库查询结果 mPassword "+doc.getString("mPassword"));

            }
            //

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        
        return doc;
    }

    @Override
    public Document getCard(String id) {
        Document doc = null;
        Query query= Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("CardTypeC").and(Expression.property("_id").equalTo(id)));

        try {
            ResultSet resultSet= query.run();
            Result result;

            if ((result=resultSet.next())!=null){

                String cardId = result.getString(0);
                doc=database.getDocument(cardId);
                // MyLog.e("数据库查询结果 Name "+doc.getString("name"));
                //MyLog.e("数据库查询结果 mPassword "+doc.getString("mPassword"));

            }


        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return doc;
    }

    @Override
    public List<Document> getByClassName(String name) {

        List<Document> documentList = new ArrayList<>();
        Query query= Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(name));
        try {
            ResultSet resultSet= query.run();
            Result result;

            while ((result=resultSet.next())!=null){

                String id = result.getString(0);
                Document doc=database.getDocument(id);
                documentList.add(doc);
                // MyLog.e("数据库查询结果 Name "+doc.getString("name"));
                //MyLog.e("数据库查询结果 mPassword "+doc.getString("mPassword"));


            }
            //

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return documentList;
    }

    @Override
    public void setContext(Context context) {


            MyApplication application = (MyApplication)context.getApplicationContext();
            database = application.getDatabase();


    }

    @Override
    public Object getById(String id) {

        return database != null?database.getDocument(id):null;
    }


    /**
     * @show 将bean通过反射转换成map
     *
     * @param obj
     * @return 返回map
     * @throws IllegalAccessException
     */
    public  Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {

                Map<String, Object> map = new HashMap<>();

                 Class<?> clazz = obj.getClass();

                for (Field field : clazz.getDeclaredFields()) {

                         field.setAccessible(true);

                         //获取变量名

                         String fieldName = field.getName();
                        // MyLog.e("映射得到的变量名  "+fieldName);
                         //获取变量值

                         Object value = field.get(obj);
                         MyLog.e("映射得到的变量值   "+value);
                        // map.put(fieldName, value);
                    }
               return map;
           }


}
