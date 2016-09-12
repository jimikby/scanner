package com.epam.scanner.utils.parser.common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A factory for creating ParserXML objects.
 */

public class ParserFactory {

	private static final Logger LOGGER = LogManager.getLogger(ParserFactory.class);

	public static ParcerXml create(ParserType type) {

		ParcerXml parser = null;
		switch (type) {

		case STAX:
			parser = new ParserStax();
			break;

		case DOM:
			parser = new ParserDom();
			break;

		case SAX:
			parser = new ParserSax();
			break;

		default:
			LOGGER.log(Level.ERROR,"Parser type - " + type + " is not created.");
		}
		return parser;
	}
}