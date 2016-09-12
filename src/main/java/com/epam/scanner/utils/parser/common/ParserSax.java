package com.epam.scanner.utils.parser.common;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.epam.scanner.exception.ParserXmlException;

/**
 * The Class ParserSax.
 */
public class ParserSax implements ParcerXml {

	/** The service. */
	private ParserBuilder builder;

	@Override
	public List<ParsedItem> create(File path, ParserBuilder builder) throws ParserXmlException {

		this.builder = builder;

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;

		try {
			saxParser = saxParserFactory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e1) {
			throw new ParserXmlException("Exception in ParserSax class " + e1);
		}
		ParserSaxHandler handler = new ParserSaxHandler();
		try {
			saxParser.parse(path, handler);
		} catch (SAXException | IOException e) {
			throw new ParserXmlException("Exception in ParserSax class " + e);
		}
		return builder.getAll();
	}

	/**
	 * The Class ParserSaxHandler.
	 */
	private class ParserSaxHandler extends DefaultHandler {

		/** The element. */
		private String element = null;
		private String itemClassName = null;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {

			List<String> fieldsName = builder.getFieldsName();
			List<String> parsedItemName = builder.getParsedItemName();

			this.element = qName;
			parsedItemName.stream().filter(qName::equalsIgnoreCase).forEach(itemName -> {
				builder.createItem(itemName);
				itemClassName = itemName;

				fieldsName.stream().filter(fieldName -> attributes.getValue(fieldName) != null)
						.forEach(fieldName -> builder.fillItem(fieldName, attributes.getValue(fieldName)));
			});
		}

		@Override
		public void endElement(String uri, String localName, String qName) {

			List<String> parsedItemName = builder.getParsedItemName();

			parsedItemName.stream().filter(qName::equalsIgnoreCase).forEach(itemName -> {
				builder.completeItem();
				itemClassName = null;
			});

		}

		@Override
		public void characters(char ch[], int start, int length) {

			List<String> fieldsName = builder.getFieldsName();
			String fieldValue = new String(ch, start, length);

			fieldsName.stream().filter(fieldName -> this.element != null && this.element.equals(fieldName))
					.forEach(fieldName -> {

						if (itemClassName != null) {
							builder.fillItem(fieldName, fieldValue);
							this.element = null;

						}
					});
		}
	}

}
