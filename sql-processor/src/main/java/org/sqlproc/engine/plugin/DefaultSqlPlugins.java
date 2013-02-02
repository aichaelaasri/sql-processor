package org.sqlproc.engine.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.sqlproc.engine.SqlFeature;
import org.sqlproc.engine.impl.SqlProcessContext;
import org.sqlproc.engine.impl.SqlUtils;
import org.sqlproc.engine.type.SqlMetaType;

/**
 * The SQL Processor plugins standard implementation.
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public class DefaultSqlPlugins implements IsEmptyPlugin, IsTruePlugin, SqlCountPlugin, SqlFromToPlugin,
        SqlSequencePlugin, SqlIdentityPlugin {

    /**
     * The modifier used to detect the empty value and true value. For the usage please see the Wiki Tutorials.
     */
    public static final String MODIFIER_NOTNULL = "notnull";
    /**
     * The modifier used to detect the empty value and true value. For the usage please see the Wiki Tutorials.
     */
    public static final String MODIFIER_ANY = "any";
    /**
     * The modifier used to detect the empty value and true value. For the usage please see the Wiki Tutorials.
     */
    public static final String MODIFIER_NULL = "null";
    /**
     * The modifier used to detect the method call invoked on the parent object. For the usage please see the Wiki
     * Tutorials.
     */
    public static final String MODIFIER_CALL = "call";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotEmpty(String attributeName, Object obj, Object parentObj, SqlMetaType sqlMetaType,
            String inOutModifier, boolean inSqlSetOrInsert, Map<String, String> values, Map<String, Object> features)
            throws IllegalArgumentException {

        Boolean delegatedResult = callMethod(attributeName, parentObj, values);
        if (delegatedResult != null) {
            return delegatedResult;
        }

        String value = (inOutModifier != null) ? inOutModifier.toLowerCase() : null;

        if (MODIFIER_NOTNULL.equalsIgnoreCase(value)) {
            if (obj == null)
                throw new IllegalArgumentException(MODIFIER_NOTNULL);
        }

        if (inSqlSetOrInsert) {
            boolean isEmptyUseMethodIsNull = false;
            if (obj == null && attributeName != null && parentObj != null) {
                Object o = features.get(SqlFeature.EMPTY_USE_METHOD_IS_NULL);
                if (o != null && o instanceof Boolean && ((Boolean) o))
                    isEmptyUseMethodIsNull = true;
            }
            Object isNullObj = null;
            if (isEmptyUseMethodIsNull) {
                try {
                    isNullObj = MethodUtils.invokeMethod(parentObj, "isNull", attributeName);
                } catch (NoSuchMethodException e) {
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }
            if (isNullObj != null && isNullObj instanceof Boolean && ((Boolean) isNullObj)) {
                return true;
            }
            boolean isEmptyForNull = isEmptyUseMethodIsNull;
            if (obj == null) {
                Object o = features.get(SqlFeature.EMPTY_FOR_NULL);
                if (o != null && o instanceof Boolean && ((Boolean) o))
                    isEmptyForNull = true;
                if (!isEmptyForNull)
                    return true;
            }
        }

        if (MODIFIER_ANY.equalsIgnoreCase(value)) {
            return true;
        } else if (MODIFIER_NULL.equalsIgnoreCase(value)) {
            if (obj == null)
                return true;
            else
                return false;
        } else {
            if (obj == null) {
                return false;
            } else if (obj instanceof Collection<?>) {
                if (((Collection<?>) obj).isEmpty())
                    return false;
            } else if (obj.toString().length() <= 0) {
                return false;
            }
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTrue(String attributeName, Object obj, Object parentObj, SqlMetaType sqlMetaType,
            String inOutModifier, Map<String, String> values, Map<String, Object> features) {

        Boolean delegatedResult = callMethod(attributeName, parentObj, values);
        if (delegatedResult != null) {
            return delegatedResult;
        }

        if (inOutModifier == null) {
            if (obj != null) {
                if (obj instanceof Boolean) {
                    return ((Boolean) obj).booleanValue();
                } else if (obj instanceof String) {
                    String str = ((String) obj).trim();
                    return (str.length() > 0 && !str.equalsIgnoreCase("false"));
                } else if (obj instanceof Number) {
                    return ((Number) obj).longValue() > 0;
                } else if (obj.getClass().isEnum()) {
                    return true;
                } else {
                    return true; // not null
                }
            }
            return false;
        } else {
            if (obj == null) {
                if (inOutModifier.toLowerCase().equalsIgnoreCase(MODIFIER_NULL))
                    return true;
                else
                    return false;
            } else {
                if (obj.getClass().isEnum()) {
                    if (obj.toString().equals(inOutModifier)) {
                        return true;
                    } else if (sqlMetaType == SqlProcessContext.getTypeFactory().getEnumStringType()) {
                        return inOutModifier.equals(SqlUtils.getEnumToValue(obj));
                    } else if (sqlMetaType == SqlProcessContext.getTypeFactory().getEnumIntegerType()) {
                        return inOutModifier.equals(SqlUtils.getEnumToValue(obj).toString());
                    } else {
                        Object enumVal = SqlUtils.getEnumToValue(obj);
                        if (enumVal.toString().equals(inOutModifier))
                            return true;
                        return false;
                    }
                } else {
                    if (obj.toString().equals(inOutModifier))
                        return true;
                    else
                        return false;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String sqlCount(StringBuilder sql) {
        String s = sql.toString().toUpperCase();
        int start = s.indexOf("ID");
        int end = s.indexOf("FROM");
        StringBuilder sb = sql;
        if (start < 0 || end < 0 || start > end)
            return "select count(*) as vysledek from (" + sb.toString() + ") derived";
        String s1 = sb.substring(0, start + 2);
        String s2 = sb.substring(end);
        start = s1.toUpperCase().indexOf("SELECT");
        if (start < 0)
            return "select count(*) as vysledek from (" + sb.toString() + ") derived";
        return s1.substring(0, start) + "select count(" + s1.substring(start + 6) + ") as vysledek " + s2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LimitType limitQuery(String queryString, StringBuilder queryResult, Integer firstResult, Integer maxResults,
            boolean ordered) {
        LimitType limitType = new LimitType();

        if (maxResults == null || maxResults <= 0)
            return null;
        if (firstResult != null && firstResult > 0) {
            limitType.alsoFirst = true;
            String limitPattern = (ordered) ? SqlProcessContext.getFeature(SqlFeature.LIMIT_FROM_TO_ORDERED)
                    : SqlProcessContext.getFeature(SqlFeature.LIMIT_FROM_TO);
            if (limitPattern == null && ordered)
                limitPattern = SqlProcessContext.getFeature(SqlFeature.LIMIT_FROM_TO);
            limitType = limitQuery(limitPattern, limitType, queryString, queryResult, firstResult, maxResults);
            return limitType;
        } else {
            String limitPattern = (ordered) ? SqlProcessContext.getFeature(SqlFeature.LIMIT_TO_ORDERED)
                    : SqlProcessContext.getFeature(SqlFeature.LIMIT_TO);
            if (limitPattern == null && ordered)
                limitPattern = SqlProcessContext.getFeature(SqlFeature.LIMIT_TO);
            limitType = limitQuery(limitPattern, limitType, queryString, queryResult, firstResult, maxResults);
            return limitType;
        }
    }

    private Boolean callMethod(String attributeName, Object parentObj, Map<String, String> values) {
        if (attributeName == null || parentObj == null || values == null)
            return null;
        String methodName = values.get(MODIFIER_CALL);
        if (methodName == null)
            return null;
        Object result = null;
        try {
            result = MethodUtils.invokeMethod(parentObj, methodName, attributeName);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (result == null || !(result instanceof Boolean)) {
            return null;
        }
        return (Boolean) result;
    }

    private LimitType limitQuery(String limitPattern, LimitType limitType, String queryString,
            StringBuilder queryResult, Integer firstResult, Integer maxResults) {
        if (limitPattern == null)
            return null;
        int ix = limitPattern.indexOf("$S");
        if (ix >= 0) {
            limitType.afterSql = limitPattern.indexOf("$", ix + 1) > 0;
            queryResult.append(limitPattern.substring(0, ix));
            queryResult.append(queryString);
            queryResult.append(limitPattern.substring(ix + 2));
        } else {
            ix = limitPattern.indexOf("$s");
            if (ix >= 0) {
                limitType.afterSql = limitPattern.indexOf("$", ix + 1) > 0;
                int ix2 = queryString.toLowerCase().indexOf("select");
                if (ix2 < 0)
                    return null;
                queryResult.append(limitPattern.substring(0, ix));
                queryResult.append(queryString.substring(ix2 + 6));
                queryResult.append(limitPattern.substring(ix + 2));
            } else {
                return null;
            }
        }
        if (limitType.alsoFirst) {
            ix = queryResult.indexOf("$F");
            if (ix >= 0) {
                if (queryResult.indexOf("$m", ix) < 0 && queryResult.indexOf("$M", ix) < 0)
                    limitType.maxBeforeFirst = true;
                queryResult.replace(ix, ix + 2, "?");
            } else {
                ix = queryResult.indexOf("$f");
                if (ix >= 0) {
                    limitType.zeroBasedFirst = true;
                    if (queryResult.indexOf("$m", ix) < 0 && queryResult.indexOf("$M", ix) < 0)
                        limitType.maxBeforeFirst = true;
                    queryResult.replace(ix, ix + 2, "?");
                } else {
                    return null;
                }
            }
        }
        ix = queryResult.indexOf("$M");
        if (ix >= 0) {
            queryResult.replace(ix, ix + 2, "?");
        } else {
            ix = queryResult.indexOf("$m");
            if (ix >= 0) {
                limitType.rowidBasedMax = true;
                queryResult.replace(ix, ix + 2, "?");
            } else {
                return null;
            }
        }
        return limitType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String identitySelect(String identitySelectName, String tableName, String columnName) {
        String identitySelect = (SqlIdentityPlugin.SUPPVAL_IDENTITY_SELECT.equals(identitySelectName)) ? null
                : SqlProcessContext.getFeature(identitySelectName);
        if (identitySelect != null)
            return identitySelect;
        return SqlProcessContext.getFeature(SqlFeature.IDSEL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String sequenceSelect(String sequenceName) {
        String sequence = SqlProcessContext.getFeature(sequenceName);
        if (sequence != null)
            return sequence;
        String sequencePattern = SqlProcessContext.getFeature(SqlFeature.SEQ);
        if (sequencePattern == null)
            return null;
        int ix = sequencePattern.indexOf("$n");
        if (ix < 0)
            return sequencePattern;
        if (SqlSequencePlugin.SUPPVAL_SEQUENCE.equals(sequenceName))
            return sequencePattern.substring(0, ix) + SqlFeature.DEFAULT_SEQ_NAME + sequencePattern.substring(ix + 2);
        else
            return sequencePattern.substring(0, ix) + sequenceName + sequencePattern.substring(ix + 2);
    }
}
