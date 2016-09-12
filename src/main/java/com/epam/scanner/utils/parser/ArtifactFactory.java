package com.epam.scanner.utils.parser;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.scanner.domain.Artifact;
import com.epam.scanner.utils.parser.enums.ParsedType;

public class ArtifactFactory {

	private static final Logger LOGGER = LogManager.getLogger(ArtifactFactory.class);

	public Artifact create(ParsedType type) {

		Artifact artifact = null;

		switch (type) {

		case DEPENDENCY:
			artifact = new Artifact();
			break;

		default:
			LOGGER.log(Level.ERROR, "Object " + type + " is not created.");
		}
		return artifact;
	}
}