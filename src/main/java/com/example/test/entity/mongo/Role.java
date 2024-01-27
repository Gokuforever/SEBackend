package com.example.test.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "role")
public class Role extends BaseMongoEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer status;
	

}
