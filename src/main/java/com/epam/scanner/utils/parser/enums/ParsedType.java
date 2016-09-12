package com.epam.scanner.utils.parser.enums;

public enum ParsedType {

		DEPENDENCY("dependency");

		private String text;


		ParsedType(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

		public static ParsedType fromString(String text) {
			ParsedType value = null;
			for (ParsedType enumeration : ParsedType.values()) {
				if (text.equalsIgnoreCase(enumeration.text)) {
					value = enumeration;
				}

			}
			return value;
		}
	}