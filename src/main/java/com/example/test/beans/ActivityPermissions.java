package com.example.test.beans;

import com.example.test.enums.Permissions;

public class ActivityPermissions {

	public Permissions getPermission() {
		return permission;
	}

	public void setPermission(Permissions permission) {
		this.permission = permission;
		this.permissions_id = permission.getId();
	}

	public Integer getPermissions_id() {
		return permissions_id;
	}

	private Permissions permission;
	private Integer permissions_id;

}
