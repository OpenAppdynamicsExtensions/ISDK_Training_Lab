package de.appdynamics.isdk.lab.plugin;

import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.template.AEntry;
import com.appdynamics.instrumentation.sdk.template.AGenericInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stefan.marx on 03.07.17.
 */
public class DebugPluginInterceptor extends AGenericInterceptor {

    AtomicInteger exec = new AtomicInteger(0);

    @Override
    public Object onMethodBegin(Object invokedObject, String className, String methodName, Object[] paramValues) {
        int i = exec.incrementAndGet();
        if (i %10 == 0) return System.currentTimeMillis();
        return null;
    }

    @Override
    public void onMethodEnd(Object state, Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue) {
        if (state != null) {
            long start = ((Long)state).longValue();

            getLogger().info("Method called "+methodName+ " on class "
                    +className+"  value :" + paramValues[0]);
        }
    }

    @Override
    public List<Rule> initializeRules() {
        List<Rule> rules = new ArrayList<Rule>();

        rules.add(new Rule.Builder("de.appdynamics.ace.isdk.lab.application.main.jobs.util.BusinessCalltree")
                    .methodMatchString("calculateBusinessValue")
                    .build());
        return rules;
    }
}
