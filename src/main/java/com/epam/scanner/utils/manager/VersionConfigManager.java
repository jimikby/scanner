package com.epam.scanner.utils.manager;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class VersionConfigManager {

	private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("version");

	private VersionConfigManager() {
	}

	public static String getProperty(String key) {

		return resourceBundle.getString(key);
	}
	
	public static Enumeration<String> getKeys() {
		return resourceBundle.getKeys();
		
	}
}