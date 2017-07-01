package de.appdynamics.isdk.lab.plugin;

import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKDataContext;
import com.appdynamics.instrumentation.sdk.template.ADataCollector;
import com.appdynamics.instrumentation.sdk.template.AMidDataCollector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan.marx on 02.07.17.
 */
public class BTBusinessInnerValues extends AMidDataCollector {

    @Override
    public void storeData(Object invokedObject, String className, String methodName, Object[] paramValues, Object[] localVariables, int lineNum, ISDKDataContext sdkContext) throws ReflectorException {
        getLogger().info("LOGGING Custom "+ArrayTool.logValues(localVariables));
        sdkContext.storeData("customerCode",""+localVariables[0]);
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

        Rule r = new Rule.Builder("de.appdynamics.ace.isdk.lab.application.main.jobs.util.BusinessCalltree")
                .classStringMatchType(SDKStringMatchType.EQUALS)
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("calculateBusinessValue")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .collectLocalVariables(new String []{"customerCode"})
                .build();
        rules.add(r) ;


        return rules;
    }
}
