package com.longlong.longlongdisplay.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author longlong
 * @create 2020 07 09 8:40
 */
public class PropertiesUtil {

    private static InputStream is = ClassLoader.getSystemResourceAsStream("jdbc.properties");
    private static Properties properties = new Properties();
    public static String getProperty(String propertyName) throws IOException {
        properties.load(is);
        return properties.getProperty(propertyName);
    }

}
