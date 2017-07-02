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
 * Created by stefan.marx on 02.07.17.
 */
public class TCPCorrelationEndPlugin extends AEntry {


    public TCPCorrelationEndPlugin
    private final IReflector _getHeader;

    {
        _getHeader = getNewReflectionBuilder().invokeInstanceMethod("getCorrelation",false).build();

    }


    @Override
    public String unmarshalTransactionContext(Object invokedObject, String className, String methodName, Object[] paramValues, ISDKUserContext context) throws ReflectorException {

        getLogger().info("Start Correlation : ");

        return null;
    }

    @Override
    public String getBusinessTransactionName(Object invokedObject, String className, String methodName, Object[] paramValues, ISDKUserContext context) throws ReflectorException {
        return "TCP-SERVER";
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
        List<Rule> rules  = new ArrayList<Rule>();
        rules.add(new Rule.Builder("de.appdynamics.ace.isdk.lab.application.tcpBackend.server.ComandExecutor")
                            .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                            .classStringMatchType(SDKStringMatchType.EQUALS)
                            .methodMatchString("executeCommand")
                            .methodStringMatchType(SDKStringMatchType.EQUALS)
                            .build());
        return rules;
    }
}
