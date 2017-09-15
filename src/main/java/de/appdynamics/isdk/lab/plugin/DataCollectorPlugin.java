package de.appdynamics.isdk.lab.plugin;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.contexts.ISDKDataContext;
import com.appdynamics.instrumentation.sdk.template.ADataCollector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.ReflectorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 04/08/17.
 */
public class DataCollectorPlugin extends ADataCollector {

    private IReflector reflector;

    public DataCollectorPlugin() {
        reflector = getNewReflectionBuilder().invokeInstanceMethod("getAntrag", true).invokeInstanceMethod("getHaendlernummer", true).build();
    }

    @Override
    public void storeData(Object invokedObject, String className, String methodName, Object[] paramValues,
                          Throwable thrownException, Object returnValue, ISDKDataContext isdkDataContext) throws ReflectorException {
        getLogger().info("Logging agent plugin data collection for Haendlernummer");

        Object payload =  paramValues[0];

        String toStore = null;
        try {
            int haendlernummer = (Integer) reflector.execute(payload.getClass().getClassLoader(), payload);
            toStore = String.valueOf(haendlernummer);
        } catch (ReflectorException e) {
            getLogger().error("Caught exception in attempting to pull payload data", e);
        }

        isdkDataContext.storeData("haendlernummerPlugin", toStore);
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
        List<Rule> rules  = new ArrayList<Rule>();
        rules.add(new Rule.Builder("de.bank11.victor.services.antrag.AntragImpl")
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .classStringMatchType(SDKStringMatchType.EQUALS)
                .methodMatchString("submitAntrag")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build());
        return rules;
    }
}
