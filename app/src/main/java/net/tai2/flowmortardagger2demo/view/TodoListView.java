package net.tai2.flowmortardagger2demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import javax.inject.Inject;
import net.tai2.flowmortardagger2demo.R;
import net.tai2.flowmortardagger2demo.model.Todo;
import net.tai2.flowmortardagger2demo.mvpsupport.ActionBarModifier;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;
import net.tai2.flowmortardagger2demo.presenter.TodoListPath;

public class TodoListView extends LinearLayout implements ActionBarModifier, TodoListPath.View {

  @Inject TodoListPath.Presenter presenter;
  @Bind(R.id.list_view) ListView listView;
  @Bind(R.id.filter_group) RadioGroup filterGroup;

  public TodoListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    DaggerService.<TodoListPath.Component>getDaggerComponent(context).inject(this);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    ButterKnife.bind(this);
    presenter.takeView(this);
  }

  @Override protected void onDetachedFromWindow() {
    presenter.dropView(this);
    super.onDetachedFromWindow();
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    menu.findItem(R.id.action_add).setVisible(true);
    return true;
  }

  @Override public String getTitle() {
    return getContext().getString(R.string.title_todo_list);
  }

  @Override public void showList(RealmResults<Todo> results) {
    MyAdapter adapter = new MyAdapter(getContext(), results, true);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemClick(position);
      }
    });
  }

  @Override public void setFilter(String filter) {
    if (filter.equals("all")) {
      filterGroup.check(R.id.radio_all);
    } else if (filter.equals("done")) {
      filterGroup.check(R.id.radio_done);
    } else if (filter.equals("undone")) {
      filterGroup.check(R.id.radio_undone);
    }
  }

  @OnClick({ R.id.radio_all, R.id.radio_done, R.id.radio_undone }) void onFilterChecked() {
    switch (filterGroup.getCheckedRadioButtonId()) {
      case R.id.radio_all:
        presenter.onFilterChanged("all");
        break;
      case R.id.radio_done:
        presenter.onFilterChanged("done");
        break;
      case R.id.radio_undone:
        presenter.onFilterChanged("undone");
        break;
    }
  }

  static class ViewHolder {
    @Bind(R.id.content) TextView content;
    @Bind(R.id.check) CheckBox check;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  private class MyAdapter extends RealmBaseAdapter<Todo> implements ListAdapter {

    public MyAdapter(Context context, RealmResults<Todo> realmResults, boolean automaticUpdate) {
      super(context, realmResults, automaticUpdate);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      final ViewHolder viewHolder;
      if (convertView == null) {
        convertView = inflater.inflate(R.layout.todo_list_row, parent, false);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      final int pos = position;
      Todo item = realmResults.get(position);
      viewHolder.content.setText(item.getContent());
      viewHolder.check.setChecked(item.isDone());
      viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          presenter.onItemCheck(pos, isChecked);
        }
      });
      return convertView;
    }
  }
}
