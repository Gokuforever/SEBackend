package com.example.test.manage.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageUserBLService {

	@PostMapping("/test")
	public String test() {
		return "Working";
	}

}
