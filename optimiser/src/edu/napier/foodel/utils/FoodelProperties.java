package edu.napier.foodel.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FoodelProperties {
	private Properties prop;

	//This class is a Singleton to ensure that only 1 object is created.
	private FoodelProperties() { 

	}

	private static FoodelProperties instance;
	public static FoodelProperties getInstance() {
		if (instance == null) {
			instance = new FoodelProperties();
		}
		return instance;
	}

	public void setProperties(Properties theProps) {
		prop = theProps;
	}
	
	public String get(String key) {
		return prop.getProperty(key);
	}
}
