package com.sorted.portal.entity.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sorted.portal.beans.Role_Activity_Permissions;
import com.sorted.portal.enums.All_Status.Role_Status;

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
	private String code;
	private String name;
	private Integer status;
	private List<Role_Activity_Permissions> activity_Permissions;

	public void setStatus(Role_Status status) {
		this.status = status.getId();
	}

}
