package presenter;

import android.content.Context;

import model.MainModelImpl;

import com.couchbase.lite.CouchbaseLiteException;
import com.zm.order.view.IMainView;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/18 15:18
 * 修改人：donghaifeng
 * 修改时间：2017/9/18 15:18
 * 修改备注：
 */

public class MainPresenterImpl implements IMainPresenter {

    IMainView context;
    MainModelImpl mainModel;

    public MainPresenterImpl(IMainView context){
        this.context = context;

    }
    @Override
    public void LoadingDisheKind() {

    }

    @Override
    public void init() {

       /*  mainModel = new MainModelImpl((Context) context);
       try {
            mainModel.addTestData();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }*/




        context.initView();

        mainModel = new MainModelImpl((Context) context);


        mainModel.getHeadList();

        context.showDishes(mainModel.getAll(), mainModel.getHeadList());

        context.showKindName(mainModel.getKindName());




    }

    @Override
    public void setFactor(int position) {

        //通过位置设置滑动位置





    }
}
