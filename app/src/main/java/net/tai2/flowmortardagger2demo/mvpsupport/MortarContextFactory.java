package net.tai2.flowmortardagger2demo.mvpsupport;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import flow.path.Path;
import flow.path.PathContextFactory;
import mortar.MortarScope;

public final class MortarContextFactory implements PathContextFactory {

    private final PathScoper pathScoper = new PathScoper();

    public MortarContextFactory() {
    }

    @Override
    public Context setUpContext(Path path, Context parentContext) {
        MortarScope pathScope =
                pathScoper.getPathScope(parentContext, path.getClass().getName(), path);
        return new TearDownContext(parentContext, pathScope);
    }

    @Override
    public void tearDownContext(Context context) {
        TearDownContext.destroyScope(context);
    }

    static class TearDownContext extends ContextWrapper {
        private static final String SERVICE = "SNEAKY_MORTAR_PARENT_HOOK";
        private final MortarScope parentScope;
        private LayoutInflater inflater;

        static void destroyScope(Context context) {
            MortarScope.getScope(context).destroy();
        }

        public TearDownContext(Context context, MortarScope scope) {
            super(scope.createContext(context));
            this.parentScope = MortarScope.getScope(context);
        }

        @Override
        public Object getSystemService(String name) {
            if (LAYOUT_INFLATER_SERVICE.equals(name)) {
                if (inflater == null) {
                    inflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
                }
                return inflater;
            }

            if (SERVICE.equals(name)) {
                return parentScope;
            }

            return super.getSystemService(name);
        }
    }
}
