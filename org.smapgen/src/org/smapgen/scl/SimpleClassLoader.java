package org.smapgen.scl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.smapgen.scl.exception.ClassLoaderException;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class SimpleClassLoader extends ClassLoader {
    private static final char BACKSLASH = "\\".charAt(0);
    private static final String CLASS_EXTENSION = ".class";
    private static final char DOT_CHAR = ".".charAt(0);
    private static final String JAR_EXTENSION = ".jar";
    private static final String LINE_SEPARATOR = System.getProperty("file.separator");
    private static final char SHARP_CHAR = "#".charAt(0);
    private static final char SLASH_CHAR = "/".charAt(0);
    /**
     * Class cache
     * 
     */
    private final Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    /**
     * Dependency paths .
     */
    private Map<String, URL> depsPath = new HashMap<String, URL>();
    private boolean ignoreClassNotFound;
    /**
     * String libpath .
     */
    private String libpath;
    /**
     * Boolean libpathLoaded .
     */
    private boolean libpathLoaded;
    /**
     * Paths .
     */
    private final Map<String, String> paths = new HashMap<String, String>();
    /**
     * SimpleClassLoader repoClassLoader .
     */
    private SimpleClassLoader repoClassLoader;

    public SimpleClassLoader() {}

    /**
     * 
     * @param String
     *            path.
     * @return void
     */
    public SimpleClassLoader(final String path) {
        libpath = path;
    }

    /**
     * 
     * @param String
     *            name
     * @return Class<?>
     * @throws NoClassDefFoundError
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> findClass(final String name) throws NoClassDefFoundError, ClassNotFoundException {
        if (classCache.containsKey(name)) {
            return getClassCache(name);
        }
        if (this.repoClassLoader != null && this.repoClassLoader.classCache.containsKey(name)) {
            return this.repoClassLoader.getClassCache(name);
        }
        Class<?> response = findClassFromLoadDep(name);
        if (response != null) {
            return response;
        }
        response = getClassFromRepo(name);
        if (response != null) {
            return response;
        }
        response = findClassFromPkg(name);
        if (response != null) {
            return response;
        }
        if (ignoreClassNotFound) {
            try {
                response = super.findClass(name);
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        } else {
            response = super.findClass(name);
        }
        if (response == null) {
            throw new NoClassDefFoundError(name);
        }

        return response;
    }

    public InputStream getClassAsStream(String name) {
        final URL pkgToUri = depsPath.get(name);
        try {
            return new FileInputStream(pkgToUri.getPath());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 
     * @param String
     *            name
     * @return Class<?>
     */
    public Class<?>[] getClassCacheValues() {
        return classCache.values().toArray(new Class<?>[0]);
    }

    /**
     * .
     * 
     * @return dependency paths Map
     */
    public Map<String, URL> getClassMap() {
        return depsPath;
    }

    /**
     * .
     * 
     * @return String[]
     * @throws Throwable
     */
    public String[] getLoadedClassNames() throws Throwable {
        return depsPath.keySet().toArray(new String[0]);
    }

    /**
     * @param
     * @throws Throwable
     */
    public void initDeps(Boolean ignoreClassNotFound) throws Throwable {
        setignoreClassNotFound(ignoreClassNotFound);
        try {
            ArrayList<String> deps = new ArrayList<String>(depsPath.keySet());
            loadAllDeps(deps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setignoreClassNotFound(false);
    }

    /**
     * 
     * @param String
     *            classname
     * @return Class<?>
     * @throws ClassLoaderException
     */
    public Class<?> loadClassByName(final String classname) throws ClassLoaderException {
        Class<?> response = null;
        try {
            if (!libpathLoaded) {
                loadlib(libpath);
                libpathLoaded = true;
            }
            final ArrayList<String> deps = new ArrayList<String>();
            deps.add(classname);
            loadDeps(deps);
            response = loadClass(classname.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR));
            classCache.put(classname, response);
            if (response != null) {
                return response;
            }
        } catch (final Throwable e) {
            if (response != null || repoClassLoader == null) {
                throw new ClassLoaderException(classname, e);
            }
            try {
                response = repoClassLoader
                        .loadClassByName(classname.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR));
            } catch (final Throwable ex) {
                throw new ClassLoaderException(classname);
            }
            if (response == null) {
                throw new ClassLoaderException(classname);
            }
            addClassesToLoader(response);
            classCache.putAll(repoClassLoader.classCache);
            return response;
        }

        return response;
    }

    /**
     * 
     * @param ArrayList<String>
     *            dependencias.
     * @return boolean
     * @throws Throwable
     */
    public boolean loadDeps(final ArrayList<String> dependencias) throws Throwable {
        if (dependencias == null) {
            return false;
        }
        final Set<String> eval = formatDepsCanonicalNames(dependencias);
        for (final String key : eval) {
            if (depsPath.containsKey(key) && ldepsFromDepsPath(key)) {
                continue;
            }
        }
        return true;
    }

    /**
     * @param className
     * @throws IOException
     */
    public void loadJarFromContinedClassName(String className) throws IOException {
        if (depsPath.containsKey(className)) {
            URL url = depsPath.get(className);
            if (url.toString().startsWith("jar:file:")) {
                loadJar(url.toString().substring(9, url.toString().lastIndexOf("!/")));
            }
        } else if (repoClassLoader != null && repoClassLoader.getClassMap().containsKey(className)) {
            URL url = depsPath.get(className);
            if (url.toString().startsWith("jar:file:")) {
                loadJar(url.toString().substring(9, url.toString().lastIndexOf("!/")));
            }
        }
    }

    /**
     * 
     * @param String
     *            libpath.
     * @return void
     * @throws Throwable
     */
    public void loadlib(final String libpath) throws Throwable {
        final File f = new File(libpath);
        this.libpath = libpath;
        libpathLoaded = false;
        if (f.listFiles() == null) {
            loadJar(libpath);
        } else {
            for (final File fileElement : f.listFiles()) {
                if (fileElement.isDirectory() && fileElement.listFiles() != null) {
                    loadFolder(fileElement);
                } else {
                    loadJar(fileElement.getAbsolutePath());
                }
            }
        }

    }

    /**
     * 
     * @param Map<String,URL>
     *            map.
     * @return void
     */
    public void setClassMap(final Map<String, URL> map) {
        depsPath = map;
        libpathLoaded = true;
    }

    /**
     * @param repoClassLoader
     *            the repoClassLoader to set
     */
    public void setRepoClassLoader(final SimpleClassLoader repoClassLoader) {
        this.repoClassLoader = repoClassLoader;
    }

    /**
     * 
     * @param Class<?>
     *            loadClass
     * @return void
     */
    private void addClassCache(final Class<?> loadClass) {
        addClassesToLoader(loadClass);
        classCache.put(loadClass.getName(), loadClass);
    }

    /**
     * 
     * @param Class<?>
     *            classFrom
     * @return void
     */
    private void addClassesToLoader(final Class<?> classFrom) {
        final ClassLoader appClassLoader = classFrom.getClassLoader();
        Field clasesField = getfieldFromClassSuperClass(appClassLoader.getClass(), "classes");
        final boolean acc = clasesField.isAccessible();
        clasesField.setAccessible(true);
        try {
            @SuppressWarnings("unchecked")
            final List<Class<?>> classesValue = (List<Class<?>>) clasesField.get(appClassLoader);
            classesValue.addAll(classCache.values());
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        clasesField.setAccessible(acc);
    }

    /**
     * @param className
     * @param ur
     */
    private void addDependencyPath(final String className, final URL ur) {
        if (!depsPath.containsKey(className)) {
            depsPath.put(className, ur);
        }
    }

    /**
     * @param pathToJar
     * @param je
     * @throws MalformedURLException
     */
    private void addJarEntry(final String pathToJar, final JarEntry je) throws MalformedURLException {
        if (!je.isDirectory() && je.getName().endsWith(SimpleClassLoader.CLASS_EXTENSION)) {
            final String className = je.getName().substring(0, je.getName().length() - 6);
            // className = className.replace("/", ".");
            final URL ur = new URL(
                    "jar:file:" + pathToJar + "!/" + className + SimpleClassLoader.CLASS_EXTENSION);
            addDependencyPath(className, ur);
        }
    }

    /**
     * @param name
     * @return
     */
    private Class<?> findClassFromLoadDep(final String name) {
        final ArrayList<String> deps = new ArrayList<String>();
        deps.add(name);
        try {
            loadDeps(deps);
            if (classCache.containsKey(name)) {
                return loadClass(name);
            }
        } catch (final Throwable e) {
            return null;
        }
        return null;
    }

    /**
     * @param name
     * @return
     */
    private Class<?> findClassFromPkg(final String name) {
        final String pkgToUri = name.replace(SimpleClassLoader.DOT_CHAR, SimpleClassLoader.BACKSLASH);
        Class<?> response = null;
        final Set<String> rootUri = findRootUri(pkgToUri, paths.keySet());
        if (rootUri == null) {
            final String transName = name.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR);
            if (depsPath.containsKey(transName)) {
                response = findClassFromLoadDep(name);
                if (response != null) {
                    return response;
                }
            }
        } else {
            final Map<String, Set<String>> systemRootUri = new HashMap<String, Set<String>>();
            for (final String ru : rootUri) {
                systemRootUri.put(ru, findSystemRootUri(ru, paths.keySet()));
            }
            for (final String sru : systemRootUri.keySet()) {
                final Set<String> startUriSet = systemRootUri.get(sru);
                for (final String startUri : startUriSet) {
                    final String uriPath = startUri.concat(pkgToUri.concat(SimpleClassLoader.CLASS_EXTENSION));
                    try {
                        response = getClass(name, uriPath);
                    } catch (final URISyntaxException e) {
                        return null;
                    } catch (ClassLoaderException e) {
                        return null;
                    }
                    if (response != null) {
                        return response;
                    }
                }
            }
        }
        return response;
    }

    /**
     * 
     * @param String
     *            pkgToUri.
     * @param Set
     *            keys
     * @return Set paths
     */
    private Set<String> findRootUri(String pkgToUri, final Set<String> keys) {
        if (!pkgToUri.contains(String.valueOf(SimpleClassLoader.BACKSLASH))) {
            return null;
        }
        String newPkgToUri = pkgToUri.substring(0, pkgToUri.lastIndexOf(SimpleClassLoader.BACKSLASH));
        final Set<String> response = new HashSet<String>();
        for (final String k : keys) {
            if (k.contains(newPkgToUri)) {
                response.add(newPkgToUri);
            }
        }
        if (!response.isEmpty()) {
            return response;
        }
        if (newPkgToUri.lastIndexOf(SimpleClassLoader.BACKSLASH) < 0) {
            return null;
        }
        final String auxPkgToUri = newPkgToUri.substring(0, newPkgToUri.lastIndexOf(SimpleClassLoader.BACKSLASH));
        return auxPkgToUri.equals(newPkgToUri) ? null : findRootUri(auxPkgToUri, paths.keySet());
    }

    /**
     * @param String
     *            ru.
     * @param Set
     *            String keys
     * @return Set String
     */
    private Set<String> findSystemRootUri(final String ru, final Set<String> keys) {
        final Set<String> response = new HashSet<String>();
        for (final String k : keys) {
            if (k.contains(ru)) {
                response.add(k.substring(0, k.lastIndexOf(ru)));
            }
        }
        return response.isEmpty() ? null : response;
    }

    /**
     * @param dependencias
     * @return
     */
    private Set<String> formatDepsCanonicalNames(final ArrayList<String> dependencias) {
        final Set<String> eval = new HashSet<String>();
        for (final String key : dependencias) {
            if (depsPath.containsKey(key.replace(SimpleClassLoader.DOT_CHAR, SimpleClassLoader.SLASH_CHAR))) {
                eval.add(key.replace(SimpleClassLoader.DOT_CHAR, SimpleClassLoader.SLASH_CHAR));
            }
            if (depsPath.containsKey(key.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR))) {
                eval.add(key.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR));
            }
        }
        return eval;
    }

    /**
     * @param classname
     * @param path
     * @return
     * @throws URISyntaxException
     * @throws ClassLoaderException
     */
    private Class<?> getClass(final String classname, final String path)
            throws URISyntaxException, ClassLoaderException {
        final String uri = path.substring(0, path.lastIndexOf(SimpleClassLoader.BACKSLASH));
        if (!paths.containsKey(uri)) {
            paths.put(uri, uri);
        }
        Class<?> c = getFromClass(classname);
        if (c != null) {
            return c;
        }
        c = getFromSystemClass(classname);
        if (c != null) {
            return c;
        }
        try {
            final String name = classname.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR);
            if (classCache.containsKey(name)) {
                return getClassCache(name);
            }
            try {
                final Class<?> loadedclass = loadClass(name);
                if (loadedclass != null) {
                    return loadedclass;
                }
            } catch (final Exception ex) {}
            final InputStream classImput = new FileInputStream(path);
            return fromStream(classImput);
        } catch (final Exception e) {
            throw new ClassLoaderException(classname);
        }
    }

    /**
     * @param classImput
     * @return
     * @throws Exception
     * @throws IOException
     * @throws ClassFormatError
     */
    public Class<?> fromStream(final InputStream classImput) throws Exception, IOException, ClassFormatError {
        final byte[] classData = loadClassData(classImput);
        classImput.close();
        final Class<?> clazz = defineClass(null,classData, 0, classData.length);
        addClassCache(clazz);
        return clazz;
    }

    /**
     * 
     * @param String
     *            name
     * @return Class<?>
     */
    private Class<?> getClassCache(final String name) {
        return classCache.get(name);
    }

    /**
     * @param name
     * @return
     */
    private Class<?> getClassFromRepo(final String name) {
        final ArrayList<String> deps = new ArrayList<String>();
        deps.add(name);
        try {
            if (repoClassLoader != null) {
                String auxlib = repoClassLoader.libpath;
                // first try package
                String auxName = name.replace(SimpleClassLoader.DOT_CHAR, SimpleClassLoader.SHARP_CHAR)
                        .replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.SHARP_CHAR);
                String[] fragments = auxName.split(String.valueOf(SimpleClassLoader.SHARP_CHAR));
                for (int i = 0; i < fragments.length - 1; i++) {
                    StringBuffer rPath = new StringBuffer();
                    repoClassLoader.libpath = auxlib;
                    for (int j = 0; j < fragments.length - i - 1; j++) {
                        rPath = rPath.append(SimpleClassLoader.LINE_SEPARATOR).append(fragments[j]);
                    }
                    try {
                        final Class<?> response = repoClassLoader.loadClassByNameAndPackage(name,
                                repoClassLoader.libpath + rPath + SimpleClassLoader.LINE_SEPARATOR);
                        if (response != null) {
                            repoClassLoader.libpath = auxlib;
                            addClassesToLoader(response);
                            classCache.putAll(repoClassLoader.classCache);
                            return response;
                        }
                    } catch (final Throwable e) {
                        continue;
                    }
                }
                repoClassLoader.libpath = auxlib;
            }
            if (!loadDeps(deps) && repoClassLoader != null) {
                final Class<?> response = repoClassLoader.loadClassByName(name);
                addClassesToLoader(response);
                classCache.putAll(repoClassLoader.classCache);
                if (response != null) {
                    return response;
                }
            }
        } catch (final Throwable e) {
            return null;
        }
        return null;
    }

    /**
     * 
     * @param Class<?>
     *            classz
     * @param String
     *            name
     * @return Field
     */
    private Field getfieldFromClassSuperClass(final Class<?> classz, final String name) {
        try {
            return "Object".equals(classz.getName()) ? null : classz.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return getfieldFromClassSuperClass(classz.getSuperclass(), name);
        }
    }

    /**
     * @param classname
     * @return
     */
    private Class<?> getFromClass(final String classname) {
        try {
            return Class.forName(classname);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * @param classname
     * @return
     */
    private Class<?> getFromSystemClass(final String classname) {
        try {
            return super.findSystemClass(classname);
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param key
     * @throws NoClassDefFoundError
     */
    private boolean ldepsFromDepsPath(final String key) throws NoClassDefFoundError {
        try {
            final String name = key.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR);
            final URL ur = depsPath.get(key);
            final InputStream imput = ur.openStream();
            final byte[] classData = loadClassData(imput);
            imput.close();
            addClassCache(defineClass(name, classData, 0, classData.length));
            return true;
        } catch (final NoClassDefFoundError e) {
            throw e;
        } catch (final Throwable e) {
            return false;
        }
    }

    /**
     * 
     * @param ArrayList<String>
     *            dependencias.
     * @return boolean
     * @throws Throwable
     */
    private void loadAllDeps(final ArrayList<String> dependencias) throws Throwable {
        if (dependencias == null) {
            return;
        }
        final Set<String> eval = formatDepsCanonicalNames(dependencias);
        for (final String key : eval) {
            if (depsPath.containsKey(key) && !classCache.containsKey(key)) {
                try {
                    ldepsFromDepsPath(key);
                } catch (Exception e) {
                    continue;
                }
            }
        }

    }

    /**
     * 
     * @param String
     *            classname
     * @return Class<?>
     * @throws ClassLoaderException
     */
    private Class<?> loadClassByNameAndPackage(final String classname, String pakageRelativePath)
            throws ClassLoaderException {
        Class<?> response = null;
        try {
            loadlib(pakageRelativePath);

            final ArrayList<String> deps = new ArrayList<String>();
            deps.add(classname);
            loadDeps(deps);
            response = loadClass(classname.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR));
            if (response != null) {
                return response;
            }
        } catch (final Throwable e) {
            if (response != null || repoClassLoader == null) {
                throw new ClassLoaderException(classname, e);
            }
            try {
                response = repoClassLoader.loadClassByNameAndPackage(
                        classname.replace(SimpleClassLoader.SLASH_CHAR, SimpleClassLoader.DOT_CHAR),
                        pakageRelativePath);
            } catch (final Throwable ex) {
                throw new ClassLoaderException(classname);
            }
            classCache.putAll(repoClassLoader.classCache);
            return response;
        }

        return response;
    }

    /**
     * 
     * @param InputStream
     *            classImput.
     * @return byte[]
     * @throws Exception
     */
    private byte[] loadClassData(final InputStream classImput) throws Exception {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = classImput.read();

        while (data != -1) {
            buffer.write(data);
            data = classImput.read();
        }
        return buffer.toByteArray();
    }

    /**
     * 
     * @param File
     *            f.
     * @return void
     * @throws IOException
     * @throws Throwable
     */
    private void loadFolder(final File f) throws Throwable {
        if (f.listFiles() == null) {
            loadJar(f.getAbsolutePath());
        } else {
            for (final File fileElement : f.listFiles()) {
                loadFolder(fileElement);
            }
        }

    }

    /**
     * 
     * @param String
     *            pathToJar.
     * @return void
     * @throws IOException
     */
    @SuppressWarnings("resource")
    private void loadJar(final String pathToJar) throws IOException {
        if (pathToJar.endsWith(SimpleClassLoader.CLASS_EXTENSION)) {
            String className = pathToJar.substring(libpath.length() + 1, pathToJar.length() - 6)
                    .replace(SimpleClassLoader.LINE_SEPARATOR, String.valueOf(SimpleClassLoader.DOT_CHAR));
            final URL ur = new File(pathToJar).toURI().toURL();
            addDependencyPath(className, ur);
        } else {
            if (pathToJar.endsWith(SimpleClassLoader.JAR_EXTENSION)) {
                final JarFile jarFile = new JarFile(pathToJar);
                final Enumeration<JarEntry> e = jarFile.entries();
                while (e.hasMoreElements()) {
                    final JarEntry je = e.nextElement();
                    addJarEntry(pathToJar, je);

                }
            }
        }
    }

    private void setignoreClassNotFound(Boolean ignore) {
        ignoreClassNotFound = ignore.booleanValue();

    }
}