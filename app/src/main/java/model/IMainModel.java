package model;

import com.couchbase.lite.CouchbaseLiteException;

import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/18 15:21
 * 修改人：donghaifeng
 * 修改时间：2017/9/18 15:21
 * 修改备注：
 */

public interface IMainModel {




    void addTestData() throws CouchbaseLiteException;

    List<String> getKindName();




}
