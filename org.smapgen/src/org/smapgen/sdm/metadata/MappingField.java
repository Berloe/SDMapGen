package org.smapgen.sdm.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MappingField {

    /** Field for mapping. */
    private Field field;
    /** Field type. */
    private Class<?> calculatedFieldType;
    /** Getter return type. */
    private MappingType getterGenericType;
    /** Getter Method. */
    private Method getterMethod;
    /** Field Name. */
    private String name;
    /** Field class. */
    private Class<?> fieldType;
    /** Setter p�rameter type. */
    private MappingType setterGenericType;
    /** Setter Method. */
    private Method setterMethod;
    /** Constructor. */
    private String varName;
//    /**Resolved Class**/
//    private Class<?> resolvedAbsClss;
    
    private Boolean mapped;
    private ArrayList<String> anotations = new ArrayList<String>();

    public Field getField() {
        return field;
    }

    public Class<?> getCalculatedFieldType() {
        return calculatedFieldType;
    }

    public MappingType getGetterGenericType() {
        return getterGenericType;
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public String getName() {
        return name;
    }

    public Class<?> getFieldType() {
        return fieldType;
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

    public void setField(final Field f) {
        this.field = f;
    }

    public void setCalculatedFieldType(final Class<?> fieldType) {
        this.calculatedFieldType = fieldType;
    }

    public void setGetterGenericType(final MappingType t) {
        getterGenericType = t;
    }

    public void setGetterMethod(final Method getterMethod) {
        this.getterMethod = getterMethod;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setFieldType(final Class<?> objClass) {
        this.fieldType = objClass;
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

	/**
	 * @return the mapped
	 */
	public Boolean getMapped() {
		return mapped;
	}

	/**
	 * @param mapped the mapped to set
	 */
	public void setMapped(Boolean mapped) {
		this.mapped = mapped;
	}
	
	/**
	 * @param anotationsTypes
	 */
	public void setAnotations(ArrayList<String> anotationsTypes) {
	    this.anotations = anotationsTypes;
    }
	
    /**
     * @return the anotations
     */
    public ArrayList<String> getAnotations() {
        return anotations;
    }
    
    /**
     * @return
     */
    public MappingField cloneMappingField() {
        MappingField newMappingField = new MappingField();
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

}
