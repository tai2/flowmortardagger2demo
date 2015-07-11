package net.tai2.flowmortardagger2demo.presenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import flow.Flow;
import flow.path.Path;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import javax.inject.Inject;
import mortar.ViewPresenter;
import net.tai2.flowmortardagger2demo.MyApplication;
import net.tai2.flowmortardagger2demo.R;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.Layout;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.mvpsupport.WithComponent;
import net.tai2.flowmortardagger2demo.view.TodoListView;

@Layout(R.layout.todo_list) @WithComponent(TodoListPath.Component.class) public class TodoListPath
    extends Path {

  @dagger.Component(dependencies = MyApplication.Component.class) @PerScreen
  public interface Component {
    void inject(TodoListView v);
  }

  @PerScreen public static class Presenter extends ViewPresenter<TodoListView> {

    private RealmResults<Todo> todoItems;

    @Inject SharedPreferences prefs;

    @Inject Presenter() {
    }

    @Override protected void onLoad(Bundle savedInstanceState) {
      getView().setFilter(prefs.getString("filter", "all"));
      showList();
    }

    private void showList() {
      Realm realm = Realm.getInstance(getView().getContext());
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

    @Override protected void onSave(Bundle outState) {
    }

    public void onFilterChanged(String filter) {
      prefs.edit().putString("filter", filter).commit();
      showList();
    }

    public void onItemClick(int position) {
      Flow.get(getView()).set(new TodoEditPath(todoItems.get(position).getId()));
    }

    public void onItemCheck(int position, boolean check) {
      Realm realm = Realm.getInstance(getView().getContext());
      Todo todo = todoItems.get(position);
      realm.beginTransaction();
      todo.setDone(check);
      realm.commitTransaction();
    }
  }
}
