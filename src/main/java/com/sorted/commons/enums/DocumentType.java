package com.sorted.commons.enums;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentType {
	PRODUCT_IMAGE(1, Arrays.asList(UserType.SUPER_ADMIN, UserType.SELLER)),
	PROFILE_PICTURE(2, Arrays.asList(UserType.SUPER_ADMIN, UserType.GUEST, UserType.CUSTOMER, UserType.SELLER)),
	LOGO(3, Arrays.asList(UserType.SUPER_ADMIN, UserType.SELLER));

	private int id;
	private List<UserType> allowed_to;

}