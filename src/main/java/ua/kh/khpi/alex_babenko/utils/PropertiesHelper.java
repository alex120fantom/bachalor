package ua.kh.khpi.alex_babenko.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PropertiesHelper {

	private static final Logger LOG = Logger.getLogger(PropertiesHelper.class);
	
	public static String getPropertyByCode(PropertyEnum propertyCode) {
		Properties prop = new Properties();
		String value = StringUtils.EMPTY;
		try {
			InputStream inputStream = PropertiesHelper.class.getClassLoader()
					.getResourceAsStream("config.properties");
			prop.load(inputStream);
			value = prop.getProperty(propertyCode.getName());
		} catch (IOException e) {
			LOG.error(e);
		}
		return value;
	}
}
