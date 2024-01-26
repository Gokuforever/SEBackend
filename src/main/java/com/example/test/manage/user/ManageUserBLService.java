package com.example.test.manage.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.entity.mysql.Users;

@RestController
public class ManageUserBLService {

	@PostMapping("/test")
	public String test() {
		return "Working";
	}
	
	@PostMapping("/create/user")
	public void create(){
		Users users = new Users();
		users.setName("Yogesh");
	}

}
