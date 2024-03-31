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
	PRODUCT_NOT_FOUND("SE_0027", "Product not found.", "Product not found."),
	PRODUCT_OUT_OF_STOCK("SE_0026", "This product out of stock.", "This product out of stock."),
	STOCK_LIMIT_REACHED("SE_0029", "Stock limit reached. Additional quantities cannot be added at this time.",
			"Stock limit reached. Additional quantities cannot be added at this time."),
	MISSING_PRODUCT_QUANTITY("SE_0030", "Product quantity is missing.", "Product quantity is missing."),
	INVAALID_PRODUCT_QUANTITY("SE_0031", "Product quantity is invalid.", "Product quantity is invalid."),
	CART_NOT_FOUND("SE_0032", "First crete a cart for this user by adding an item.", "First crete a cart for this user by adding an item."),
	PASS_VALIDATION_FAILURE_1("SE_0033", "Password must contain atleast one lower case letter", "Password must contain atleast one lower case letter"),
	PASS_VALIDATION_FAILURE_2("SE_0034", "Password must contain atleast one upper case letter", "Password must contain atleast one upper case letter"),
	PASS_VALIDATION_FAILURE_3("SE_0035", "Password must contain atleast one numeric value", "Password must contain atleast one numeric value"),
	PASS_VALIDATION_FAILURE_4("SE_0036", "Password must contain atleast one special character", "Password must contain atleast one special character"),
	PASS_VALIDATION_FAILURE_5("SE_0037", "Password must contain minimum eight characters", "Password must contain minimum eight characters"),
	DUPLICATE_MOBILE("SE_0038", "An account is already created using this mobile no, login or use a different mobile no.", "An account is already created using this mobile no, login or use a different mobile no."),
	DUPLICATE_EMAIL("SE_0039", "This email id is linked to another mobile no.", "This email id is linked to another mobile no."),
	DEFAULT_ROLE_MISSING("SE_0040", "Default role not found.", "Something went wrong."),
	MISSING_OTP("SE_0041", "Please enter otp.", "Please enter otp."),
	INVALID_OTP("SE_0042", "Please enter a valid otp.", "Please enter a valid otp."),
	MISSING_ENTITY("SE_0043", "Entity id is missing.", "Something went wrong."),
	ENTITY_NOT_FOUND("SE_0044", "Entity not found.", "Something went wrong."),
	OTP_EXPIRED("SE_0045", "Otp expired.", "Otp expired."),
	MISSING_FN("SE_0044", "First name is missing.", "First name is missing."),
	MISSING_LN("SE_0045", "Last name is missing.", "Last name is missing."),
	MISSING_MN("SE_0046", "Mobile number is missing.", "Mobile number is missing."),
	MISSING_EI("SE_0047", "Email ID is missing.", "Email ID is missing."),
	MISSING_PASS("SE_0048", "Password is missing.", "Password is missing."),
	INVALID_FN("SE_0049", "Please enter a valid first name.", "Please enter a valid first name."),
	INVALID_LN("SE_0050", "Please enter a valid last name.", "Please enter a valid last name."),
	INVALID_MN("SE_0051", "Please enter a valid mobile number.", "Please enter a valid mobile number."),
	INVALID_EI("SE_0052", "Please enter a valid email ID.", "Please enter a valid first email ID."),
	MISSING_MOBILE("SE_0053", "Mobile no is missing.", "Mobile no is missing."),
	LOGIN_FAILED("SE_0054", "Invalid user name or password.", "Invalid user name or password."),
	USER_BLOCKED("SE_0055", "This account is blocked, please contact customer service.", "This account is blocked, please contact customer service."),
	ROLE_MISSING("SE_0056", "Role not found.", "Something went wrong."),
	USER_NOT_FOUND("SE_0057", "User Not found.", "User Not found."),
	INVALID_CATEGORY("SE_000", "Invalid category name.", "Invalid category name.");

	private final String code;
	private final String errorMessage;
	private final String userMessage;
}
