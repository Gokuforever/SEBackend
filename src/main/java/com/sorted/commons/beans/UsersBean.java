package com.sorted.commons.beans;

import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.entity.mongo.Seller;
import com.sorted.commons.entity.mongo.Users;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersBean extends Users {
	@Serial
	private static final long serialVersionUID = 1L;
	private Role role;
	private String token;
	private Seller seller;
	private String refresh_token;

}
