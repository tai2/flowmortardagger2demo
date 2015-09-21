package net.tai2.flowmortardagger2demo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import dagger.Provides;
import io.realm.RealmConfiguration;
import javax.inject.Singleton;
import mortar.MortarScope;
import net.tai2.flowmortardagger2demo.mvpsupport.DaggerService;

public class MyApplication extends Application {

  @dagger.Module public static class Module {

    private final Context context;

    Module(Context context) {
      this.context = context;
    }

    @Provides @Singleton SharedPreferences providePreferences() {
      return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides @Singleton RealmConfiguration provideRealmConfiguration() {
      return new RealmConfiguration.Builder(context).build();
    }
  }

  @Singleton @dagger.Component(modules = Module.class) public interface Component {
    SharedPreferences provideSharedPreferences();

    RealmConfiguration provideRealmConfiguration();
  }

  private MortarScope rootScope;

  @Override public Object getSystemService(String name) {
    if (rootScope == null) {
      Component component = DaggerService.createComponent(Component.class, new Module(this));
      rootScope = MortarScope.buildRootScope()
          .withService(DaggerService.SERVICE_NAME, component)
          .build("Root");
    }
    return rootScope.hasService(name) ? rootScope.getService(name) : super.getSystemService(name);
  }

  public void setRootScope(MortarScope scope) {
    if (rootScope != null) {
      rootScope.destroy();
    }
    rootScope = scope;
  }
}
