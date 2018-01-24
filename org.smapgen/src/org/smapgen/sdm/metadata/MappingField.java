package org.smapgen.sdm.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MappingField {

    private ArrayList<String> anotations = new ArrayList<>();
    /** Field type. */
    private Class<?> calculatedFieldType;
    /** Field for mapping. */
    private Field field;
    /** Field class. */
    private Class<?> fieldType;
    /** Getter return type. */
    private MappingType getterGenericType;
    /** Getter Method. */
    private Method getterMethod;
    private Boolean mapped;
    /** Field Name. */
    private String name;
    /** Setter p�rameter type. */
    private MappingType setterGenericType;

    /** Setter Method. */
    private Method setterMethod;
    /** Constructor. */
    private String varName;
    // /**Resolved Class**/
    // private Class<?> resolvedAbsClss;

    /**
     * @return
     */
    public MappingField cloneMappingField() {
        final MappingField newMappingField = new MappingField();
        newMappingField.setCalculatedFieldType(getCalculatedFieldType());
        newMappingField.setField(getField());
        newMappingField.setFieldType(getFieldType());
        newMappingField.setGetterGenericType(getGetterGenericType());
        newMappingField.setGetterMethod(getGetterMethod());
        newMappingField.setMapped(getMapped());
        newMappingField.setName(getName());
        newMappingField.setSetterGenericType(getSetterGenericType());
        newMappingField.setSetterMethod(getSetterMethod());
        newMappingField.setVarName(getVarName());
        return newMappingField;
    }

    /**
     * @return the anotations
     */
    public ArrayList<String> getAnotations() {
        return anotations;
    }

    public Class<?> getCalculatedFieldType() {
        return calculatedFieldType;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public MappingType getGetterGenericType() {
        return getterGenericType;
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    /**
     * @return the mapped
     */
    public Boolean getMapped() {
        return mapped;
    }

    public String getName() {
        return name;
    }

    public MappingType getSetterGenericType() {
        return setterGenericType;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    /**
     * @return the varName
     */
    public String getVarName() {
        return varName;
    }

    /**
     * @param anotationsTypes
     */
    public void setAnotations(final ArrayList<String> anotationsTypes) {
        anotations = anotationsTypes;
    }

    public void setCalculatedFieldType(final Class<?> fieldType) {
        calculatedFieldType = fieldType;
    }

    public void setField(final Field f) {
        field = f;
    }

    public void setFieldType(final Class<?> objClass) {
        fieldType = objClass;
    }

    public void setGetterGenericType(final MappingType t) {
        getterGenericType = t;
    }

    public void setGetterMethod(final Method getterMethod) {
        this.getterMethod = getterMethod;
    }

    /**
     * @param mapped
     *            the mapped to set
     */
    public void setMapped(final Boolean mapped) {
        this.mapped = mapped;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSetterGenericType(final MappingType t) {
        setterGenericType = t;
    }

    public void setSetterMethod(final Method setterMethod) {
        this.setterMethod = setterMethod;
    }

    /**
     * @param varName
     */
    public void setVarName(final String varName) {
        this.varName = varName;
    }

}
