package net.tai2.flowmortardagger2demo.mvpsupport;

import android.content.Context;
import mortar.Presenter;
import mortar.bundler.BundleService;

public class ViewPresenter<V extends ContextHolder> extends Presenter<V> {
  @Override protected final BundleService extractBundleService(V view) {
    return BundleService.getBundleService(view.getContext());
  }

  public final Context getContext() {
    return getView().getContext();
  }
}