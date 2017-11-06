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
     * 
     */
    private Registry() {
        super();
    }
    
    /**
     * @return
     */
    public static synchronized Registry getInstance (){
        if (registry == null || registry.functionList == null || registry.namesRegistry==null ||registry.functionsRegistry== null) {
            registry = new Registry();
            registry.functionList=new ArrayList<StringBuffer>();
            registry.namesRegistry=new HashMap<String, Integer>();
            registry.functionsRegistry=new HashMap<String, HashMap<String,String>>();
        }

        return registry;
    }
    public static synchronized void dispose (){
        registry.functionList=null;
        registry.namesRegistry=null;
        registry.functionsRegistry=null;
        registry=null;
    }

    /**
     * @return the functionList
     */
    public List<StringBuffer> getFunctionList() {
        return functionList;
    }
    /**
     * @param functionList the functionList to set
     */
    public void setFunctionList(List<StringBuffer> functionList) {
        this.functionList = functionList;
    }
    /**
     * @return the functionsRegistry
     */
    public HashMap<String, HashMap<String, String>> getFunctionsRegistry() {
        return functionsRegistry;
    }
    /**
     * @param functionsRegistry the functionsRegistry to set
     */
    public void setFunctionsRegistry(HashMap<String, HashMap<String, String>> functionsRegistry) {
        Registry.getInstance().functionsRegistry = functionsRegistry;
    }
    /**
     * @return the namesRegistry
     */
    public HashMap<String, Integer> getNamesRegistry() {
        return namesRegistry;
    }
    /**
     * @param namesRegistry the namesRegistry to set
     */
    public void setNamesRegistry(HashMap<String, Integer> namesRegistry) {
        this.namesRegistry = namesRegistry;
    }
    
    /**
     * @param source
     * @param target
     * @param fName
     */
    public static void registreFunction(final String source, final String target, final String fName) {
        if (!Registry.getInstance().functionsRegistry.containsKey(target)) {
            final HashMap<String, String> funtion = new HashMap<String, String>();
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
     * @param canonicalName
     * @return
     */
    public static boolean containsFuncReturn(String canonicalName) {
        return Registry.getInstance().functionsRegistry.containsKey(canonicalName);
    }

    /**
     * @param canonicalName
     * @return
     */
    public static HashMap<String, String> getFunctionsRegistry(String canonicalName) {
        return Registry.getInstance().functionsRegistry.get(canonicalName);
    }

    /**
     * @param fn
     */
    public static void addFunctionToList(StringBuffer fn) {
        Registry.getInstance().functionList.add(fn);        
    }
}
