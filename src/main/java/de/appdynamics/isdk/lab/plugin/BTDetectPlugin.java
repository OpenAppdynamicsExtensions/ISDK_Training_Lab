package de.appdynamics.isdk.lab.plugin;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AEntry;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan.marx on 01.07.17.
 */
public class BTDetectPlugin extends AEntry {

    private final IReflector method_getBTName;

    public BTDetectPlugin () {
        method_getBTName = getNewReflectionBuilder().invokeInstanceMethod("getName",true).build();

    }
    @Override
    public String unmarshalTransactionContext(Object invokedObject, String className, String methodName, Object[] paramValues, ISDKUserContext context) throws ReflectorException {
        return null;
    }

    @Override
    public String getBusinessTransactionName(Object invokedObject, String className, String methodName, Object[] paramValues, ISDKUserContext context) throws ReflectorException {
        String name = method_getBTName.execute(invokedObject.getClass().getClassLoader(),invokedObject);

        return "CJob."+name;
    }

    @Override
    public boolean isCorrelationEnabled() {
        return false;
    }

    @Override
    public boolean isCorrelationEnabledForOnMethodBegin() {
        return false;
    }

    @Override
    public List<Rule> initializeRules() {

        List<Rule> rules = new ArrayList<Rule>();

        Rule r = new Rule.Builder("Job")
                .classStringMatchType(SDKStringMatchType.CONTAINS)
                .classMatchType(SDKClassMatchType.INHERITS_FROM_CLASS)
                .methodMatchString("callJob")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build();
        rules.add(r) ;

        // AsyncSimple
        r = new Rule.Builder("AsyncSimple")
                .classStringMatchType(SDKStringMatchType.CONTAINS)
                .classMatchType(SDKClassMatchType.INHERITS_FROM_CLASS)
                .methodMatchString("callJob")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build();
        rules.add(r) ;

        return rules;
    }
}
