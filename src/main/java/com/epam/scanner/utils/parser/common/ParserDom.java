package com.epam.scanner.utils.parser.common;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.epam.scanner.exception.ParserXmlException;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The Class ParserDom.
 */
public class ParserDom implements ParcerXml {

	@Override
	public List<ParsedItem> create(File xmlFile, ParserBuilder builder) throws ParserXmlException {

		try {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(xmlFile);

			doc.getDocumentElement().normalize();

			List<String> fieldsName = builder.getFieldsName();
			List<String> parsedItemName = builder.getParsedItemName();
			String elementTextContent = null;

			for (String itemName : parsedItemName) {

				NodeList nList = doc.getElementsByTagName(itemName);
				for (int temp = 0; temp < nList.getLength(); temp++) {
					builder.createItem(itemName);
					Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						for (String fieldName : fieldsName) {

							if (!eElement.getAttribute(fieldName).equals("")) {
								builder.fillItem(fieldName, eElement.getAttribute(fieldName));
							}

							if (eElement.getElementsByTagName(fieldName).item(0) != null) {
								elementTextContent = eElement.getElementsByTagName(fieldName).item(0).getTextContent();
								builder.fillItem(fieldName, elementTextContent);
							}
						}
					}
					builder.completeItem();
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new ParserXmlException("Exception in ParserDom class " + e);
		}
		return builder.getAll();
	}
}