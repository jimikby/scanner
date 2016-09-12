package com.epam.scanner.utils.parser.common;

import java.io.File;
import java.util.List;

import com.epam.scanner.exception.ParserXmlException;


public interface ParcerXml {

	 List<?> create(File path, ParserBuilder service) throws ParserXmlException;
}
