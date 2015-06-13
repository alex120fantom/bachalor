package ua.kh.khpi.alex_babenko.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

	public static String getPropertyByCode(PropertyEnum propertyCode) {
		Properties prop = new Properties();
		String value = "";
		try {
			InputStream inputStream = PropertiesHelper.class.getClassLoader()
					.getResourceAsStream("config.properties");
			prop.load(inputStream);
			value = prop.getProperty(propertyCode.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
}
