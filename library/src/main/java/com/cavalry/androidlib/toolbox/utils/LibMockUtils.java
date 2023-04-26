package com.cavalry.androidlib.toolbox.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 模拟数据: 给bean对象填充数据,或者根据bean对象生成json字符串
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class LibMockUtils {

    private final static String TAG = "LibMockUtils";


    /**
     * 生成bean对象所需要的json串
     * @param bean 只需new一个空的bean对象即可
     * @param level
     * @return
     */
    public static String mockJsonString(Object bean, int level) {
        mockBean(bean, level);
        return getObject(bean);
    }


    /**
     * 给bean对象填充数据
     *
     * @param bean  只需new一个空的bean对象即可,数据会被填充
     * @param level 当前对象层级,传递任何数字都不会影响程序运行,填充的数据不同而已
     */
    public static void mockBean(Object bean, int level) {
        try {
            Field[] fields = bean.getClass().getFields();
            for (Field field : fields) {
                Class<?> type = field.getType();

                if (type.isPrimitive()) {
                    Log.e(TAG, type.getName() + " isPrimitive");
                }

                if (type.isArray()) {
                    Log.e(TAG, type.getName() + " isArray");
                }

                if ("java.util.List".equals(type.getName())) {
                    Log.e(TAG, type.getName() + " isList");
                }

                if (type.isMemberClass()) {
                    Log.e(TAG, type.getName() + " isMemberClass");
                }

                if (type.isPrimitive()) {//原始数据类型
                    setPrimitive(bean, field, type, level);
                } else if (String.class.getName().equals(type.getName())) {//String类型
                    setString(bean, field, level);
                } else if ("java.util.List".equals(type.getName())) {//List类型
                    setList(bean, field, level);
                } else {//Class类型
                    Object clazzObject = type.newInstance();
                    mockBean(clazzObject, level + 1);
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置List数据
     *
     * @param object
     * @param field
     * @param level
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void setList(Object object, Field field, int level) throws IllegalAccessException, InstantiationException {
        List list = new ArrayList();
        field.set(object, list);

        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class genericClazz = (Class) pt.getActualTypeArguments()[0];

            if (genericClazz.isMemberClass()) {
                Log.e(TAG, genericClazz.getName() + " isMemberClass");
            }

            Object genericObject1 = genericClazz.newInstance();
            list.add(genericObject1);
            mockBean(genericObject1, level + 1);

            Object genericObject2 = genericClazz.newInstance();
            list.add(genericObject2);
            mockBean(genericObject2, level + 2);

            Object genericObject3 = genericClazz.newInstance();
            list.add(genericObject3);
            mockBean(genericObject3, level + 3);
        }
    }

    /**
     * 设置String类型数据
     *
     * @param object
     * @param field
     * @param level
     * @throws IllegalAccessException
     */
    private static void setString(Object object, Field field, int level) throws IllegalAccessException {
        field.set(object, field.getName() + level);
    }

    /**
     * 设置原始类型数据
     *
     * @param object
     * @param field
     * @param type
     * @param level
     * @throws IllegalAccessException
     */
    private static void setPrimitive(Object object, Field field, Class<?> type, int level) throws IllegalAccessException {
        if (int.class.getName().equals(type.getName()))
            field.setInt(object, level);
        if (long.class.getName().equals(type.getName()))
            field.setLong(object, level);
        if (float.class.getName().equals(type.getName()))
            field.setFloat(object, level);
        if (double.class.getName().equals(type.getName()))
            field.setDouble(object, level);
    }

    private static String getPrimitive(Object object, Field field) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(field.getName())
                .append("\"")
                .append(":")
                .append(field.getInt(object))
                .append(",");
        return sb.toString();

    }

    private static String getString(Object object, Field field) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(field.getName())
                .append("\"")
                .append(":")
                .append("\"")
                .append(field.get(object))
                .append("\"")
                .append(",");
        return sb.toString();
    }


    private static String getList(Object object, Field field) throws IllegalAccessException, InstantiationException {
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(field.getName())
                .append("\"")
                .append(":")
                .append("[");

        List list = (List) field.get(object);

        if (list == null || list.size() <= 0) {
            sb.append("]")
                    .append(",");
            return sb.toString();
        }

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class genericClazz = (Class) pt.getActualTypeArguments()[0];

            if (genericClazz.isMemberClass()) {
                for (Object obj : list) {
                    sb.append(getObject(obj));
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length());//删掉最后一个","
            } else {
                Log.e(TAG, "List的泛型不是 MemberClass, 放弃序列化");
            }

        }

        sb.append("]")
                .append(",");

        return sb.toString();
    }

    private static String getObject(Object object) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("{");

            Field[] fields = object.getClass().getFields();
            for (Field field : fields) {
                Class<?> type = field.getType();
                if (type.isPrimitive()) {//原始数据类型
                    sb.append(getPrimitive(object, field));
                } else if (String.class.getName().equals(type.getName())) {//String类型
                    sb.append(getString(object, field));
                } else if ("java.util.List".equals(type.getName())) {//List类型
                    sb.append(getList(object, field));
                } else {//Class类型
                    sb.append("\"")
                            .append(field.getName())
                            .append("\"")
                            .append(":")
                            .append(getObject(field.get(object)))
                            .append(",");
                }
            }
            sb.deleteCharAt(sb.length());//删除最后一个","
            sb.append("}");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
