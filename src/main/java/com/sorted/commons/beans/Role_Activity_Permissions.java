package com.sorted.commons.beans;

import com.sorted.commons.enums.Activity;
import lombok.Data;

import java.util.List;

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
