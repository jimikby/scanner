package com.epam.scanner.utils.manager;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class GradleConfigManager {

	private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("gradle");

	private GradleConfigManager() {
	}

	public static String getProperty(String key) {

		return resourceBundle.getString(key);
	}
	
	public static Enumeration<String> getKeys() {

		return resourceBundle.getKeys();
	}
}