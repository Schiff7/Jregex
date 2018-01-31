package cn.hu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

/**
 * Utils
 */
@SuppressWarnings("rawtypes")
public class Utils {
    static void show(Object o) {
        StringBuffer result = new StringBuffer();
        Class clazz = o.getClass();
        if (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
                || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class)
                || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.isPrimitive()) {
            result.append(o);
        } else {
            result.append(clazz.getName()).append("\n{\n");
            List<Field> field = Arrays.asList(clazz.getDeclaredFields());
            if (!clazz.isPrimitive()) {
                for (Field f : field) {
                    f.setAccessible(true);
                    Object val = null;
                    try {
                        val = f.get(o);
                    } catch (Exception e) {
                        //TODO: handle exception
                        System.out.println(e);
                    }
                    result.append("\t").append(f.getName()).append(": ").append(val).append(",\n");
                }
            }
            result.append("}");
        }
        System.out.println(result);
    }

    public static Object deepCopy(Object o) throws IOException, ClassNotFoundException {  
        //先序列化，写入到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(o);
        //然后反序列化，从流里读取出来，即完成复制
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }
}