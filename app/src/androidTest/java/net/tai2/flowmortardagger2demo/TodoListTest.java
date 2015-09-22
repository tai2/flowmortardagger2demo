package net.tai2.flowmortardagger2demo;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import javax.inject.Inject;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.presenter.TodoEditPath;
import net.tai2.flowmortardagger2demo.presenter.TodoListPath;

public class TodoListTest extends ActivityInstrumentationTestCase2<MainActivity> {

  @dagger.Component(dependencies = MyApplication.Component.class) @PerScreen interface Component {
    void inject(TodoListTest test);
  }

  class MockTodoListView implements TodoListPath.View {

    Context context;
    RealmResults<Todo> list;

    public MockTodoListView(Context context) {
      this.context = context;
    }

    @Override public void showList(RealmResults<Todo> results) {
      list = results;
    }

    @Override public void setFilter(String filter) {

    }

    @Override public Context getContext() {
      return context;
    }
  }

  MockTodoListView mockView;
  Component component;
  Realm realm;
  @Inject RealmConfiguration realmConfig;
  @Inject TodoListPath.Presenter presenter;

  public TodoListTest() {
    super(MainActivity.class);
  }

  public void setUp() throws Exception {
    super.setUp();

    MyApplication app =
        (MyApplication) getInstrumentation().getTargetContext().getApplicationContext();
    app.setRootScope(TestUtils.createTestRootScope(app));
    MyApplication.Component appComponent = DaggerService.getDaggerComponent(app);
    component = DaggerTodoListTest_Component.builder().component(appComponent).build();
    component.inject(this);

    getInstrumentation().waitForIdleSync();
    Realm.deleteRealm(realmConfig);

    getActivity(); // start Activity.
    getInstrumentation().runOnMainSync(new Runnable() {
      @Override public void run() {
        realm = Realm.getInstance(realmConfig);
        TestUtils.addTodo(realm, "Sample Todo 1", false);
        TestUtils.addTodo(realm, "Sample Todo 2", true);

        mockView = new MockTodoListView(getActivity());
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

  @UiThreadTest public void testFilterChanged() {

    presenter.onFilterChanged("done");
    assertEquals(1, mockView.list.size());

    presenter.onFilterChanged("all");
    assertEquals(2, mockView.list.size());
  }

  @UiThreadTest public void testItemCheck() {

    presenter.onItemCheck(0, true);

    Todo todo = realm.where(Todo.class).findFirst();
    assertEquals(true, todo.isDone());
  }

  @UiThreadTest public void testItemClick() {

    presenter.onItemClick(0);

    getInstrumentation().waitForIdle(new Runnable() {
      @Override public void run() {
        Todo todo = realm.where(Todo.class).findFirst();
        assertEquals(TodoEditPath.class, Flow.get(getActivity()).getHistory().top().getClass());
      }
    });
  }
}