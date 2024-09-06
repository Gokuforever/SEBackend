package com.sorted.commons.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.commons.beans.Bank_Details;
import com.sorted.commons.beans.Spoc_Details;
import com.sorted.commons.enums.All_Status.Seller_Status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "seller")
public class Seller extends BaseMongoEntity<String> {

	private static final long serialVersionUID = 7105004513020596023L;

	private String code;
	private String business_name;
//	private AddressDTO business_address;
	private List<Spoc_Details> spoc_details;
	private List<String> serviceable_pincodes;
	private String company_pan;
	private Bank_Details bank_details;
	private Seller_Status status = Seller_Status.VERIFICATION_PENDING;

	// Not In Use
	private String cin;
	private String gstin;
	private String business_type;

}
