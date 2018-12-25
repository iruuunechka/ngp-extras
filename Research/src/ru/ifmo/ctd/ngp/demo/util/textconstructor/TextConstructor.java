package ru.ifmo.ctd.ngp.demo.util.textconstructor;

import org.uncommons.maths.random.*;
import ru.ifmo.ctd.ngp.util.Static;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author Maxim Buzdalov
 */
public final class TextConstructor {
    private TextConstructor() {
        Static.doNotCreateInstancesOf(TextConstructor.class);
    }
    
    public static <T> T constructFromString(Class<T> clazz, String value) {
        value = value.trim();
        Parser<T> registeredParser = getRegisteredParser(clazz);
        if (registeredParser != null) {
            return registeredParser.parse(value);
        }
        if (clazz.isEnum()) {
            for (T constant : clazz.getEnumConstants()) {
                Enum<?> e = (Enum<?>) (constant);
                if (e.name().equals(value)) {
                    return constant;
                }
            }
            throw new IllegalArgumentException("The requested class " + clazz.getCanonicalName() +
                    " is an enum, and the string value \"" + value + "\" is not a constant of this enum");
        }

        //ClassName(name1 = value1, name2 = value2, ... nameK = valueK)
        int firstPH = value.indexOf('(');
        int lastPH = value.lastIndexOf(')');
        if (firstPH == -1) {
            throw new IllegalArgumentException(
                    "General constructor form does not conform the format: no opening parenthesis");
        }
        if (lastPH == -1) {
            throw new IllegalArgumentException(
                    "General constructor form does not conform the format: no closing parenthesis");
        }
        if (lastPH != value.length() - 1) {
            throw new IllegalArgumentException(
                    "General constructor form does not conform the format: last character is not a closing parenthesis");
        }
        String className = value.substring(0, firstPH);
        String paramStrings = value.substring(firstPH + 1, lastPH);
        Map<String, String> parameters = parseNameValuePairs(paramStrings);

        try {
            Class<?> theClass = Class.forName(className);
            if (!clazz.isAssignableFrom(theClass)) {
                throw new IllegalArgumentException("Class \"" + className +
                        "\" is not a subclass of required \"" + clazz.getCanonicalName() + "\"");
            }
            Constructor<?>[] constructors = theClass.getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                String[] parameterNames = new String[parameterAnnotations.length];
                String[] defaultValues = new String[parameterAnnotations.length];
                boolean everyParameterHasParamDef = true;
                for (int i = 0; i < parameterAnnotations.length; ++i) {
                    for (Annotation a : parameterAnnotations[i]) {
                        if (a instanceof ParamDef) {
                            ParamDef p = (ParamDef) (a);
                            parameterNames[i] = p.name();
                            defaultValues[i] = p.value();
                        }
                    }
                    everyParameterHasParamDef &= parameterNames[i] != null;
                }
                if (everyParameterHasParamDef) {
                    Set<String> allValues = new HashSet<>(Arrays.asList(parameterNames));
                    Set<String> requiredValues = new HashSet<>();
                    for (int i = 0; i < parameterNames.length; ++i) {
                        if (defaultValues[i].length() == 0) {
                            requiredValues.add(parameterNames[i]);
                        }
                    }
                    Set<String> definedNames = parameters.keySet();

                    if (definedNames.containsAll(requiredValues) && allValues.containsAll(definedNames)) {
                        //A perfectly matching constructor.
                        Object[] actualParams = new Object[parameterNames.length];
                        for (int i = 0; i < actualParams.length; ++i) {
                            String ps;
                            if (parameters.containsKey(parameterNames[i])) {
                                ps = parameters.get(parameterNames[i]);
                            } else {
                                ps = defaultValues[i];
                            }
                            actualParams[i] = constructFromString(parameterTypes[i], ps);
                        }
                        try {
                            //noinspection unchecked
                            return (T) constructor.newInstance(actualParams);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Constructor invocation failed", e);
                        }
                    }
                }
            }

            throw new IllegalArgumentException("No suitable constructor exists");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No such class: \"" + className + "\"");
        }
    }

    private static Map<String, String> parseNameValuePairs(String nameValuePairs) {
        Map<String, String> result = new HashMap<>();

        nameValuePairs = nameValuePairs.trim();

        int cp = 0;
        while (cp < nameValuePairs.length()) {
            int nextEq = nameValuePairs.indexOf('=', cp);
            if (nextEq == -1) {
                throw new IllegalArgumentException("Expected = in the parameter list definition");
            }
            String name = nameValuePairs.substring(cp, nextEq).trim();
            cp = nextEq + 1;
            int ph = 0;
            while (cp < nameValuePairs.length() && (nameValuePairs.charAt(cp) != ',' || ph > 0)) {
                if (nameValuePairs.charAt(cp) == '(') {
                    ++ph;
                } else if (nameValuePairs.charAt(cp) == ')') {
                    --ph;
                    if (ph < 0) {
                        throw new IllegalArgumentException("Unbalanced parenthesis (extra closing one)");
                    }
                }
                ++cp;
            }
            if (ph > 0) {
                throw new IllegalArgumentException("Unbalanced parenthesis (missing " + ph + " closing ones)");
            }
            String value = nameValuePairs.substring(nextEq + 1, cp).trim();
            if (result.containsKey(name)) {
                throw new IllegalArgumentException("Name " + name + " is defined (at least) twice");
            }
            ++cp;
            result.put(name, value);
        }
        
        return result;
    }
    
    private static final Map<Class<?>, Parser<?>> registeredParsers = new HashMap<>();

    static {
        Parser<Boolean> booleanParser = value -> {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
                return true;
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
                return false;
            } else {
                throw new IllegalArgumentException("Expected 'true', 'false', 'yes', or 'no', found '" + value + "'");
            }
        };
        Parser<String> stringParser = value -> value;
        Parser<Byte> byteParser = Byte::valueOf;
        Parser<Short> shortParser = Short::valueOf;
        Parser<Integer> intParser = Integer::valueOf;
        Parser<Long> longParser = Long::valueOf;
        Parser<Float> floatParser = Float::valueOf;
        Parser<Double> doubleParser = Double::valueOf;
        Parser<Character> charParser = value -> {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Source string for Character should consist of one character");
            }
            return value.charAt(0);
        };
        Parser<Probability> probabilityParser = value -> new Probability(Double.parseDouble(value));

        registerParser(Boolean.class, booleanParser);
        registerParser(Boolean.TYPE, booleanParser);
        registerParser(String.class, stringParser);
        registerParser(Byte.class, byteParser);
        registerParser(Byte.TYPE, byteParser);
        registerParser(Short.class, shortParser);
        registerParser(Short.TYPE, shortParser);
        registerParser(Integer.class, intParser);
        registerParser(Integer.TYPE, intParser);
        registerParser(Long.class, longParser);
        registerParser(Long.TYPE, longParser);
        registerParser(Float.class, floatParser);
        registerParser(Float.TYPE, floatParser);
        registerParser(Double.class, doubleParser);
        registerParser(Double.TYPE, doubleParser);
        registerParser(Character.class, charParser);
        registerParser(Character.TYPE, charParser);
        registerParser(Probability.class, probabilityParser);
    }

    public static <T> void registerParser(Class<T> clazz, Parser<T> parser) {
        registeredParsers.put(clazz, parser);
    }

    @SuppressWarnings("unchecked")
    private static <T> Parser<T> getRegisteredParser(Class<T> clazz) {
        return (Parser<T>) registeredParsers.get(clazz);
    }

    public interface Parser<T> {
        T parse(String value);
    }
}
