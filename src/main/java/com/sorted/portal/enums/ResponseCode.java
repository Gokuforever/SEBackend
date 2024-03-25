package com.sorted.portal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

	SUCCESSFUL("SE_0000", "Successful.", "Successful."),
	ERR_0001("SE_0001", "Something went wrong.", "Something went wrong."),
	INVALID_REQ("SE_0002", "Invalid request.", "Invalid request."),
	MANDATE_CATEGORY("SE_0003", "Category name is mandatory.", "Category name is mandatory."),
	MANDATE_SUB_CATEGORY("SE_0004", "Sub category name is missing.", "Sub category name is missing."),
	INVALID_SUB_CATEGORY("SE_0005", "Invalid sub category.", "Invalid sub category."),
	INVALID_MIN_SUB_CATEGORY("SE_0006", "At least one value is required for sub category.",
			"At least one value is required for sub category."),
	INVALID_SUB_CATEGORY_VAL("SE_0007", "Invalid sub category value.", "Invalid sub category value"),
	INVALID_CATEGORY_SELECTED("SE_0008", "Invalid category selected.", "Invalid category selected."),
	MISSING_PRODUCT_NAME("SE_0009", "Product name is missing.", "Product name is missing."),
	INVALID_PRODUCT_NAME("SE_0010", "Invalid product name.", "Invalid product name."),
	MISSING_PRODUCT_PRICE("SE_0011", "Price is missing.", "Price is missing."),
	INVALID_PRODUCT_PRICE("SE_0012", "Invalid product price.", "Invalid product price."),
	MISSING_QUANTITY("SE_0013", "Quantity is missing.", "Quantity is missing."),
	INVALID_QUANTITY("SE_0014", "Invalid quantity.", "Invalid quantity."),
	CATALORY_NOT_SELECTED("SE_0015", "Category not selected.", "Category not selected."),
	CATALORY_NOT_FOUND("SE_0015", "Selected category not found.", "Selected category not found."),
	EMPTY_SUB_CATEGORY("SE_0016", "Select atleast 1 sub-category.", "Select atleast 1 sub-category."),
	EMPTY_ATTRIBUTE("SE_0017", "Attribute is missing.", "Attribute is missing."),
	INVALID_ATTRIBUTE("SE_0018", "Invalid attribute selected.", "Invalid attribute selected."),
	MISSING_PRODUCT_DESCRIPTION("SE_0019", "Product description is missing.", "Product description is missing."),
	INVALID_PRODUCT_DESCRIPTION("SE_0020", "Product description is invalid.", "Product description is invalid."),
	MISSING_ID("SE_0021", "Entity id is missing for update.", "Something went wrong."),
	NO_RECORD("SE_0022", "No record found.", "No record found."),
	NOT_A_LIST("SE_0023", "List is required.", "Something went wrong."),
	MISSING_USER_ID("SE_0024", "User id is missing.", "Requester details missing."),
	MISSING_ROLE_ID("SE_0025", "Role id is missing.", "Requester details missing."),
	MISSING_PRODUCT_ID("SE_0026", "Product id is missing.", "Product id is missing."),
	PRODUCT_NOT_FOUND("SE_0026", "Product not found.", "Product not found."),
	PRODUCT_OUT_OF_STOCK("SE_0027", "This product out of stock.", "This product out of stock."),
	STOCK_LIMIT_REACHED("SE_0028", "Stock limit reached. Additional quantities cannot be added at this time.",
			"Stock limit reached. Additional quantities cannot be added at this time."),
	MISSING_PRODUCT_QUANTITY("SE_0026", "Product quantity is missing.", "Product quantity is missing."),
	INVAALID_PRODUCT_QUANTITY("SE_0026", "Product quantity is invalid.", "Product quantity is invalid."),
	CART_NOT_FOUND("SE_0026", "First crete a cart for this user by adding an item.", "First crete a cart for this user by adding an item."),
	INVALID_CATEGORY("SE_000", "Invalid category name.", "Invalid category name.");

	private final String code;
	private final String errorMessage;
	private final String userMessage;
}
