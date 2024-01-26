package com.example.test.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payout_details")
@FieldNameConstants
@Entity
public class Payout_Details implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String created_by;
	@Column(name = "txn_InitiateDate")
	private LocalDateTime creation_date;
	@ToString.Include
	private String modified_by;
	private LocalDateTime modification_date;
	private String creation_date_str;
	@ToString.Include
	private String modification_date_str;
	@ToString.Include
	private boolean deleted = false;

	@Column(length = 125, name = "accountHolderName")
	private String account_holder_name;
	@Column(length = 55, name = "accountNo")
	private String account_number;
	@Column(length = 20, name = "ifscCode")
	private String ifsc;
	@Column(length = 20, name = "mobileNo")
	private String mobile_number;
	@Column(length = 50, name = "txnType")
	private String txn_type;
	@Column(length = 55, name = "payoutId")
	private String payout_id;
	@Column(length = 105)
	private String email_id;
	@Column(length = 55, name = "orderRefNo")
	private String me_order_ref_number;
	@Column(length = 55, name = "accountType")
	private String debit_account;
	@Column(name = "txnAmount")
	private Long amount;// in paisa

	@Column(length = 360)
	private String transaction_narration;
	@Column(length = 360, name = "remark")
	private String remarks;
	@Column(length = 360)
	private String reversal_remark;
	@Column(length = 105, name = "beneficieryVPA")
	private String upi_address;
	// TODO: is this considered in phase 1
	private LocalDateTime link_expiry_date;
	@Column(name = "txn_ScheduledDate")
	private LocalDateTime schedule_payout_date;
	@Column(length = 55)
	private String ag_id;
	@Column(length = 55, name = "aggregatorId")
	private String ag_code;
	@Column(length = 150, name = "aggName")
	private String ag_name;
	@Column(length = 55)
	private String me_id;
	@Column(length = 55, name = "customerId")
	private String me_code;
	@Column(length = 150, name = "customerName")
	private String me_name;
	@Column(length = 55)
	private String re_id;
	@Column(length = 55)
	private String re_code;
	@Column(length = 150)
	private String re_name;
	private Integer source;
	@Column(name = "txnStatus")
	private String status;
	private Integer status_id;
	private String sub_status;

	// newly added.. check for the new fields
	@Column(length = 80, name = "bankName")
	private String bank_name;
	@Column(length = 55, name = "bankRefNo")
	private String bank_ref_no; // UTR
	@Column(length = 55, name = "bankTransactionRefNo")
	private String bank_txn_ref_no;
	@Column(length = 55, name = "statusCode")
	private String status_code;
	@Column(length = 200, name = "statusDesc")
	private String status_desc;
	@Column(length = 250, name = "bank_responseCode")
	private String bank_response_code;
	@Column(length = 55, name = "spRefNo")
	private String spk_ref_no;
	@Column(name = "txnDate")
	private LocalDateTime txn_date;
	@Column(length = 55)
	private String requester_ip;

	@Column(length = 55, name = "beneficiaryId")
	private String bene_id;

	@Column(length = 20, name = "commission")
	private Long commission;

	@Column(length = 20, name = "gst")
	private Long gst;

	@Column(length = 1, name = "invoice")
	private String invoice;

	@Column(length = 150, name = "commissionId")
	private String commissionId;

	private String bank_status;
	// for commission calculation
	private String is_processed = "NO";

	private Long fee = 0L;
	private Long income = 0L;

	private Long ag_payable = 0L;
	private Long pg_payable = 0L;
	private Long re_payable = 0L;

	private Long me_igst = 0L;
	private Long me_cgst = 0L;
	private Long me_sgst = 0L;
}
