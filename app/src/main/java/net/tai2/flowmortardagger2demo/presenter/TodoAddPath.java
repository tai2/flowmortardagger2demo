package net.tai2.flowmortardagger2demo.presenter;

import flow.Flow;
import flow.path.Path;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.util.Date;
import javax.inject.Inject;
import mortar.MortarScope;
import net.tai2.flowmortardagger2demo.MyApplication;
import net.tai2.flowmortardagger2demo.R;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.ContextHolder;
import net.tai2.flowmortardagger2demo.mvpsupport.Layout;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.mvpsupport.ViewPresenter;
import net.tai2.flowmortardagger2demo.mvpsupport.WithComponent;
import net.tai2.flowmortardagger2demo.view.TodoAddView;

@Layout(R.layout.todo_add) @WithComponent(TodoAddPath.Component.class) public class TodoAddPath
    extends Path {

  public interface View extends ContextHolder {
    String getContent();

    void hideKeyboard();
  }

  @dagger.Component(dependencies = MyApplication.Component.class) @PerScreen
  public interface Component {
    void inject(TodoAddView v);
  }

  @PerScreen public static class Presenter extends ViewPresenter<View> {

    @Inject RealmConfiguration realmConfig;
    private Realm realm;

    @Inject Presenter() {
    }

    @Override public void onEnterScope(MortarScope scope) {
      realm = Realm.getInstance(realmConfig);
    }

    @Override public void onExitScope() {
      realm.close();
      realm = null;
    }

    public void onAddClick() {
      String content = getView().getContent();
      if (!content.isEmpty()) {
        Todo todo = Todo.create();
        todo.setContent(content);
        todo.setAddedDate(new Date());
        todo.setDone(false);
        realm.beginTransaction();
        realm.copyToRealm(todo);
        realm.commitTransaction();

        getView().hideKeyboard();
        Flow.get(getContext()).goBack();
      }
    }
  }
}
