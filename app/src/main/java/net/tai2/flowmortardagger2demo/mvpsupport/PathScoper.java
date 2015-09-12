package net.tai2.flowmortardagger2demo.mvpsupport;

import android.content.Context;
import flow.path.Path;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mortar.MortarScope;
import net.tai2.flowmortardagger2demo.MyApplication;

import static java.lang.String.format;

public class PathScoper {

  static class ComponentEntry {
    Class<?> componentClass;
    List<Constructor> moduleConstructors;
  }

  private final Map<Class, ComponentEntry> componentCache = new LinkedHashMap<>();

  public MortarScope getPathScope(Context context, String name, Path path) {
    MortarScope parentScope = MortarScope.getScope(context);
    MortarScope childScope = parentScope.findChild(name);
    if (childScope == null) {
      childScope = parentScope.buildChild()
          .withService(DaggerService.SERVICE_NAME, createComponent(context, path))
          .build(name);
    }

    return childScope;
  }

  private Object createComponent(Context context, Path path) {
    ComponentEntry componentEntry = getComponentEntry(path);
    if (componentEntry == null) {
      throw new UnsupportedOperationException();
    }

    List<Object> args = new ArrayList<>();

    for (Constructor constructor : componentEntry.moduleConstructors) {
      Class[] parameters = constructor.getParameterTypes();

      Class pathParameter;
      if (parameters.length == 1) {
        pathParameter = parameters[0];
        if (!pathParameter.isInstance(path)) {
          throw new IllegalArgumentException(format(
              "Module %s for screen %s should have a constructor parameter that is a super class of %s",
              constructor.getDeclaringClass().getName(), path, path.getClass().getName()));
        }
      } else {
        pathParameter = null;
      }

      try {
        if (pathParameter == null) {
          args.add(constructor.newInstance());
        } else {
          args.add(constructor.newInstance(path));
        }
      } catch (Exception e) {
        throw new RuntimeException(format("Failed to instantiate module %s for screen %s",
            constructor.getDeclaringClass().getName(), path), e);
      }
    }

    args.add(DaggerService.<MyApplication.Component>getDaggerComponent(context));

    return DaggerService.createComponent(componentEntry.componentClass, args.toArray());
  }

  private ComponentEntry getComponentEntry(Path path) {
    Class<?> pathType = path.getClass();
    ComponentEntry componentEntry = componentCache.get(pathType);
    if (componentEntry == null) {
      WithComponent withComponentAnnotation = pathType.getAnnotation(WithComponent.class);
      if (withComponentAnnotation != null) {
        Class<?> componentClass = withComponentAnnotation.value();
        List<Constructor> moduleConstructors = new ArrayList<>();

        dagger.Component componentAnnotation = componentClass.getAnnotation(dagger.Component.class);
        for (Class<?> moduleClass : componentAnnotation.modules()) {
          Constructor<?>[] constructors = moduleClass.getDeclaredConstructors();
          if (constructors.length != 1) {
            throw new IllegalArgumentException(
                format("Module %s for screen %s should have exactly one public constructor",
                    moduleClass.getName(), path));
          }

          Constructor constructor = constructors[0];
          Class[] parameters = constructor.getParameterTypes();
          if (parameters.length > 1) {
            throw new IllegalArgumentException(
                format("Module %s for screen %s should have 0 or 1 parameter",
                    moduleClass.getName(), path));
          }

          moduleConstructors.add(constructor);
        }

        componentEntry = new ComponentEntry();
        componentEntry.componentClass = componentClass;
        componentEntry.moduleConstructors = moduleConstructors;
        componentCache.put(pathType, componentEntry);
      }
    }
    return componentEntry;
  }
}
