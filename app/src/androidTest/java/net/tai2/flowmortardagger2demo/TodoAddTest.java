package net.tai2.flowmortardagger2demo;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import javax.inject.Inject;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;
import net.tai2.flowmortardagger2demo.mvpsupport.PerScreen;
import net.tai2.flowmortardagger2demo.presenter.TodoAddPath;
import net.tai2.flowmortardagger2demo.presenter.TodoListPath;

public class TodoAddTest extends ActivityInstrumentationTestCase2<MainActivity> {

  @dagger.Component(dependencies = MyApplication.Component.class) @PerScreen interface Component {
    void inject(TodoAddTest test);
  }

  class MockTodoAddView implements TodoAddPath.View {

    Context context;
    String content;

    public MockTodoAddView(Context context) {
      this.context = context;
    }

    @Override public String getContent() {
      return content;
    }

    @Override public void hideKeyboard() {
    }

    @Override public Context getContext() {
      return context;
    }
  }

  Component component;
  MockTodoAddView mockView;
  Realm realm;
  @Inject RealmConfiguration realmConfig;
  @Inject TodoAddPath.Presenter presenter;

  public TodoAddTest() {
    super(MainActivity.class);
  }

  public void setUp() throws Exception {
    super.setUp();

    MyApplication app =
        (MyApplication) getInstrumentation().getTargetContext().getApplicationContext();
    app.setRootScope(TestUtils.createTestRootScope(app));
    MyApplication.Component appComponent = DaggerService.getDaggerComponent(app);
    component = DaggerTodoAddTest_Component.builder().component(appComponent).build();
    component.inject(this);

    Realm.deleteRealm(realmConfig);

    getActivity(); // start Activity.
    getInstrumentation().runOnMainSync(new Runnable() {
      @Override public void run() {
        realm = Realm.getInstance(realmConfig);

        mockView = new MockTodoAddView(getActivity());
        mockView.content = "Sample Todo";
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

  @UiThreadTest public void testAddClick() {

    presenter.onAddClick();

    getInstrumentation().waitForIdle(new Runnable() {
      @Override public void run() {
        Todo todo = realm.where(Todo.class).findFirst();
        assertNotNull(todo);
        assertEquals(mockView.getContent(), todo.getContent());
        assertEquals(TodoListPath.class, Flow.get(getActivity()).getHistory().top().getClass());
      }
    });
  }
}