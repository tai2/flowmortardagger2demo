package net.tai2.flowmortardagger2demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Date;
import javax.inject.Inject;
import net.tai2.flowmortardagger2demo.R;
import net.tai2.flowmortardagger2demo.mvpsupport.ActionBarModifier;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;
import net.tai2.flowmortardagger2demo.presenter.TodoEditPath;

public class TodoEditView extends RelativeLayout implements ActionBarModifier {

  @Inject TodoEditPath.Presenter presenter;
  @Bind(R.id.content) EditText content;
  @Bind(R.id.added_date) TextView addedDate;

  public TodoEditView(Context context, AttributeSet attrs) {
    super(context, attrs);
    DaggerService.<TodoEditPath.Component>getDaggerComponent(context).inject(this);
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
    menu.findItem(R.id.action_add).setVisible(false);
    return true;
  }

  @Override public String getTitle() {
    return getContext().getString(R.string.title_todo_edit);
  }

  @OnClick(R.id.button_update) void onEditClick() {
    presenter.onEditClick();
  }

  @OnClick(R.id.button_delete) void onDeleteClick() {
    presenter.onDeleteClick();
  }

  public String getContent() {
    return content.getText().toString();
  }

  public void setContent(String text) {
    content.setText(text);
  }

  public void setAddedDate(Date date) {
    addedDate.setText(date.toString());
  }

  public void hideKeyboard() {
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getWindowToken(), 0);
  }
}
