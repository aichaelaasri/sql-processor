package org.sqlproc.engine.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlproc.engine.SqlFeature;
import org.sqlproc.engine.SqlRuntimeContext;
import org.sqlproc.engine.SqlRuntimeException;

/**
 * Bean utilities.
 * 
 * @author <a href="mailto:Vladimir.Hudec@gmail.com">Vladimir Hudec</a>
 */
public class BeanUtils {

    /**
     * The internal slf4j logger.
     */
    static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    // used only for the output values handling, it's tested for the result null
    @SuppressWarnings("unchecked")
    public static <E> E getInstance(Class<E> clazz) {
        try {
            int isAstract = clazz.getModifiers() & 0x0400;
            if (isAstract != 0) {
                logger.warn("getInstance: " + clazz + " is abstract");
                return null;
            }
            Object o = ConstructorUtils.invokeConstructor(clazz, (Object[]) null);
            return (E) o;
        } catch (NoSuchMethodException e) {
            logger.error("getInstance", e);
            return null;
        } catch (IllegalAccessException e) {
            logger.error("getInstance", e);
            return null;
        } catch (InvocationTargetException e) {
            logger.error("getInstance", e);
            return null;
        } catch (InstantiationException e) {
            logger.error("getInstance", e);
            return null;
        }
    }

