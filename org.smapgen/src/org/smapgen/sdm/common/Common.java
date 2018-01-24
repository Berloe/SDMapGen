package org.smapgen.sdm.common;

import java.util.HashMap;

import org.smapgen.sdm.ISimpleDataObjMapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;
import org.smapgen.sdm.registry.Registry;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class Common implements ISimpleDataObjMapper {

    private static final char DOT = '.';
    private static final char GREATER_THAN = '>';
    private static final String INSTANCEOF = " instanceof ";
    private static final char KEY_R = '}';
    private static final char LESS_THAN = '<';
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final char PARENTHESIS_L = '(';
    private static final char PARENTHESIS_R = ')';
    private static final char SEMICOLON = ';';
    private static final char SPACE = ' ';
    private static final String STR_Equals = " = ";

    /**
     * @param fcode
     * @param source
     * @param target
     * @param targetName
     * @param sourceName
     * @param functionName
     * @param isprivate
     */
    public static void addMappingMethod(final StringBuffer fcode, final Class<? extends Object> source, final Class<?> target, final String targetName, final String sourceName, final String functionName, final boolean isprivate) {
        Registry.addFunctionToList(Common.createMappingMethod(fcode, source, target, targetName, sourceName, functionName, isprivate));
    }

    /**
     * @param newSourceName
     * @return
     */
    public static StringBuffer addNullValidatorstart(final String newSourceName) {
        return new StringBuffer().append("if(").append(newSourceName).append("!=null){");
    }

    /**
     * @param b
     * @param source
     * @param target
     * @param targetName
     * @param sourceName
     * @param functionName
     * @param isprivate
     * @return
     */
    public static StringBuffer createMappingMethod(final StringBuffer b, final Class<?> source, final Class<?> target, final String targetName, final String sourceName, final String functionName, final boolean isprivate) {
        return new StringBuffer().append("/**").append(Common.LINE_SEPARATOR).append("* @param ").append(sourceName).append(Common.LINE_SEPARATOR).append("* @return ").append(target.getSimpleName()).append(Common.LINE_SEPARATOR).append("**/").append(Common.LINE_SEPARATOR).append(isprivate ? "  private static " : "    public static ").append(target.getCanonicalName()).append(Common.SPACE).append(functionName).append(Common.SPACE).append(Common.PARENTHESIS_L).append(source.getCanonicalName())
                .append(Common.SPACE).append(sourceName).append(") {").append(b).append(" return ").append(targetName).append(Common.SEMICOLON).append(Common.KEY_R);
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param Object
     *            targetField
     * @return String
     */
    public static String createNewVar(final StringBuffer b, final Class<?> targetClass) {
        final String ret = Common.genName(targetClass, Boolean.FALSE);
        b.append(targetClass.getCanonicalName()).append(Common.SPACE).append(ret).append(" =  new ").append(targetClass.getCanonicalName()).append("();");
        return ret;
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param Object
     *            targetField
     * @return String
     */
    public static String createNewVar(final StringBuffer b, final Object targetField) {
        return Common.createNewVar(b, targetField.getClass());
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param Object
     *            targetField
     * @return String
     */
    public static String createNewVarArray(final StringBuffer b, final Class<?> targetField) {
        final String ret = Common.genName(targetField, Boolean.FALSE);
        b.append(targetField.getCanonicalName()).append("[] ").append(ret).append(" =  null;");
        return ret;
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param Object
     *            targetField
     * @return String
     */
    public static String createNewVarArray(final StringBuffer b, final Object targetField) {
        final String ret = Common.genName(targetField, Boolean.FALSE);
        b.append(targetField.getClass().getCanonicalName()).append("[] ").append(ret).append(" =  null");
        return ret;
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param Object
     *            targetField
     * @param MappingField
     *            targetMappingField
     * @return String
     */
    public static String createNewVarCollection(final StringBuffer b, final Object targetField, final MappingField targetMappingField) {
        final String name = "list" + Common.genName(targetField, Boolean.FALSE);
        final String concreteType = Utils.getConcreteType(targetMappingField);
        b.append(concreteType).append(Common.LESS_THAN).append(targetMappingField.getFieldType().getCanonicalName()).append(Common.GREATER_THAN).append(name).append(" =  new ").append(concreteType).append(Common.LESS_THAN).append(targetMappingField.getFieldType().getCanonicalName()).append(">();");
        return name;
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param Object
     *            targetField
     * @return String
     */
    public static String createNewVarNull(final StringBuffer b, final Class<?> targetClass) {
        final String ret = Common.genName(targetClass, Boolean.FALSE);
        b.append(targetClass.getCanonicalName()).append(Common.SPACE).append(ret).append(" =  null;");
        return ret;
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param MappingField
     *            sourceField
     * @param Object
     *            datoSource
     * @return String
     */
    public static String createVar(final StringBuffer b, final MappingField sourceField, final Class<?> datoSource) {
        final String ret = Common.genName(datoSource, Boolean.TRUE);
        b.append(datoSource.getCanonicalName());
        if (MappingType.ARRAY.equals(sourceField.getGetterGenericType())) {
            b.append("[]");
        } else if (MappingType.COLLECTION.equals(sourceField.getGetterGenericType())) {
            b.append(Common.LESS_THAN).append(sourceField.getCalculatedFieldType().getCanonicalName()).append(Common.GREATER_THAN);
        }
        b.append(Common.SPACE);
        b.append(ret).append(Common.STR_Equals).append(sourceField.getVarName()).append(Common.DOT).append(sourceField.getGetterMethod().getName()).append("();");
        return ret;
    }

    /**
     *
     * @param StringBuffer
     *            sb.
     * @param MappingField
     *            sourceField
     * @param Object
     *            datoSource
     * @return String
     */
    public static void createVar(final StringBuffer b, final MappingField sourceField, final Class<?> datoSource, final String name) {
        b.append(datoSource.getCanonicalName());
        if (MappingType.ARRAY.equals(sourceField.getGetterGenericType())) {
            b.append("[]");
        } else if (MappingType.COLLECTION.equals(sourceField.getGetterGenericType())) {
            b.append(Common.LESS_THAN).append(sourceField.getCalculatedFieldType().getCanonicalName()).append(Common.GREATER_THAN);
        }
        b.append(Common.SPACE);
        b.append(name).append(Common.STR_Equals).append(sourceField.getVarName()).append(Common.DOT).append(sourceField.getGetterMethod().getName()).append("();");
    }

    /**
     * @param concreteSource
     * @param sourceName
     * @param excluded
     * @return
     */
    public static StringBuffer excludeElements(final Class<?> concreteSource, final String sourceName, final Class<?>[] excluded) {
        final StringBuffer b = new StringBuffer();
        if (excluded.length > 0) {
            b.append("&& !(");
            boolean isFirst = true;
            for (final Class<?> classExcluded : excluded) {
                if (!classExcluded.getSimpleName().equals(concreteSource.getSimpleName())) {
                    if (!isFirst) {
                        b.append(" || ");
                    } else {
                        isFirst = false;
                    }
                    b.append(sourceName).append(Common.INSTANCEOF).append(classExcluded.getCanonicalName());
                }
            }
            b.append(Common.PARENTHESIS_R);
        }
        return b;
    }

    /**
     *
     * @param Object
     *            data.
     * @param Boolean
     *            isIn
     * @return String
     */
    public static String genName(final Class<?> data, final Boolean isIn) {
        return Registry.registerName(data.getSimpleName().substring(0, 1).toLowerCase() + (!data.isArray() ? data.getSimpleName().substring(1) : data.getSimpleName().substring(1, data.getSimpleName().length() - 2)) + (isIn ? ISimpleDataObjMapper.config.getINSufix() : ISimpleDataObjMapper.config.getOUTSufix()));

    }

    /**
     *
     * @param Object
     *            data.
     * @param Boolean
     *            isIn
     * @return String
     */
    public static String genName(final Object data, final Boolean isIn) {
        return Common.genName(data.getClass(), isIn);

    }

    /**
     * @param sourceName
     * @param newSourceName
     * @param sourceClass
     * @param classExcluded
     * @param objecMapping
     * @param ignoreNullValid
     * @return
     */
    public static StringBuffer instanceOfMap(final String sourceName, final String newSourceName, final String sourceClass, final String classExcluded, final String objecMapping, final boolean ignoreNullValid) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("if(").append(sourceName).append(Common.INSTANCEOF).append(sourceClass).append(classExcluded).append("){");
        if (classExcluded.length() <= 0) {
            buffer.append(sourceClass).append(Common.SPACE).append(newSourceName).append("= (").append(sourceClass).append(Common.PARENTHESIS_R).append(sourceName).append(Common.SEMICOLON);
        }
        Common.mappingObj(newSourceName, objecMapping, buffer, ignoreNullValid);
        buffer.append(Common.postBlock());
        return buffer;
    }

    /**
     * @param newSourceName
     * @param objMapCode
     * @param buffer
     * @param ignoreNullValid
     */
    public static void mappingObj(final String newSourceName, final String objMapCode, final StringBuffer buffer, final boolean ignoreNullValid) {
        if (!ignoreNullValid) {
            buffer.append(Common.preBlock(newSourceName));
            buffer.append(objMapCode);
            buffer.append(Common.postBlock());
        } else {
            buffer.append(objMapCode);
        }
    }

    /**
     * @param newSourceName
     * @return
     */
    public static StringBuffer nullValidation(final String name) {
        return new StringBuffer().append(name).append("!=null");
    }

    /**
     * @param sourceField
     * @param newSourceName
     * @param objMapCode
     * @param ignoreNullValid
     * @return
     */
    public static StringBuffer objMap(final MappingField sourceField, final String newSourceName, final String objMapCode, final boolean ignoreNullValid) {
        final StringBuffer buffer = new StringBuffer();
        Common.createVar(buffer, sourceField, sourceField.getFieldType(), newSourceName);
        Common.mappingObj(newSourceName, objMapCode, buffer, ignoreNullValid);
        return buffer;
    }

    /**
     * @return StringBuffer
     */
    public static StringBuffer postBlock() {
        return new StringBuffer().append(Common.KEY_R);
    }

    /**
     * @param newSourceName
     * @return
     */
    public static StringBuffer preBlock(final String newSourceName) {
        return Common.addNullValidatorstart(newSourceName);
    }

    /**
     * @param b
     * @param targetName
     * @param maptarget
     */
    public static void setNotMappedNull(final StringBuffer b, final String targetName, final HashMap<String, MappingField> maptarget) {
        for (final MappingField targetMappingField : maptarget.values()) {
            if (!targetMappingField.getMapped()) {
                b.append(Common.valueAssignNull(targetName, targetMappingField)).append("//TODO:Not Mapped").append(Common.LINE_SEPARATOR);
            }
        }
    }

    /**
     *
     * @param MappingField
     *            sourceField.
     * @param MappingField
     *            targetField
     * @return StringBuffer
     */
    public static StringBuffer valueAssign(final MappingField sourceField, final MappingField targetField) {
        return new StringBuffer().append(targetField.getVarName() + Common.DOT + targetField.getSetterMethod().getName() + Common.PARENTHESIS_L + sourceField.getVarName() + Common.DOT + sourceField.getGetterMethod().getName() + "());");
    }

    /**
     *
     * @param String
     *            sourceField.
     * @param MappingField
     *            targetField
     * @return StringBuffer
     */
    public static StringBuffer valueAssign(final String sourceField, final MappingField targetField) {
        return new StringBuffer().append(targetField.getVarName() + Common.DOT + targetField.getSetterMethod().getName() + Common.PARENTHESIS_L + sourceField + ");");
    }

    /**
     * @param newTarget
     * @param newSourceName
     * @return
     */
    public static StringBuffer valueAssignDirect(final String newTarget, final String newSourceName) {
        return new StringBuffer().append(newTarget).append(Common.STR_Equals).append(newSourceName).append(Common.SEMICOLON);
    }

    /**
     *
     * @param String
     *            newtargetName.
     * @param MappingField
     *            targetField
     * @param String
     *            newSourceName
     * @param String
     *            fName
     * @return Object
     */
    public static StringBuffer valueAssignFunction(final MappingField targetField, final String newSourceName, final String fName) {
        return new StringBuffer().append(targetField.getVarName()).append(Common.DOT).append(targetField.getSetterMethod().getName()).append(Common.PARENTHESIS_L).append(fName).append(Common.PARENTHESIS_L).append(newSourceName).append("));");
    }

    /**
     * @param newtargetName
     * @param newSourceName
     * @param fName
     * @return
     */
    public static StringBuffer valueAssignFunction(final String newtargetName, final String newSourceName, final String fName) {
        return new StringBuffer().append(newtargetName).append(Common.STR_Equals).append(fName).append(Common.PARENTHESIS_L).append(newSourceName).append(");");
    }

    /**
     *
     * @param String
     *            targetName.
     * @param MappingField
     *            targetField
     * @return StringBuffer
     */
    public static StringBuffer valueAssignNull(final String targetName, final MappingField targetField) {
        return new StringBuffer().append(targetName).append(Common.DOT).append(targetField.getSetterMethod().getName()).append("(null);");
    }

    /**
     *
     * @param MappingField
     *            targetField.
     * @param String
     *            calendarName
     * @return StringBuffer
     */
    public static StringBuffer valueAssignTimeInMilllis(final MappingField targetField, final String calendarName) {
        return new StringBuffer().append(targetField.getVarName()).append(Common.DOT).append(targetField.getSetterMethod().getName()).append(Common.PARENTHESIS_L).append(calendarName).append("());");
    }

    /**
     *
     */
    private Common() {
        super();
    }
}
