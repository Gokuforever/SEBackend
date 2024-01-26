package com.example.test.helper;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public abstract class BaseEntity<K> implements Serializable, Cloneable {

	/**
	 * This is a base class for entity
	 */
	private static final long serialVersionUID = 1L;
	private K id;
	private String created_by;
	private String modified_by;
	private LocalDateTime creation_date;
	private LocalDateTime modification_date;
	private boolean deleted = false;
	
	@JsonIgnore
	public Object getClone() throws CloneNotSupportedException  {
		return super.clone();
	}

}
