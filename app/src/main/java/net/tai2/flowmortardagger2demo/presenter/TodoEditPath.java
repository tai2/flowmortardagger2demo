package net.tai2.flowmortardagger2demo.presenter;

import android.os.Bundle;
import dagger.Provides;
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
import net.tai2.flowmortardagger2demo.view.TodoEditView;

@Layout(R.layout.todo_edit) @WithComponent(TodoEditPath.Component.class) public class TodoEditPath
    extends Path {

  private String itemId;

  public TodoEditPath(String itemId) {
    this.itemId = itemId;
  }

  public interface View extends ContextHolder {
    String getContent();

    void setContent(String text);

    void setAddedDate(Date date);

    void hideKeyboard();
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

  @PerScreen public static class Presenter extends ViewPresenter<View> {

    @Inject RealmConfiguration realmConfig;
    @Inject String itemId;
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

    @Override protected void onLoad(Bundle savedInstanceState) {
      Todo todo = realm.where(Todo.class).equalTo("id", itemId).findFirst();
      getView().setContent(todo.getContent());
      getView().setAddedDate(todo.getAddedDate());
    }

    public void onEditClick() {
      String content = getView().getContent();
      if (!content.isEmpty()) {
        Todo todo = realm.where(Todo.class).equalTo("id", itemId).findFirst();
        realm.beginTransaction();
        todo.setContent(content);
        realm.commitTransaction();

        getView().hideKeyboard();
        Flow.get(getContext()).goBack();
      }
    }

    public void onDeleteClick() {
      Todo todo = realm.where(Todo.class).equalTo("id", itemId).findFirst();
      realm.beginTransaction();
      todo.removeFromRealm();
      realm.commitTransaction();

      getView().hideKeyboard();
      Flow.get(getContext()).goBack();
    }
  }
}
