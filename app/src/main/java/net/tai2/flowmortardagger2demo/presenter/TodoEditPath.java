package net.tai2.flowmortardagger2demo.presenter;

import android.os.Bundle;
import dagger.Provides;
import flow.Flow;
import flow.path.Path;
import io.realm.Realm;
import javax.inject.Inject;
import mortar.ViewPresenter;
import net.tai2.flowmortardagger2demo.MyApplication;
import net.tai2.flowmortardagger2demo.R;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.Layout;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.mvpsupport.WithComponent;
import net.tai2.flowmortardagger2demo.view.TodoEditView;

@Layout(R.layout.todo_edit) @WithComponent(TodoEditPath.Component.class) public class TodoEditPath
    extends Path {

  private String itemId;

  public TodoEditPath(String itemId) {
    this.itemId = itemId;
  }

  @dagger.Module public class Module {
    @Provides String provideItemId() {
      return itemId;
    }
  }

  @dagger.Component(dependencies = MyApplication.Component.class, modules = Module.class) @PerScreen
  public interface Component {
    void inject(TodoEditView v);
  }

  @PerScreen public static class Presenter extends ViewPresenter<TodoEditView> {

    @Inject String itemId;

    @Inject Presenter() {
    }

    @Override protected void onLoad(Bundle savedInstanceState) {
      if (savedInstanceState != null) {
        itemId = savedInstanceState.getString("itemId");
      }

      Realm realm = Realm.getInstance(getView().getContext());
      Todo todo = realm.where(Todo.class).equalTo("id", itemId).findFirst();
      getView().setContent(todo.getContent());
      getView().setAddedDate(todo.getAddedDate());
    }

    @Override protected void onSave(Bundle outState) {
      outState.putString("itemId", itemId);
    }

    @Override public void dropView(TodoEditView view) {
      super.dropView(view);
    }

    public void onEditClick() {
      String content = getView().getContent();
      if (!content.isEmpty()) {
        Realm realm = Realm.getInstance(getView().getContext());
        Todo todo = realm.where(Todo.class).equalTo("id", itemId).findFirst();
        realm.beginTransaction();
        todo.setContent(content);
        realm.commitTransaction();

        getView().hideKeyboard();
        Flow.get(getView()).goBack();
      }
    }

    public void onDeleteClick() {
      Realm realm = Realm.getInstance(getView().getContext());
      Todo todo = realm.where(Todo.class).equalTo("id", itemId).findFirst();
      realm.beginTransaction();
      todo.removeFromRealm();
      realm.commitTransaction();

      getView().hideKeyboard();
      Flow.get(getView()).goBack();
    }
  }
}
