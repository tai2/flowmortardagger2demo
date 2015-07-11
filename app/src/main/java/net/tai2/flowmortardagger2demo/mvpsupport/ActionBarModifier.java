package net.tai2.flowmortardagger2demo.mvpsupport;

import android.view.Menu;

public interface ActionBarModifier {
  boolean onPrepareOptionsMenu(Menu menu);

  String getTitle();
}
