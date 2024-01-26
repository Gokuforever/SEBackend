package com.example.test.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@MappedSuperclass
public abstract class BaseMySqlEntity implements Serializable, Cloneable {

	/**
	 * This is a base class for entity
	 */

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "created_by", updatable = false)
	private String created_by;
	@Column(name = "modified_by")
	private String modified_by;
	@CreationTimestamp
	@Column(name = "creation_date", nullable = false, updatable = false)
	private LocalDateTime creation_date;
	@UpdateTimestamp
	@Column(name = "modification_date")
	private LocalDateTime modification_date;
	@ToString.Include
	private String creation_date_str;
	@ToString.Include
	private String modification_date_str;
	@ToString.Include
	private boolean deleted = false;

	@JsonIgnore
	public Object getClone() throws CloneNotSupportedException {
		return super.clone();
	}

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
