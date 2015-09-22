package net.tai2.flowmortardagger2demo;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.util.Date;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.presenter.TodoEditPath;
import net.tai2.flowmortardagger2demo.presenter.TodoListPath;

public class TodoEditTest extends ActivityInstrumentationTestCase2<MainActivity> {

  @dagger.Module public class Module {
    @Provides String provideItemId() {
      return itemId;
    }
  }

  @dagger.Component(dependencies = MyApplication.Component.class, modules = Module.class) @PerScreen
  interface Component {
    RealmConfiguration realmConfig();

    TodoEditPath.Presenter presenter();
  }

  class MockTodoEditView implements TodoEditPath.View {

    Context context;
    String content;

    public MockTodoEditView(Context context) {
      this.context = context;
    }

    @Override public String getContent() {
      return content;
    }

    @Override public void setContent(String text) {
      content = text;
    }

    @Override public void setAddedDate(Date date) {

    }

    @Override public void hideKeyboard() {

    }

    @Override public Context getContext() {
      return context;
    }
  }

  Component component;
  MockTodoEditView mockView;
  Realm realm;
  String itemId;
  TodoEditPath.Presenter presenter;

  public TodoEditTest() {
    super(MainActivity.class);
  }

  public void setUp() throws Exception {
    super.setUp();

    MyApplication app =
        (MyApplication) getInstrumentation().getTargetContext().getApplicationContext();
    app.setRootScope(TestUtils.createTestRootScope(app));
    MyApplication.Component appComponent = DaggerService.getDaggerComponent(app);
    component =
        DaggerTodoEditTest_Component.builder().component(appComponent).module(new Module()).build();

    Realm.deleteRealm(component.realmConfig());
    Realm r = Realm.getInstance(component.realmConfig());
    itemId = TestUtils.addTodo(r, "Sample Todo", false);
    r.close();

    presenter = component.presenter();

    getActivity(); // start Activity.
    getInstrumentation().runOnMainSync(new Runnable() {
      @Override public void run() {
        realm = Realm.getInstance(component.realmConfig());

        mockView = new MockTodoEditView(getActivity());
        mockView.content = "Edited Text";
        presenter.takeView(mockView);
      }
    });
  }

  public void tearDown() throws Exception {
    getInstrumentation().runOnMainSync(new Runnable() {
      @Override public void run() {
        presenter.dropView(mockView);
        realm.close();
      }
    });
    super.tearDown();
  }

  @UiThreadTest public void testEditClick() {

    presenter.onEditClick();

    getInstrumentation().waitForIdle(new Runnable() {
      @Override public void run() {
        Todo todo = realm.where(Todo.class).findFirst();
        assertEquals(mockView.getContent(), todo.getContent());
        assertEquals(TodoListPath.class, Flow.get(getActivity()).getHistory().top().getClass());
      }
    });
  }

  @UiThreadTest public void testDeleteClick() {

    presenter.onDeleteClick();

    getInstrumentation().waitForIdle(new Runnable() {
      @Override public void run() {
        assertTrue(realm.where(Todo.class).findAll().isEmpty());
        assertEquals(TodoListPath.class, Flow.get(getActivity()).getHistory().top().getClass());
      }
    });
  }
}