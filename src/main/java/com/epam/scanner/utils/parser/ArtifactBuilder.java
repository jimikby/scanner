package com.epam.scanner.utils.parser;

import java.util.ArrayList;
import java.util.List;

import com.epam.scanner.domain.Artifact;
import com.epam.scanner.utils.parser.common.ParsedItem;
import com.epam.scanner.utils.parser.common.ParserBuilder;
import com.epam.scanner.utils.parser.enums.FieldEnum;
import com.epam.scanner.utils.parser.enums.ParsedType;

public class ArtifactBuilder implements ParserBuilder {

	private ParsedItem artifact;
	private List<ParsedItem> artifacts = new ArrayList<>();
	private List<ParsedType> parsedType = new ArrayList<>();
	
	public ArtifactBuilder add(ParsedType device) {
		parsedType.add(device);
		return this;
	}
	

	@Override
	public boolean checkForNewItem(String text) {
		boolean value = false;
		for (ParsedType type : parsedType) {
			if (text.equalsIgnoreCase(type.getText())) {
				value = true;
			}
		}
		return value;
	}

	@Override
	public boolean checkField(String text) {
		boolean value = false;
		for (FieldEnum fieldEnum : FieldEnum.values()) {
			if (text.equalsIgnoreCase(fieldEnum.getText())) {
				value = true;
			}
		}
		return value;
	}

	@Override
	public boolean createItem(String text) {
		ArtifactFactory devFactory = new ArtifactFactory();
		boolean value = false;
		for (ParsedType type : parsedType) {
			if (text.equalsIgnoreCase(type.getText())) {
				this.artifact = devFactory.create(type);
				value = true;
			}
		}
		return value;
	}

	@Override
	public void completeItem() {
		artifacts.add(this.artifact);
	}

	public List<ParsedItem> getAll() {
		List<ParsedItem> newDevices = this.artifacts;
		clear();
		return newDevices;
	}

	@Override
	public void clear() {
		this.artifact = null;
		this.artifacts = new ArrayList<>();
	}

	@Override
	public List<String> getFieldsName() {
		List<String> fieldsName = new ArrayList<>();
		for (FieldEnum fieldEnum : FieldEnum.values()) {
			fieldsName.add(fieldEnum.getText());
		}
		return fieldsName;
	}

	@Override
	public List<String> getParsedItemName() {
		List<String> getParsedItemName = new ArrayList<>();
		for (ParsedType type : parsedType) {
			getParsedItemName.add(type.getText());
		}
		return getParsedItemName;
	}

	@Override
	public boolean fillItem(String fieldName, String fieldValue) {
		boolean value = false;

		if (artifact instanceof Artifact) {

			switch (FieldEnum.fromString(fieldName)) {
			case GROUP_ID:
				((Artifact) artifact).setGroupId(fieldValue);
				break;
			case ARTIFACT_ID:
				((Artifact) artifact).setArtifactId(fieldValue);
				break;
			case VERSION:
				((Artifact) artifact).setVersion(fieldValue.replaceAll("[${}]",""));
				break;
			case EXCLUSIONS:
				if (fieldValue != null) {
					((Artifact) artifact).setExclude(true);
				} else {
					((Artifact) artifact).setExclude(false);
				}
				break;
			}
		}

		return value;
	}
}
