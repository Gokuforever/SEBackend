package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

	SUCCESSFUL("SE_0000", "Successful.", "Successful."),
	ERR_0001("SE_0001", "Something went wrong.", "Something went wrong."),
	INVALID_REQ("SE_0002", "Invalid request.", "Invalid request."),
	MANDATE_CATAGORY("SE_0003", "Category name is mandatory.", "Category name is mandatory."),
	MANDATE_SUB_CATAGORY("SE_0004", "Sub category name is missing.", "Sub category name is missing."),
	INVALID_SUB_CATAGORY("SE_0005", "Invalid sub category.", "Invalid sub category."),
	INVALID_MIN_SUB_CATAGORY("SE_0006", "At least one value is required for sub category.", "At least one value is required for sub category."),
	INVALID_SUB_CATAGORY_VAL("SE_0007", "Invalid sub category value.", "Invalid sub category value"),
	INVALID_CATAGORY_SELECTED("SE_0008", "Invalid catagory selected.", "Invalid catagory selected."),
	MISSING_PRODUCT_NAME("SE_0009", "Product name is missing.", "Product name is missing."),
	INVALID_PRODUCT_NAME("SE_0010", "Invalid product name.", "Invalid product name."),
	MISSING_PRODUCT_PRICE("SE_0011", "Price is missing.", "Price is missing."),
	INVALID_PRODUCT_PRICE("SE_0012", "Invalid product price.", "Invalid product price."),
	MISSING_QUANTITY("SE_0013", "Quantity is missing.", "Quantity is missing."),
	INVALID_QUANTITY("SE_0014", "Invalid quantity.", "Invalid quantity."),
	CATALORY_NOT_SELECTED("SE_0015", "Catagory not selected.", "Catagory not selected."),
	CATALORY_NOT_FOUND("SE_0015", "Selected catagory not found.", "Selected catagory not found."),
	EMPTY_SUB_CATAGORY("SE_0016", "Select atleast 1 sub-catagory.", "Select atleast 1 sub-catagory."),
	EMPTY_ATTRIBUTE("SE_0017", "Attribute is missing.", "Attribute is missing."),
	INVALID_ATTRIBUTE("SE_0018", "Invalid attribute selected.", "Invalid attribute selected."),
	MISSING_PRODUCT_DESCRIPTION("SE_0019", "Product description is missing.", "Product description is missing."),
	INVALID_PRODUCT_DESCRIPTION("SE_0020", "Product description is invalid.", "Product description is invalid."),
	MISSING_ID("SE_0021", "Entity id is missing for update.", "Something went wrong."),
	NO_RECORD("SE_0022", "No record found.", "No record found."),
	NOT_A_LIST("SE_0023", "List is required.", "Something went wrong."),
	INVALID_CATAGORY("SE_000", "Invalid catagory name.", "Invalid catagory name.");

	private final String code;
	private final String errorMessage;
	private final String userMessage;
}
