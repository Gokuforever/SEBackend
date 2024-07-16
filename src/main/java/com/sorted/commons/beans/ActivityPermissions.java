package com.sorted.commons.beans;

import com.sorted.commons.enums.Permissions;

import lombok.Data;

@Data
public class ActivityPermissions {

	private Permissions permission;
	private Integer permissions_id;


	public void setPermission(Permissions permission) {
		this.permission = permission;
		this.permissions_id = permission.getId();
	}

}
