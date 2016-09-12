package com.epam.scanner.config;

import com.epam.scanner.utils.manager.ConfigurationManager;

public class AppConfig {
	public final static String SCR_PATH  = ConfigurationManager.getProperty("tdp.path");
	public final static String LIST_JARS_INPUT = ConfigurationManager.getProperty("list.jars.input");
	public final static String LIST_JARS_OUTPUT = ConfigurationManager.getProperty("list.jars.output");
}