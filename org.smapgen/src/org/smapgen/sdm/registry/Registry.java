/**
 *
 */
package org.smapgen.sdm.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class Registry {
    private static Registry registry;

    /**
     * Registered Function List.
     */
    private List<StringBuffer> functionList;

    /**
     * Functions Registry.
     */
    private HashMap<String, HashMap<String, String>> functionsRegistry;

    /**
     * Names Registry.
     */
    private HashMap<String, Integer> namesRegistry;

    /**
     * @param fn
     */
    public static void addFunctionToList(final StringBuffer fn) {
        Registry.getInstance().functionList.add(fn);
    }

    /**
     * @param canonicalName
     * @return
     */
    public static boolean containsFuncReturn(final String canonicalName) {
        return Registry.getInstance().functionsRegistry.containsKey(canonicalName);
    }

    public static synchronized void dispose() {
        Registry.registry.functionList = null;
        Registry.registry.namesRegistry = null;
        Registry.registry.functionsRegistry = null;
        Registry.registry = null;
    }

    /**
     * @param canonicalName
     * @return
     */
    public static HashMap<String, String> getFunctionsRegistry(final String canonicalName) {
        return Registry.getInstance().functionsRegistry.get(canonicalName);
    }

    /**
     * @return
     */
    public static synchronized Registry getInstance() {
        if (Registry.registry == null || Registry.registry.functionList == null || Registry.registry.namesRegistry == null || Registry.registry.functionsRegistry == null) {
            Registry.registry = new Registry();
            Registry.registry.functionList = new ArrayList<>();
            Registry.registry.namesRegistry = new HashMap<>();
            Registry.registry.functionsRegistry = new HashMap<>();
        }

        return Registry.registry;
    }
    /**
     * @param baseName
     * @return
     */
    public static String registerName(final String baseName) {
        if (!Registry.getInstance().namesRegistry.containsKey(baseName)) {
            Registry.getInstance().namesRegistry.put(baseName, Integer.valueOf(0));
            return baseName;
        }
        Integer count = Registry.getInstance().namesRegistry.get(baseName);
        ++count;
        Registry.getInstance().namesRegistry.put(baseName, count);
        return baseName + count;
    }
    /**
     * @param source
     * @param target
     * @param fName
     */
    public static void registreFunction(final String source, final String target, final String fName) {
        if (!Registry.getInstance().functionsRegistry.containsKey(target)) {
            final HashMap<String, String> funtion = new HashMap<>();
            funtion.put(source, fName);
            Registry.getInstance().functionsRegistry.put(target, funtion);
        } else {
            final HashMap<String, String> funtion = Registry.getInstance().functionsRegistry.get(target);
            if (!funtion.containsKey(source)) {
                funtion.put(source, fName);
                Registry.getInstance().functionsRegistry.put(target, funtion);
            }
        }
    }

    /**
     *
     */
    private Registry() {
        super();
    }

    /**
     * @return the functionList
     */
    public List<StringBuffer> getFunctionList() {
        return functionList;
    }

    /**
     * @return the functionsRegistry
     */
    public HashMap<String, HashMap<String, String>> getFunctionsRegistry() {
        return functionsRegistry;
    }

    /**
     * @return the namesRegistry
     */
    public HashMap<String, Integer> getNamesRegistry() {
        return namesRegistry;
    }

    /**
     * @param functionList
     *            the functionList to set
     */
    public void setFunctionList(final List<StringBuffer> functionList) {
        this.functionList = functionList;
    }

    /**
     * @param functionsRegistry
     *            the functionsRegistry to set
     */
    public void setFunctionsRegistry(final HashMap<String, HashMap<String, String>> functionsRegistry) {
        Registry.getInstance().functionsRegistry = functionsRegistry;
    }

    /**
     * @param namesRegistry
     *            the namesRegistry to set
     */
    public void setNamesRegistry(final HashMap<String, Integer> namesRegistry) {
        this.namesRegistry = namesRegistry;
    }
}
