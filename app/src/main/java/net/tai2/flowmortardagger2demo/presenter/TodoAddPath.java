package net.tai2.flowmortardagger2demo.presenter;

import android.os.Bundle;
import flow.Flow;
import flow.path.Path;
import io.realm.Realm;
import java.util.Date;
import javax.inject.Inject;
import mortar.ViewPresenter;
import net.tai2.flowmortardagger2demo.MyApplication;
import net.tai2.flowmortardagger2demo.R;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.Layout;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.mvpsupport.WithComponent;
import net.tai2.flowmortardagger2demo.view.TodoAddView;

@Layout(R.layout.todo_add) @WithComponent(TodoAddPath.Component.class) public class TodoAddPath
    extends Path {

  @dagger.Component(dependencies = MyApplication.Component.class) @PerScreen
  public interface Component {
    void inject(TodoAddView v);
  }

  @PerScreen public static class Presenter extends ViewPresenter<TodoAddView> {

    @Inject Presenter() {
    }

    @Override protected void onLoad(Bundle savedInstanceState) {
    }

    @Override protected void onSave(Bundle outState) {
    }

    @Override public void dropView(TodoAddView view) {
      super.dropView(view);
    }

    public void onAddClick() {
      String content = getView().getContent();
      if (!content.isEmpty()) {
        Todo todo = Todo.create();
        todo.setContent(content);
        todo.setAddedDate(new Date());
        todo.setDone(false);
        Realm realm = Realm.getInstance(getView().getContext());
        realm.beginTransaction();
        realm.copyToRealm(todo);
        realm.commitTransaction();

        getView().hideKeyboard();
        Flow.get(getView()).goBack();
      }
    }
  }
}
