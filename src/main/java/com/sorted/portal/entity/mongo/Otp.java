package com.sorted.portal.entity.mongo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.portal.beans.Attempt_Details;
import com.sorted.portal.enums.ProcessType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Document(collection = "otp")
public class Otp extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String otp_value;
	private Boolean is_verified;
	private Boolean status;
	private Integer max_attempt;
	private Integer verification_attempt;
	private Integer resend_attempt;
	private String mobile_no;
	private String email_id;
	private String entity_id;
	private LocalDateTime expiry_time;
	private LocalDateTime verification_time;
	private List<Attempt_Details> attempt_details;
	private ProcessType process_type;
}
