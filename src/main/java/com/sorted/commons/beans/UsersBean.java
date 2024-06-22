package com.sorted.commons.beans;

import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.entity.mongo.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersBean extends Users {
	private static final long serialVersionUID = 1L;
	private Role role;
	private String token;

}
