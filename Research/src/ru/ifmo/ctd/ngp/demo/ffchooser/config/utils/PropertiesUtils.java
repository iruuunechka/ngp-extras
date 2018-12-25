package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates the arrays of possible properties keys for different
 * {@link ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration}s.
 *
 * @author Maxim Buzdalov
 */
public final class PropertiesUtils {
    private PropertiesUtils() {}

    /**
     * Returns an array of property keys for the given configuration class.
     * @param clazz the class.
     * @return the array of property keys.
     */
    public static String[] getKeysFor(Class<? extends Configuration> clazz) {
        List<String> rv = new ArrayList<>();
        for (Method m : clazz.getMethods()) {
        	String key = getKey(m);
            if (key != null) {
            	rv.add(key);
            }
        }
        return rv.toArray(new String[0]);
    }
    
    /**
     * Gets key associated with the specified method.
     * The getter should start with "get", have no arguments
     * and be annotated with {@link PropertyMapped}.
     * Otherwise, <code>null</code> is returned.
     * @param method the specified method
     * @return 	parameter key associated with <code>method</code> if the method is an annotated getter, 
     * 			otherwise returns <code>null</code>
     */
    public static String getKey(Method method) {
    	String name = method.getName();
        if (name.startsWith("get") && method.getParameterTypes().length == 0) {
            PropertyMapped annotationValue = method.getAnnotation(PropertyMapped.class);
            if (annotationValue != null) {
                String value = annotationValue.value();
                if (value.isEmpty()) {
                    return name.substring(3).toLowerCase();
                } else {
                    return value;
                }
            } 
        } 
        return null;
    }
}
