package com.sorted.commons.beans;

import java.util.List;

import com.sorted.commons.enums.Activity;

import lombok.Data;

@Data
public class Role_Activity_Permissions {

	private Integer activity_id;
	private String activity_name;
	private List<Integer> permissions;

	public void setActivity(Activity activity) {
		this.activity_name = activity.getName();
		this.activity_id = activity.getId();
	}

}
