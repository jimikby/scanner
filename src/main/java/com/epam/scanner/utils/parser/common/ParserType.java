package com.epam.scanner.utils.parser.common;

public enum ParserType {

	STAX("stax"), DOM("dom"), SAX("sax");

	private String text;

	 ParserType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static ParserType fromString(String text) {
		ParserType value = null;
		for (ParserType enumeration : ParserType.values()) {
			if (text.equalsIgnoreCase(enumeration.text)) {
				value = enumeration;
			}
		}
		return value;
	}
}
