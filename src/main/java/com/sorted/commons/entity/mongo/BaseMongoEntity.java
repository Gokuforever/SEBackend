package com.sorted.commons.entity.mongo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public abstract class BaseMongoEntity<K> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Setter(AccessLevel.PRIVATE)
	private K id;
	@Setter(AccessLevel.PRIVATE)
	private String created_by;
	@Setter(AccessLevel.PRIVATE)
	private String modified_by;
	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime creation_date;
	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime modification_date;
	@Setter(AccessLevel.PRIVATE)
	private String creation_date_str;
	@Setter(AccessLevel.PRIVATE)
	private String modification_date_str;
	private boolean deleted = false;

	public void setBeforeCreate(@NonNull String cudby) {
		this.created_by = cudby;
		this.modified_by = cudby;
		LocalDateTime curDate = LocalDateTime.now();
		this.creation_date = curDate;
		this.modification_date = curDate;

		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String curDateStr = curDate.format(pattern);
		this.creation_date_str = curDateStr;
		this.modification_date_str = curDateStr;
	}

	public void setBeforeModification(@NonNull String cudby) {
		this.modified_by = cudby;
		LocalDateTime curDate = LocalDateTime.now();
		this.modification_date = curDate;

		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String curDateStr = curDate.format(pattern);
		this.modification_date_str = curDateStr;
	}

}
