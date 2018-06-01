package com.appdynamics.instrumentation.custom.rule;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AEntry;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

public class BarclaysMessageListenerIntrceptor extends AEntry {

    private IReflector headerReflector;
    private IReflector staticBTNameMethodRefelctor;

    private String staticClassName = "com.barcap.tcw.appd.bt.TCWBusinessTransactionDiscovery";
    private String staticMethodName = "getBusinessTransactionName";

//    private String staticClassName = "com.barcap.tcw.appd.data.TCWBusinessDataCollector";
//    private String staticMethodName = "collectBTData"; // return Map<String,String>

    public BarclaysMessageListenerIntrceptor() {
        boolean searchSuperClass = true;
        this.headerReflector = getNewReflectionBuilder().invokeInstanceMethod("getStringProperty", searchSuperClass, new String[]{String.class.getCanonicalName()}).build();
        Object[] objArray = new Object[]{};
        String[] types = new String[]{Object.class.getCanonicalName(),
                String.class.getCanonicalName(),
                String.class.getCanonicalName(),
                objArray.getClass().getName()};

        this.staticBTNameMethodRefelctor = getNewReflectionBuilder().invokeStaticMethod(this.staticMethodName, searchSuperClass, types).build();
    }

    @Override
    public String unmarshalTransactionContext(Object invokedObject, String className, String methodName,
                                              Object[] paramValues, ISDKUserContext context) throws ReflectorException {

        Object payload = paramValues[0];
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Got payload to unmarshall: " + payload);
        }

        try {
            String contextString = headerReflector.execute(payload.getClass().getClassLoader(), payload, new Object[]{"singularityheader"});
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Got context: " + contextString);
            }
            return contextString;
        } catch (ReflectorException e) {
            getLogger().error("Caught reflector exception", e);
            return null;
        }
    }


    @Override
    public String getBusinessTransactionName(Object invokedObject, String className,
                                             String methodName, Object[] paramValues, ISDKUserContext context) throws ReflectorException {

        String btName = null;
        try {
            //BT Naming Requirements
            //com.barcap.tcw.appd.bt.TCWBusinessTransactionDiscovery.getBusinessTransactionName(Object, String, String, Object[])

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Deriving BT name Using Static Method " + staticClassName + "." + staticMethodName+"()");
            }

            Class klass = Class.forName(staticClassName, true, invokedObject.getClass().getClassLoader());
            Object[] params = {invokedObject, className, methodName, paramValues};
            btName = staticBTNameMethodRefelctor.execute(klass.getClassLoader(), klass, params);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Derived BT name " + btName);
            }
        } catch (Exception e) {
            getLogger().error("Error in evaluating BT name:", e);
        }

        return btName;
    }


    @Override
    public boolean isCorrelationEnabled() {
        return true;
    }

    @Override
    public boolean isCorrelationEnabledForOnMethodBegin() {
        return true;
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();

        String className = "org.springframework.integration.jms.ChannelPublishingJmsMessageListener";
        String methodName = "onMessage";

        rules.add(new Rule.Builder(className).methodMatchString(methodName).build());
        return rules;
    }
}

