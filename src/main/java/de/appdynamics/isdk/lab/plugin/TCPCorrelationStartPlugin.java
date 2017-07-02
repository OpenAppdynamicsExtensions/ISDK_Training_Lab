package de.appdynamics.isdk.lab.plugin;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKUserContext;
import com.appdynamics.instrumentation.sdk.template.AExit;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflectionBuilder;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan.marx on 02.07.17.
 */
public class TCPCorrelationStartPlugin extends AExit {

    private final IReflector _getHost;
    private final IReflector _getPort;
    private final IReflector _addHeader;

    public TCPCorrelationStartPlugin() {
        _getHost = getNewReflectionBuilder().accessFieldValue("_hostname",false).build();
        _getPort = getNewReflectionBuilder().accessFieldValue("_port",false).build();
        _addHeader = getNewReflectionBuilder().invokeInstanceMethod("setCorrelation",false,"java.lang.String").build();

    }
    @Override
    public void marshalTransactionContext(String transactionContext, Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue, ISDKUserContext context) throws ReflectorException {
        _addHeader.execute(invokedObject.getClass().getClassLoader(),paramValues[0],new Object []{transactionContext});
    }

    @Override
    public Map<String, String> identifyBackend(Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue, ISDKUserContext context) throws ReflectorException {
        Map<String, String> idMap = new HashMap<String, String>();
        ClassLoader cl = invokedObject.getClass().getClassLoader();
        idMap.put("host",""+_getHost.execute(cl,invokedObject));
        idMap.put("port",""+_getHost.execute(cl,invokedObject));
        return idMap;
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

        Rule r = new Rule.Builder("de.appdynamics.ace.isdk.lab.application.main.jobs.TCPClientSimple")
                .classStringMatchType(SDKStringMatchType.EQUALS)
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("communicate")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build();
        rules.add(r) ;

        return rules;
    }
}
