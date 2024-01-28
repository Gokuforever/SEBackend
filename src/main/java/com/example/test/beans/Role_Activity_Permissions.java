package com.example.test.beans;

import java.util.List;

import com.example.test.enums.Activity;

public class Role_Activity_Permissions {

	private Integer activity_id;

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		this.activity_id = activity.getId();
	}

	public List<ActivityPermissions> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<ActivityPermissions> permissions) {
		this.permissions = permissions;
	}

	public Integer getActivity_id() {
		return activity_id;
	}

	private Activity activity;
	private List<ActivityPermissions> permissions;

}
