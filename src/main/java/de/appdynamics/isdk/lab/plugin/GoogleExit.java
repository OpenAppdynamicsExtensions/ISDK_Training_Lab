package de.appdynamics.isdk.lab.plugin;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AExit;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan.marx on 01.07.17.
 */
public class GoogleExit extends AExit {
    @Override
    public void marshalTransactionContext(String transactionContext, Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue, ISDKUserContext context) throws ReflectorException {
        // NOOP, external google call
    }

    @Override
    public Map<String, String> identifyBackend(Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue, ISDKUserContext context) throws ReflectorException {
        Map<String, String> idMap = new HashMap<String, String>();
        idMap.put("URL",""+paramValues[0]);
        getLogger().info("URL_Identified :"+paramValues[0]);
        return idMap;
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

        Rule r = new Rule.Builder("de.appdynamics.ace.isdk.lab.application.main.jobs.util.GoogleSearcher")
                .classStringMatchType(SDKStringMatchType.EQUALS)
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("searchGoogle")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build();
        rules.add(r) ;

        return rules;
    }

    @Override
    public boolean resolveToNode() {
        return false;
    }
}
