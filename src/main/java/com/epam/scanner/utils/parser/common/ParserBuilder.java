package com.epam.scanner.utils.parser.common;

import java.util.List;

/**
 * The Interface ParserServce.
 */
public interface ParserBuilder {

	/**
	 * Check for new item.
	 *
	 * @param text
	 *            the text
	 * @return true, if successful
	 */
	boolean checkForNewItem(String text);

	/**
	 * Check field.
	 *
	 * @param text
	 *            the text
	 * @return true, if successful
	 */
	boolean checkField(String text);

	/**
	 * Creates the item.
	 *
	 * @param text
	 *            the text
	 * @return true, if successful
	 */
	boolean createItem(String text);

	/**
	 * Fill item.
	 *
	 * @param fieldName
	 *            the field name
	 * @param fieldValue
	 *            the field value
	 * @return true, if successful
	 */
	boolean fillItem(String fieldName, String fieldValue);

	/**
	 * Complete item.
	 */
	void completeItem();

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	List<ParsedItem> getAll();

	/**
	 * Clear.
	 */
	void clear();

	/**
	 * Gets the fields name.
	 *
	 * @return the fields name
	 */
	List<String> getFieldsName();

	/**
	 * Gets the parsed item name.
	 *
	 * @return the parsed item name
	 */
	List<String> getParsedItemName();

}
