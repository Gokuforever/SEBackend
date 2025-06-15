package com.sorted.commons.utils;

import com.sorted.commons.beans.AddressDTO;
import com.sorted.commons.beans.Bank_Details;
import com.sorted.commons.entity.mongo.Address;
import com.sorted.commons.enums.AddressType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public class ValidationUtil {

    public static Address validateAddress(AddressDTO address, Address address2) {
        String street_1 = address.getStreet_1();
        String street_2 = address.getStreet_2();
        String landmark = address.getLandmark();
        String city = address.getCity();
        String state = address.getState();
        String pincode = address.getPincode();
        String phoneNo = address.getPhone_no();
        BigDecimal lat = address.getLat();
        BigDecimal lng = address.getLng();
        String address_type_desc = address.getAddress_type_desc();

        if (!StringUtils.hasText(street_1)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_STREET);
        }
        street_1 = trimAndValidateLength(street_1, ResponseCode.INVALID_STREET);
        if (StringUtils.hasText(street_2)) {
            street_2 = trimAndValidateLength(street_2, ResponseCode.INVALID_STREET_2);
        }
        if (!StringUtils.hasText(landmark)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_LANDMARK);
        }
        landmark = trimAndValidateLength(landmark, ResponseCode.INVALID_LANDMARK);
        if (!StringUtils.hasText(city)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_CITY);
        }
        city = trimAndValidateLength(city, ResponseCode.INVALID_CITY);
        if (!StringUtils.hasText(state)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_STATE);
        }
        state = trimAndValidateLength(state, ResponseCode.INVALID_STATE);
        if (!StringUtils.hasText(pincode)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_PINCODE);
        }
        if (!SERegExpUtils.isPincode(pincode)) {
            throw new CustomIllegalArgumentsException(ResponseCode.INVALID_PINCODE);
        }
        AddressType address_type = AddressType.getByName(address.getAddress_type());
        if (address_type == null) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_ADDRESS_TYPE);
        }
        if (address_type!=AddressType.STORE && !StringUtils.hasText(phoneNo)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_PHONE);
        }
        if (address_type!=AddressType.STORE && !SERegExpUtils.isMobileNo(phoneNo)) {
            throw new CustomIllegalArgumentsException(ResponseCode.INVALID_PHONE);
        }
        if (!StringUtils.hasText(address.getAddress_type())) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_ADDRESS_TYPE);
        }
        if (address_type == AddressType.OTHER) {
            if (!StringUtils.hasText(address_type_desc)) {
                throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_OTHER_ADDRESS_DESC);
            }
            if (!SERegExpUtils.standardTextValidation(address_type_desc)) {
                throw new CustomIllegalArgumentsException(ResponseCode.INVALID_OTHER_ADDRESS_DESC);
            }
        } else {
            address_type_desc = null;
        }

        address2.setStreet_1(street_1);
        address2.setStreet_2(street_2);
        address2.setLandmark(landmark);
        address2.setCity(city);
        address2.setState(state);
        address2.setPincode(pincode);
        address2.setAddress_type(address_type);
        address2.setAddress_type_desc(address_type_desc);
        address2.setLat(lat);
        address2.setLng(lng);
        return address2;
    }

    @NotNull
    private static String trimAndValidateLength(String city, ResponseCode responseCode) {
        city = city.trim().replaceAll("\\s+", " ");
        if (city.length() > 100) {
            throw new CustomIllegalArgumentsException(responseCode);
        }
        return city;
    }

    public static void validateBankDetails(Bank_Details bank_details) {
        String account_number = bank_details.getAccount_number();
        String ifsc_code = bank_details.getIfsc_code();
        String branch_name = bank_details.getBranch_name();
        String bank_name = bank_details.getBank_name();
        if (!StringUtils.hasText(account_number)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_ACC_NO);
        }
        // TODO: validate account number
        if (!StringUtils.hasText(ifsc_code)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_IFSC);
        }
        // TODO: validate IFSC code
        if (!StringUtils.hasText(branch_name)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_BRANCH_NAME);
        }
        if (SERegExpUtils.isAlphabeticStringWithSpaces(branch_name)) {
            throw new CustomIllegalArgumentsException(ResponseCode.INVALID_BRANCH_NAME);
        }
        if (!StringUtils.hasText(bank_name)) {
            throw new CustomIllegalArgumentsException(ResponseCode.MANDATE_BANK_NAME);
        }
        if (SERegExpUtils.isAlphabeticStringWithSpaces(bank_name)) {
            throw new CustomIllegalArgumentsException(ResponseCode.INVALID_BANK_NAME);
        }
    }

}
