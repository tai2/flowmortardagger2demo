package net.tai2.flowmortardagger2demo;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.util.Date;
import mortar.MortarScope;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;

public final class TestUtils {

  public static MortarScope createTestRootScope(final Context context) {
    MyApplication.Component component = DaggerService.createComponent(MyApplication.Component.class,
        new MyApplication.Module(context) {
          @Override RealmConfiguration provideRealmConfiguration() {
            return new RealmConfiguration.Builder(context).name("testrealm.realm").build();
          }
        });
    return MortarScope.buildRootScope()
        .withService(DaggerService.SERVICE_NAME, component)
        .build("Root");
  }

  public static String addTodo(Realm realm, String content, boolean done) {
    Todo todo = Todo.create();
    todo.setContent(content);
    todo.setAddedDate(new Date());
    todo.setDone(done);
    realm.beginTransaction();
    realm.copyToRealm(todo);
    realm.commitTransaction();
    return todo.getId();
  }

  private TestUtils() {
  }
}
