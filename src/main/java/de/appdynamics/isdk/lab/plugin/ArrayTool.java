package de.appdynamics.isdk.lab.plugin;

/**
 * Created by stefan.marx on 02.07.17.
 */
public class ArrayTool {
    public static String logValues(Object[] paramValues) {
        StringBuffer result = new StringBuffer();
        int i = 0;
        for (Object o : paramValues) {
            result.append(i).append(": ").append(""+o)
                    .append("\n");
            i++;
        }
        return result.toString();
    }
}
