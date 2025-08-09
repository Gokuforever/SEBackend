package com.sorted.commons.beans;

import com.sorted.commons.enums.Permission;
import lombok.Data;

@Data
public class ActivityPermissions {

	private Permission permission;
	private Integer permissions_id;

	public void setPermission(Permission permission) {
		this.permission = permission;
		this.permissions_id = permission.getId();
	}

}
