package com.epam.scanner.utils.parser.enums;

public enum FieldEnum {

	GROUP_ID("groupId"), //
	ARTIFACT_ID("artifactId"), //
	VERSION("version"), //
	EXCLUSIONS("exclusions");

	private String text;

	FieldEnum(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static FieldEnum fromString(String text) {
		FieldEnum value = null;
		for (FieldEnum enumeration : FieldEnum.values()) {
			if (text.equalsIgnoreCase(enumeration.text)) {
				value = enumeration;
			}
		}
		return value;
	}
}