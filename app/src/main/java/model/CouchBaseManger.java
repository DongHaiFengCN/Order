package model;

import android.content.Context;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.Conflict;
import com.couchbase.lite.ConflictResolver;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Log;
import com.couchbase.lite.Query;
import com.couchbase.lite.ReadOnlyDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class CouchBaseManger<T> implements IDBManager,ReplicatorChangeListener {

    private static final String TAG = "CouchBaseManger";
    private final static String DATABASE_NAME = "order";
    private final static String SYNCGATEWAY_URL = "blip://123.207.174.171:4984/kitchen/";

    private final static boolean SYNC_ENABLED = true;
    private  Database database = null;
    private Replicator replicator;
    private String Company_ID="wangbo008";


    public CouchBaseManger(Context context){

        DatabaseConfiguration config = new DatabaseConfiguration(context.getApplicationContext());

        config.setConflictResolver(getConflictResolver());//冲突解决机制

        try {
            database = new Database(DATABASE_NAME, config);

            startReplication(DATABASE_NAME,null);

        } catch (CouchbaseLiteException e) {
            // TODO: error handling
        }


    }

    private ConflictResolver getConflictResolver(){
        /**
         * Example: Conflict resolver that merges Mine and Their document.
         */
        return new ConflictResolver() {
            @Override
            public ReadOnlyDocument resolve(Conflict conflict) {
                ReadOnlyDocument mine = conflict.getMine();
                ReadOnlyDocument theirs = conflict.getTheirs();

                Document resolved = new Document();
                Set<String> changed = new HashSet<>();

                // copy all data from theirs document
                for (String key : theirs) {
                    resolved.setObject(key, theirs.getObject(key));
                    changed.add(key);
                }

                // copy all data from mine which are not in mine document
                for (String key : mine) {
                    if (!changed.contains(key))
                        resolved.setObject(key, mine.getObject(key));
                }

                Log.e(TAG, "ConflictResolver.resolve() resolved -> %s", resolved.toMap());

                return resolved;
            }
        };
    }

    @Override
    public Database getDatabase() {
        return database;
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

            if ((result=resultSet.next())!=null){

                 String id = result.getString(0);
                 Document doc=database.getDocument(id);
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


    @Override
    public void changed(Replicator replicator, Replicator.Status status, CouchbaseLiteException error) {

        Log.e(TAG, "**************"+"%s/%s",status.getProgress().getCompleted(),status.getProgress().getTotal());
    }


    // -------------------------
    // Replicator operation
    // -------------------------

    private void startReplication(String username, String password) {
        if (!SYNC_ENABLED) return;

        URI uri;
        try {
            uri = new URI(SYNCGATEWAY_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed parse URI: %s", e, SYNCGATEWAY_URL);
            return;
        }

        ReplicatorConfiguration config = new ReplicatorConfiguration(database, uri);
        List<String>channels =new ArrayList<>();
        channels.add(Company_ID);
        config.setChannels(channels);
        config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);



        // authentication

        if (username != null && password != null)
            config.setAuthenticator(new BasicAuthenticator(username, password));

        replicator = new Replicator(config);
        replicator.addChangeListener(this);
        replicator.start();
    }

    private void stopReplication() {
        if (!SYNC_ENABLED) return;

        replicator.stop();
    }


}
