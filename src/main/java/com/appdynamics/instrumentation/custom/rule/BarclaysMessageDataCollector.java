package com.appdynamics.instrumentation.custom.rule;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.contexts.ISDKDataContext;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.ADataCollector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BarclaysMessageDataCollector  extends ADataCollector {

    private static String staticClassName = "com.barcap.tcw.appd.dc.TCWCustomDataCollector";
    private static String staticMethodName = "getBroadwayDetails";
    private final IReflector staticBTDataReflector;

    public BarclaysMessageDataCollector() {
        boolean searchSuperClass = true;

        // TODO: add the right types
        // Method Arg Types
        String[] types = new String[]{Object.class.getCanonicalName(),
                String.class.getCanonicalName(),
                String.class.getCanonicalName()};
        this.staticBTDataReflector = getNewReflectionBuilder().invokeStaticMethod(this.staticMethodName, searchSuperClass, types).build();

    }
    @Override
    public void storeData(Object invokedObject, String className,
                          String methodName, Object[] paramValues, Throwable throwable, Object returnValue,
                          ISDKDataContext isdkDataContext) throws ReflectorException {

        Map<String,String> data = null;

        try {
            //BT Naming Requirements
            //com.barcap.tcw.appd.bt.TCWBusinessTransactionDiscovery.getBusinessTransactionName(Object, String, String, Object[])

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Deriving BT name Using Static Method " + staticClassName + "." + staticMethodName+"()");
            }

            Class klass = Class.forName(staticClassName, true, invokedObject.getClass().getClassLoader());
            Object[] params = {invokedObject, className, methodName, paramValues};
            data = staticBTDataReflector.execute(klass.getClassLoader(), klass, params);

            if (getLogger().isDebugEnabled()) {
               //TODO MEaningfull Debug
            }

            // Iterate retrieved data and store in data context
            if (data != null) {
                for (Map.Entry<String,String> e : data.entrySet()) {
                    isdkDataContext.storeData(e.getKey(),e.getValue());
                }
            }
        } catch (Exception e) {
            getLogger().error("Error in retrieving Data :", e);
        }
    }

    @Override
    public boolean addToSnapshot() {
        return true;
    }

    @Override
    public boolean addToAnalytics() {
        return false;
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();

        String className = "com.barcap.tcw.txm.ecnendpoint.enricher.BroadwayEnricherImpl";
        String methodName = "enrich";

        rules.add(new Rule.Builder(className).methodMatchString(methodName).build());
        return rules;
    }
}
