package model;

import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 14:30
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 14:30
 * 修改备注：
 */

public interface IDBManager<T> {

    void Testshow();

    boolean isLogin(String name, String pass);

    List<Document> getDishesKindsByClassName(String name);

    List<T> search(T t) throws IllegalAccessException;

    void save(Document document) throws CouchbaseLiteException;

    <T> T getById(String id);

    Document getMembers(String tel);
    Document getCard(String id);

    List<Document> getByClassName(String name);

}