    public static Class<?> getFieldType(Class<?> clazz, String name) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
        if (descriptors != null) {
            for (int i = 0; i < descriptors.length; i++) {
                if (name.equals(descriptors[i].getName())) {
                    return (descriptors[i].getPropertyType());
                }
            }
        }
        return null;
    }

    // used only for the input values handling
    public static Object getProperty(Object bean, String name) {
        try {
            return PropertyUtils.getSimpleProperty(bean, name);
        } catch (IllegalAccessException e) {
            throw new SqlRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new SqlRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public static boolean checkProperty(Object bean, String name) {
        PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor prop : props) {
            if (prop.getName().equals(name))
                return true;
        }
        return false;
    }

    // used only for the output values handling, it's tested for the result null
    public static Method getGetter(Class<?> clazz, String attributeName) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
        if (descriptors != null) {
            for (int i = 0; i < descriptors.length; i++) {
                if (attributeName.equals(descriptors[i].getName())) {
                    return PropertyUtils.getReadMethod(descriptors[i]);
                }
            }
        }
        return null;
    }

    public static class ReturnType {
        Class<?> type;
        Type genericType;
        Class<?> typeClass;
        String methodName;

        ReturnType(Method m) {
            methodName = m.getName();
            type = m.getReturnType();
            genericType = m.getGenericReturnType();
            if (genericType != null && genericType instanceof ParameterizedType)
                typeClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
    }

    // used only for the output values handling, it's tested for the result null
    public static ReturnType getGetterReturnType(Class<?> clazz, String attributeName) {
        Method m = getGetter(clazz, attributeName);
        if (m == null)
            return null;
        return new ReturnType(m);
    }

    // used only for the output values handling, it's tested for the result null
    public static Method getGetter(Object bean, String attributeName) {
        PropertyDescriptor descriptor;
        try {
            descriptor = PropertyUtils.getPropertyDescriptor(bean, attributeName);
        } catch (IllegalAccessException e) {
            logger.error("getProperty", e);
            return null;
        } catch (InvocationTargetException e) {
            logger.error("getProperty", e);
            return null;
        } catch (NoSuchMethodException e) {
            logger.error("getProperty", e);
            return null;
        }
        if (descriptor == null)
            return null;
        Method m = PropertyUtils.getReadMethod(descriptor);
        return m;
    }

    // used only for the output values handling, it's tested for the result null
    public static ReturnType getGetterReturnType(Object bean, String attributeName) {
        Method m = getGetter(bean, attributeName);
        if (m == null)
            return null;
        return new ReturnType(m);
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setSimpleProperty(bean, name, value);
        } catch (IllegalAccessException e) {
            throw new SqlRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new SqlRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new SqlRuntimeException(e);
        }
    }

    // used only for the output values handling, it's tested for the result null
    public static Method getSetter(Object bean, String attrName, Class<?>... attrTypes) {
        PropertyDescriptor descriptor;
        try {
            descriptor = PropertyUtils.getPropertyDescriptor(bean, attrName);
        } catch (IllegalAccessException e) {
            logger.error("getProperty", e);
            return null;
        } catch (InvocationTargetException e) {
            logger.error("getProperty", e);
            return null;
        } catch (NoSuchMethodException e) {
            logger.error("getProperty", e);
            return null;
        }
        if (descriptor == null)
            return null;
        Method m = PropertyUtils.getWriteMethod(descriptor);
        if (m == null)
            return null;
        if (m.getParameterTypes() == null || m.getParameterTypes().length != 1)
            return null;
        Class<?> parameterClass = m.getParameterTypes()[0];
        if (attrTypes == null)
            return m;
        for (Class<?> clazz : attrTypes) {
            if (clazz.isAssignableFrom(parameterClass))
                return m;
        }
        return null;
    }

    public static Object simpleInvokeMethod(Method m, Object obj, Object param) {
        Object result;
        if (m != null) {
            try {
                if (!m.isAccessible())
                    m.setAccessible(true);
                result = m.invoke(obj, param);
            } catch (IllegalAccessException e) {
                throw new SqlRuntimeException(e);
            } catch (IllegalArgumentException e) {
                StringBuilder sb = new StringBuilder("Not compatible output value of type ")
                        .append((param != null) ? param.getClass() : "null");
                sb.append(". The result class of type ").append((obj != null) ? obj.getClass() : "null");
                sb.append(" for the method ").append(m.getName());
                sb.append(" is expecting the paramater(s) of type(s) ").append(
                        (m.getParameterTypes() != null) ? Arrays.asList(m.getParameterTypes()) : "empty");
                sb.append(".");
                throw new SqlRuntimeException(sb.toString(), e);
            } catch (InvocationTargetException e) {
                throw new SqlRuntimeException(e);
            }
        } else
            result = null;
        return result;
    }

    public static boolean simpleInvokeSetter(Object bean, String attrName, Object attrValue, Class<?>... attrTypes) {
        Method m = getSetter(bean, attrName, attrTypes);
        if (m != null) {
            simpleInvokeMethod(m, bean, attrValue);
            return true;
        } else {
            return false;
        }
    }

    public static Object invokeMethod(Object obj, String methodName, Object[] args) {
        try {
            return MethodUtils.invokeMethod(obj, methodName, args);
        } catch (NoSuchMethodException e) {
            throw new SqlRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new SqlRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public static Object invokeMethodIgnoreNoSuchMethod(Object obj, String methodName, Object arg) {
        try {
            return MethodUtils.invokeMethod(obj, methodName, arg);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new SqlRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new SqlRuntimeException(e);
        }
    }

    // enums

    public static Object getEnumToValue(SqlRuntimeContext runtimeCtx, Object obj) {
        if (obj == null)
            return null;
        for (String methodName : runtimeCtx.getFeatures(SqlFeature.METHODS_ENUM_IN)) {
            try {
                return MethodUtils.invokeMethod(obj, methodName, null);
            } catch (NoSuchMethodException e) {
                continue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static Class getEnumToClass(SqlRuntimeContext runtimeCtx, Class clazz) {
        if (clazz == null)
            return null;
        for (String methodName : runtimeCtx.getFeatures(SqlFeature.METHODS_ENUM_IN)) {
            Method m = MethodUtils.getMatchingAccessibleMethod(clazz, methodName, new Class[] {});
            if (m != null)
                return m.getReturnType();
        }
        return null;
    }

    public static Object getValueToEnum(SqlRuntimeContext runtimeCtx, Class<?> objClass, Object val) {
        if (val == null)
            return null;
        for (String methodName : runtimeCtx.getFeatures(SqlFeature.METHODS_ENUM_OUT)) {
            try {
                return MethodUtils.invokeStaticMethod(objClass, methodName, val);
            } catch (NoSuchMethodException e) {
                continue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
