package net.tai2.flowmortardagger2demo.presenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import flow.Flow;
import flow.path.Path;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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
import net.tai2.flowmortardagger2demo.view.TodoListView;

@Layout(R.layout.todo_list) @WithComponent(TodoListPath.Component.class) public class TodoListPath
    extends Path {

  public interface View extends ContextHolder {
    void showList(RealmResults<Todo> results);

    void setFilter(String filter);
  }

  @dagger.Component(dependencies = MyApplication.Component.class) @PerScreen
  public interface Component {
    void inject(TodoListView v);
  }

  @PerScreen public static class Presenter extends ViewPresenter<View> {

    @Inject SharedPreferences prefs;
    @Inject RealmConfiguration realmConfig;
    private Realm realm;
    private RealmResults<Todo> todoItems;

    @Inject Presenter() {
    }

    @Override public void onEnterScope(MortarScope scope) {
      realm = Realm.getInstance(realmConfig);
    }

    @Override protected void onLoad(Bundle savedInstanceState) {
      getView().setFilter(prefs.getString("filter", "all"));
      showList();
    }

    @Override public void onExitScope() {
      realm.close();
      realm = null;
    }

    private void showList() {
      RealmQuery<Todo> query = realm.where(Todo.class);
      String filter = prefs.getString("filter", "all");
      if (filter.equals("done")) {
        query.equalTo("done", true);
      } else if (filter.equals("undone")) {
        query.equalTo("done", false);
      }
      todoItems = query.findAll();
      getView().showList(todoItems);
    }

    public void onFilterChanged(String filter) {
      prefs.edit().putString("filter", filter).commit();
      showList();
    }

    public void onItemClick(int position) {
      Flow.get(getContext()).set(new TodoEditPath(todoItems.get(position).getId()));
    }

    public void onItemCheck(int position, boolean check) {
      Todo todo = todoItems.get(position);
      realm.beginTransaction();
      todo.setDone(check);
      realm.commitTransaction();
    }
  }
}
