package edu.napier.foodel.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.napier.foodel.utils.FoodelProperties;

public class ServerProperties {
	private Properties prop;

	//This class is a Singleton to ensure that only 1 object is created.
	private ServerProperties() { 
		try (InputStream input = new FileInputStream("config/server.properties")) {
			prop = new Properties();

			// load a properties file
			prop.load(input);
			//Add version
			prop.put("version", "10");
			FoodelProperties.getInstance().setProperties(prop);
			//Share the properties with Foodel
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static ServerProperties instance;
	public static ServerProperties getInstance() {
		if (instance == null) {
			instance = new ServerProperties();
		}
		return instance;
	}

	public String get(String key) {
		return prop.getProperty(key);
	}
}
