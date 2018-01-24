package org.smapgen.sdm.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class FieldUtils {

    private static final char PREFIX_CHOR = "[".charAt(0);

    /**
     * @param loadClass
     * @param name
     * @return
     * @throws Exception
     */

    public static MappingField[] fieldGetter(final Class<?> loadClass, final String name) throws Exception {
        // if we are mappin a list or map:
        if (List.class.isAssignableFrom(loadClass)) {
            return FieldUtils.getElementTypeList(name);
        }
        final Method[] methods = loadClass.getDeclaredMethods();
        final Collection<MappingField> mapFieldColecction = new ArrayList<>();
        // Load MappingField from "get***"
        for (final Method method : methods) {
            final boolean isPrefix = "is".equals(method.getName().substring(0, 2));
            final boolean getPrefix = "get".equals(method.getName().substring(0, 3));

            final String getterName = isPrefix ? method.getName().substring(2) : getPrefix ? method.getName().substring(3) : null;

            if ((getPrefix || isPrefix) && !Modifier.isPrivate(method.getModifiers())) {
                final MappingField mapfield = FieldUtils.getterMetadata(loadClass, name, method, getterName);
                if (null != mapfield) {
                    mapFieldColecction.add(mapfield);
                }
            }
        }
        // Si estiende otra clase miramos en el padre y lo incluimos
        if (loadClass.getSuperclass() != null && !loadClass.getSuperclass().equals(Object.class)) {
            final MappingField[] parentMappinField = FieldUtils.fieldGetter(loadClass.getSuperclass(), name);
            mapFieldColecction.addAll(Arrays.asList(parentMappinField));
        }
        if (!mapFieldColecction.isEmpty()) {
            return mapFieldColecction.toArray(new MappingField[0]);
        }
        return FieldUtils.metadataFromField(loadClass, name);

    }

    /**
     * @param loadClass
     * @param name
     * @return
     */
    public static HashMap<String, MappingField> hashSetFieldLoader(final Class<?> loadClass, final String name) {
        final Method[] methods = loadClass.getDeclaredMethods();
        final HashMap<String, MappingField> response = new HashMap<>();
        // Load MappingField from "set***"
        for (final Method method : methods) {
            final String getterName = method.getName().substring(3);
            if ("set".equals(method.getName().substring(0, 3)) && Modifier.isPublic(method.getModifiers())) {
                try {
                    if (method.getParameterTypes().length == 1) {
                        final Field field = FieldUtils.getterField(loadClass, method, getterName);
                        if (field != null) {
                            response.put(field.getName(), FieldUtils.newSetMapField(name, method, field));
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();

                }
            }
        }
        if (loadClass.getSuperclass() != null && !loadClass.getSuperclass().equals(Object.class)) {
            final HashMap<String, MappingField> hashSetMappinField = FieldUtils.hashSetFieldLoader(loadClass.getSuperclass(), name);
            response.putAll(hashSetMappinField);
        }
        return response;
    }

    /**
     * @param methodNameRoot
     * @param loadClass
     * @param paramType
     * @return
     */
    private static Field findSimilar(final String methodNameRoot, final Class<?> loadClass, final Class<?> paramType) {
        final Field[] fields = loadClass.getDeclaredFields();
        final List<Field> result = new ArrayList<>();
        if (fields != null && fields.length > 0) {
            for (final Field field : fields) {
                if (methodNameRoot.toLowerCase().startsWith(field.getName().toLowerCase()) && paramType.getCanonicalName().equals(field.getType().getCanonicalName())) {
                    result.add(field);
                }
            }
        }
        if (result.size() > 1) {
            Field fieldResult = null;
            for (final Field field : result) {
                if (fieldResult == null || field.getName().length() > fieldResult.getName().length()) {
                    fieldResult = field;
                }
            }
            return fieldResult;
        } else if (result.size() == 1) {
            return result.iterator().next();
        } else {
            return null;
        }
    }

    private static ArrayList<String> getAnotationsTypes(final Annotation[] annotations) {
        final ArrayList<String> anotTypes = new ArrayList<>();
        for (final Annotation annotation : annotations) {
            anotTypes.add(annotation.annotationType().getCanonicalName());
        }
        return anotTypes;
    }

    /**
     * @param name
     * @return
     * @throws Exception
     */
    private static MappingField[] getElementTypeList(final String name) throws Exception {
        final Field field = Field.class.newInstance();
        final MappingField mapfield = new MappingField();
        mapfield.setGetterGenericType(MappingType.COLLECTION);
        mapfield.setField(field);
        mapfield.setName(field.getName());
        mapfield.setCalculatedFieldType(field.getDeclaringClass());
        mapfield.setFieldType(FieldUtils.getfieldType(field, mapfield.getGetterGenericType()));
        mapfield.setVarName(name);
        mapfield.setMapped(Boolean.FALSE);
        return new MappingField[] { mapfield };
    }

    /**
     * @param f
     * @param t
     * @return
     */
    private static Class<?> getfieldType(final Field f, final MappingType t) {
        return !MappingType.COLLECTION.equals(t) ? f.getType() : (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * @param dataClass
     * @return
     */
    private static MappingType getGenericType(final Class<?> dataClass) {
        return dataClass.getName().charAt(0) == FieldUtils.PREFIX_CHOR ? MappingType.ARRAY
                : dataClass.getName().equals(Collection.class.getName()) || dataClass.getName().equals(Set.class.getName()) || dataClass.getName().equals(List.class.getName()) || dataClass.getName().equals(SortedSet.class.getName()) ? MappingType.COLLECTION
                        : dataClass.getName().equals(Date.class.getName()) ? MappingType.DATE : dataClass.getName().equals(Calendar.class.getName()) ? MappingType.CALENDAR : dataClass.getClass().getName().equals(Map.class.getName()) || dataClass.getName().equals(SortedMap.class.getName()) ? MappingType.MAP : dataClass.getName().equals(Enum.class.getName()) ? MappingType.ENUM : MappingType.OBJECT; // Defined
                                                                                                                                                                                                                                                                                                                                                                                                                // basic
        // Mapping types
    }

    /**
     * @param field
     * @param mapfield
     * @return
     */
    private static Class<?> getterCalculatedFieldType(final Field field, final MappingField mapfield) {
        try {
            return FieldUtils.getfieldType(field, mapfield.getGetterGenericType());
        } catch (final Exception e) {
            return field.getDeclaringClass();
        }
    }

    /**
     * @param loadClass
     * @param method
     * @param getterName
     * @return
     * @throws SecurityException
     */
    private static Field getterField(final Class<?> loadClass, final Method method, final String getterName) throws SecurityException {
        Field field = null;
        try {
            field = loadClass.getDeclaredField(getterName.substring(0, 1).toLowerCase() + getterName.substring(1));
        } catch (final NoSuchFieldException e) {
            field = FieldUtils.findSimilar(getterName.substring(0, 1).toLowerCase() + getterName.substring(1), loadClass, method.getReturnType());
        }
        return field;
    }

    /**
     * @param field
     * @param mapfield
     * @return
     */
    private static Class<?> getterGenericType(final Field field, final MappingField mapfield) {
        switch (mapfield.getGetterGenericType()) {
            case COLLECTION:
                return mapfield.getCalculatedFieldType();
            case ARRAY:
                return field.getType().getComponentType();
            default:
                return field.getType();
        }
    }

    /**
     * @param loadClass
     * @param name
     * @param mapFieldColecction
     * @param method
     * @param getterName
     * @return
     */
    private static MappingField getterMetadata(final Class<?> loadClass, final String name, final Method method, final String getterName) {
        try {
            if (method.getParameterTypes().length == 0) {
                final Field field = FieldUtils.getterField(loadClass, method, getterName);
                if (field != null) {
                    final MappingField mapfield = new MappingField();
                    mapfield.setGetterMethod(method);
                    mapfield.setGetterGenericType(FieldUtils.getGenericType(method.getReturnType()));
                    mapfield.setField(field);
                    mapfield.setName(field.getName());
                    mapfield.setCalculatedFieldType(FieldUtils.getterCalculatedFieldType(field, mapfield));
                    mapfield.setFieldType(FieldUtils.getterGenericType(field, mapfield));
                    mapfield.setVarName(name);
                    mapfield.setMapped(Boolean.FALSE);
                    mapfield.setAnotations(FieldUtils.getAnotationsTypes(field.getAnnotations()));
                    return mapfield;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param loadClass
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws ClassNotFoundException
     */
    private static MappingField[] metadataFromField(final Class<?> loadClass, final String name) {
        try {
            final Field field = loadClass.getDeclaredField("value");
            if (field.getDeclaringClass().getName().startsWith("java.lang")) {
                return FieldUtils.setBasicType(name, field);
            }
        } catch (final Exception e) {
            return new MappingField[0];
        }
        return new MappingField[0];
    }

    /**
     * @param name
     * @param method
     * @param field
     * @return
     */
    private static MappingField newSetMapField(final String name, final Method method, final Field field) {
        final MappingField mapfield = new MappingField();
        mapfield.setSetterMethod(method);
        mapfield.setSetterGenericType(FieldUtils.getGenericType(method.getParameterTypes()[0]));
        mapfield.setField(field);
        mapfield.setName(field.getName());
        try {
            mapfield.setCalculatedFieldType(FieldUtils.getfieldType(field, mapfield.getSetterGenericType()));
        } catch (final Exception e) {
            mapfield.setCalculatedFieldType(field.getDeclaringClass());
        }
        switch (mapfield.getSetterGenericType()) {
            case COLLECTION:
                mapfield.setFieldType(mapfield.getCalculatedFieldType());
                break;
            case ARRAY:
                mapfield.setFieldType(field.getType().getComponentType());
                break;
            default:
                mapfield.setFieldType(field.getType());
                break;
        }
        mapfield.setVarName(name);
        mapfield.setMapped(Boolean.FALSE);
        mapfield.setAnotations(FieldUtils.getAnotationsTypes(field.getAnnotations()));
        return mapfield;
    }

    /**
     * @param name
     * @param field
     * @return
     * @throws ClassNotFoundException
     */
    private static MappingField[] setBasicType(final String name, final Field field) throws ClassNotFoundException {
        final MappingField mapfield = new MappingField();
        mapfield.setGetterMethod(null);
        mapfield.setGetterGenericType(FieldUtils.getGenericType(Class.forName(field.getDeclaringClass().getName())));
        mapfield.setField(field);
        mapfield.setName(field.getDeclaringClass().getName());
        mapfield.setCalculatedFieldType(field.getDeclaringClass());
        mapfield.setFieldType(field.getDeclaringClass());
        mapfield.setVarName(name);
        mapfield.setMapped(Boolean.FALSE);
        return new MappingField[] { mapfield };
    }

    /**
     *
     */
    private FieldUtils() {
        super();
    }

}
