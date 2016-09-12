package com.epam.scanner.utils.parser.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.epam.scanner.exception.ParserXmlException;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.*;

/**
 * The Class ParserStax.
 */
public class ParserStax implements ParcerXml {


	@Override
	public List<ParsedItem> create(File path, ParserBuilder builder) throws ParserXmlException {
		XMLInputFactory factory = XMLInputFactory.newFactory();

		try {
			XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(path));
			boolean startElementFound = false;
			boolean value = false;
			String lastLocalName = null;

			while (reader.hasNext()) {
				int res = reader.next();

				switch (res) {

				case START_ELEMENT:

					if (builder.checkForNewItem(reader.getLocalName())) {
						builder.createItem(reader.getLocalName());
						startElementFound = true;
					} else {
						if (builder.checkField(reader.getLocalName())) {
							lastLocalName = reader.getLocalName();
							value = true;
						}
					}
					if (startElementFound) {
						for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
							builder.fillItem(reader.getAttributeName(i).toString(), reader.getAttributeValue(i));
						}
					}
					break;

				case CHARACTERS:
					if (startElementFound && !!value) {
						builder.fillItem(lastLocalName, reader.getText());
					}
					value = false;
					break;

				case END_ELEMENT:
					if (builder.checkForNewItem(reader.getLocalName())) {
						builder.completeItem();
						startElementFound = false;
					}
					break;
				}
			}
			reader.close();

		} catch (FileNotFoundException | XMLStreamException e) {
			throw new ParserXmlException("Exception in ParserStax class " + e);
		}
		return builder.getAll();
	}
}