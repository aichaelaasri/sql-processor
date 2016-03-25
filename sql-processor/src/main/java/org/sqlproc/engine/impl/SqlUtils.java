package org.sqlproc.engine.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sqlproc.engine.SqlFeature;
import org.sqlproc.engine.SqlRuntimeContext;

/**
 * SQL Processor utilities.
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public class SqlUtils {

    // miscs

    public static Object convertBigDecimal(Class<?> attributeType, Object resultValue) {
        if (resultValue == null || !(resultValue instanceof BigDecimal))
            return resultValue;
        BigDecimal result = (BigDecimal) resultValue;
        if (attributeType == Byte.class || attributeType == byte.class) {
            return result.byteValue();
        } else if (attributeType == Integer.class || attributeType == int.class) {
            return result.intValue();
        } else if (attributeType == Long.class || attributeType == long.class) {
            return result.longValue();
        } else if (attributeType == Short.class || attributeType == short.class) {
            return result.shortValue();
        } else if (attributeType == BigInteger.class) {
            return result.toBigInteger();
        }
        return result;
    }

    public static Object convertBigInteger(Class<?> attributeType, Object resultValue) {
        if (resultValue == null || !(resultValue instanceof BigInteger))
            return resultValue;
        BigInteger result = (BigInteger) resultValue;
        if (attributeType == Byte.class || attributeType == byte.class) {
            return result.byteValue();
        } else if (attributeType == Integer.class || attributeType == int.class) {
            return result.intValue();
        } else if (attributeType == Long.class || attributeType == long.class) {
            return result.longValue();
        } else if (attributeType == Short.class || attributeType == short.class) {
            return result.shortValue();
        }
        return result;
    }

    public static Byte[] toBytes(byte[] barr) {
        if (barr == null)
            return null;
        Byte[] res = new Byte[barr.length];
        for (int i = 0; i < barr.length; i++)
            res[i] = new Byte(barr[i]);
        return res;
    }

    public static byte[] toBytes(Byte[] barr) {
        if (barr == null)
            return null;
        byte[] res = new byte[barr.length];
        for (int i = 0; i < barr.length; i++)
            res[i] = (barr[i] != null) ? barr[i].byteValue() : (byte) 0;
        return res;
    }

    public static boolean isEmpty(StringBuilder sb) {
        if (sb == null)
            return true;
        String s = sb.toString().trim();
        if (s.length() == 0)
            return true;
        return false;
    }

    public static String beforeChar(String s, char c) {
        int ix = s.indexOf(c);
        if (ix < 0)
            return s;
        else
            return s.substring(0, ix);
    }

    public static String afterChar(String s, char c) {
        int ix = s.indexOf(c);
        if (ix < 0)
            return null;
        else
            return s.substring(ix + 1);
    }

    // date, time

    public static java.sql.Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return new java.sql.Date(cal.getTime().getTime());
    }

    public static java.sql.Time getTime(int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.YEAR);
        cal.clear(Calendar.MONTH);
        cal.clear(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        return new java.sql.Time(cal.getTime().getTime());
    }

    public static Date getDateTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        return cal.getTime();
    }

    // identifiers
    public static String getIdsKey(Object[] resultValues, Map<String, SqlMappingIdentity> identities, String fullName) {
        String idsKey = "";
        int i = 0;

        SqlMappingIdentity ident = identities.get(fullName);
        for (List<Integer> identityIndexes : ident.allIdentityIndexes) {
            for (Integer identityIndex : identityIndexes) {
                Object o = resultValues[identityIndex];
                if (o != null) {
                    if (i > 0)
                        idsKey += '-';
                    idsKey += identityIndex;
                    idsKey += '-';
                    idsKey += o;
                    break;
                }
            }
            ++i;
        }
        return idsKey;
    }

    public static String getIdsKey(Object[] resultValues, Integer mainIdentityIndex) {
        String idsKey = "" + mainIdentityIndex + "-" + resultValues[mainIdentityIndex];
        return idsKey;
    }

    static final String SQL = "insert into ZADOST_201405 (%id_den, %id_zadost, %typ_zadosti, %kod_agendy, %kod_ais, %kod_ovm, %cinnostni_role, %id_pozadavku_iszr, %id_pozadavku_ais, %uzivatelske_jmeno, %subjekt, %cas, %duvod_a_ucel) "
            + "values (:idZadost, :idDen, :typZadosti, :kodAgendy, :kodAis, :kodOvm, :cinnostniRole, :idPozadavkuIszr, :idPozadavkuAis, :uzivatelskeJmeno, :subjekt, :cas, :duvodAUcel)";
    static final Pattern patternInsert = Pattern.compile("[^\\(]*\\(\\s*([^\\)]*)\\s*\\).*");

    public static void main(String[] args) {
        Map<String, SqlInputValue> identities = new HashMap<String, SqlInputValue>();
        SqlInputValue siv = new SqlInputValue(null, SqlInputValue.Type.IDENTITY_SELECT, null, null, null, null, null,
                "ID_ZADOST");
        identities.put("id_zadost", siv);
        String sql = handleInsertSql(identities, SQL);
        System.out.println(sql);
    }

    public static String handleInsertSql(Map<String, SqlInputValue> identities, String sql) {
        if (identities == null || identities.isEmpty())
            return sql;
        Map<String, SqlInputValue> ids1 = new HashMap<String, SqlInputValue>();
        Map<String, SqlInputValue> ids2 = new HashMap<String, SqlInputValue>();
        for (Entry<String, SqlInputValue> e : identities.entrySet()) {
            if (e.getValue().getValueType() == SqlInputValue.Type.IDENTITY_SELECT) {
                ids1.put(e.getKey(), e.getValue());
                if (e.getValue().getDbIdentityName() != null) {
                    ids2.put(e.getValue().getDbIdentityName(), e.getValue());
                }
            }
        }
        if (ids1.isEmpty())
            return sql;
        Matcher matcher = patternInsert.matcher(sql);
        if (!matcher.matches())
            return sql;
        String fragment = matcher.group(1);
        String[] cols = fragment.split(",");
        int icol = -1;
        for (int i = 0; i < cols.length; i++) {
            String c = cols[i].trim();
            if (c.startsWith("%"))
                c = c.substring(1);
            if (ids2.containsKey(c)) {
                icol = i;
            } else if (ids2.containsKey(c.toLowerCase()))
                icol = i;
            else if (ids2.containsKey(c.toUpperCase()))
                icol = i;
            else if (ids1.containsKey(c))
                icol = i;
            else if (ids1.containsKey(c.toLowerCase()))
                icol = i;
            else if (ids1.containsKey(c.toUpperCase()))
                icol = i;
            if (icol >= 0)
                break;
        }
        if (icol < 0)
            return sql;
        int istart = matcher.start(1);
        int ix = fragment.indexOf(cols[icol]);
        if (ix < 0)
            return sql;
        // System.out.println("XXXXXXXXXX " + ix + " '" + sql + "'");
        String sql1 = sql.substring(0, istart + ix);
        String sql2 = sql.substring(istart + ix + cols[icol].length());
        // System.out.println("YYYYYYYYYY '" + sql1 + "'" + sql2 + "'");
        if (sql1.trim().endsWith(",")) {
            ix = sql1.lastIndexOf(",");
            sql = sql.substring(0, ix) + sql2;
        } else if (sql2.trim().startsWith(",")) {
            ix = sql2.indexOf(",");
            sql = sql1 + sql2.substring(ix + 1);
        } else {
            sql = sql1 + sql2;
        }
        // System.out.println("ZZZZZZZZZZ '" + sql + "'");
        return sql;
    }

    public static List<Integer> asList(int[] array) {
        List<Integer> list = new ArrayList<Integer>();
        if (array == null)
            return list;
        for (int item : array)
            list.add(item);
        return list;
    }

    public static String firstLowerCase(String s) {
        if (s == null)
            return null;
        if (s.length() == 1)
            return s.toLowerCase();
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static Set<String> oppositeFeatures(String featureName) {
        Set<String> oppositeFeatures = new HashSet<String>();
        if (SqlFeature.SURROUND_QUERY_LIKE_FULL.name().equals(featureName)) {
            oppositeFeatures.add(SqlFeature.SURROUND_QUERY_LIKE_PARTIAL.name());
            oppositeFeatures.add(SqlFeature.REPLACE_LIKE_CHARS.name());
        } else if (SqlFeature.SURROUND_QUERY_LIKE_PARTIAL.name().equals(featureName)) {
            oppositeFeatures.add(SqlFeature.SURROUND_QUERY_LIKE_FULL.name());
            oppositeFeatures.add(SqlFeature.REPLACE_LIKE_CHARS.name());
        } else if (SqlFeature.REPLACE_LIKE_CHARS.name().equals(featureName)) {
            oppositeFeatures.add(SqlFeature.SURROUND_QUERY_LIKE_FULL.name());
            oppositeFeatures.add(SqlFeature.SURROUND_QUERY_LIKE_PARTIAL.name());
        } else if (SqlFeature.EMPTY_FOR_NULL.name().equals(featureName)) {
            oppositeFeatures.add(SqlFeature.EMPTY_USE_METHOD_IS_NULL.name());
        } else if (SqlFeature.EMPTY_USE_METHOD_IS_NULL.name().equals(featureName)) {
            oppositeFeatures.add(SqlFeature.EMPTY_FOR_NULL.name());
        }
        return oppositeFeatures;
    }

    public static boolean isPrimitiveWrapper(Class clazz) {
        if (clazz == Boolean.class || clazz == Character.class || clazz == Short.class || clazz == Integer.class
                || clazz == Long.class || clazz == Double.class || clazz == Float.class || clazz == BigInteger.class
                || clazz == BigDecimal.class)
            return true;
        return false;
    }

    public static final String METHOD_IS_NULL = "isNull";
    public static final String METHOD_IS_NULL_ = METHOD_IS_NULL + "_";
    public static final String METHOD_IS_DEF = "isDef";
    public static final String METHOD_IS_DEF_ = METHOD_IS_DEF + "_";
    public static final String METHOD_TO_INIT = "toInit";
    public static final String METHOD_TO_INIT_ = METHOD_TO_INIT + "_";
    public static final String METHOD_GET_OP = "getOp";
    public static final String METHOD_GET_OP_ = METHOD_GET_OP + "_";
    public static final Map<String, String[]> METHODS = new HashMap<>();
    static {
        METHODS.put(METHOD_IS_NULL, new String[] { METHOD_IS_NULL_, METHOD_IS_NULL });
        METHODS.put(METHOD_IS_NULL_, new String[] { METHOD_IS_NULL_ });
        METHODS.put(METHOD_IS_DEF, new String[] { METHOD_IS_DEF_, METHOD_IS_DEF });
        METHODS.put(METHOD_IS_DEF_, new String[] { METHOD_IS_DEF_ });
        METHODS.put(METHOD_TO_INIT, new String[] { METHOD_TO_INIT_, METHOD_TO_INIT });
        METHODS.put(METHOD_TO_INIT_, new String[] { METHOD_TO_INIT_ });
        METHODS.put(METHOD_GET_OP, new String[] { METHOD_GET_OP_, METHOD_GET_OP });
        METHODS.put(METHOD_GET_OP_, new String[] { METHOD_GET_OP_ });
    }

    public static <E> E callMethod(SqlRuntimeContext runtimeCtx, String attributeName, Class<E> attributeClass,
            Object parentObj, String methodName) {
        if (attributeName == null || parentObj == null || methodName == null)
            return null;
        Object result = null;
        String[] methods = METHODS.get(methodName);
        if (methods == null) {
            result = (runtimeCtx.checkMethod(parentObj.getClass(), methodName, String.class))
                    ? runtimeCtx.invokeMethod(parentObj, methodName, attributeName) : null;
        } else {
            for (String method : methods) {
                if (method.startsWith(METHOD_IS_DEF)) {
                    if (runtimeCtx.checkMethod(parentObj.getClass(), method, String.class, Boolean.class)) {
                        Boolean isAttributeNotNull = runtimeCtx.checkAttribute(parentObj, attributeName)
                                ? runtimeCtx.getAttribute(parentObj, attributeName) != null : null;
                        result = runtimeCtx.invokeMethod(parentObj, method, attributeName, isAttributeNotNull);
                        break;
                    } else if (runtimeCtx.checkMethod(parentObj.getClass(), method, String.class)) {
                        // to support old SQLMOP generated POJOs
                        result = runtimeCtx.invokeMethod(parentObj, method, attributeName);
                        break;
                    }
                } else {
                    Class<?> clazz = method.startsWith("is") ? Boolean.class : String.class;
                    if (runtimeCtx.checkMethod(parentObj.getClass(), method, clazz)) {
                        result = runtimeCtx.invokeMethod(parentObj, method, attributeName);
                        break;
                    }
                }
            }
        }
        if (result == null || !(result.getClass() != attributeClass)) {
            return null;
        }
        return (E) result;
    }
}
