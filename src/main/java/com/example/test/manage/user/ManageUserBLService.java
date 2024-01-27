package com.example.test.manage.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.entity.mongo.Role;
import com.example.test.entity.mysql.Users;
import com.example.test.entity.service.RoleService;
import com.example.test.entity.service.UsersService;
import com.example.test.helper.AggregationFilter.QueryFilter;
import com.example.test.helper.AggregationFilter.QueryFilterType;
import com.example.test.helper.AggregationFilter.WhereClause;

@RestController
public class ManageUserBLService {

	@Autowired
	private RoleService roleService;
	
	
	@Autowired
	private UsersService usersService;

	@PostMapping("/test")
	public String test() {
		return "Working";
	}

	@PostMapping("/create/user")
	public void create() {
		
		Users users = new Users();
		users.setName("yogesh");
		
		usersService.create(users, "test");
		
		List<Users> repoFindAll = usersService.repoFindAll();
		
		System.out.println(repoFindAll);
		Role role = new Role();
		role.setName("admin");

		Role role2 = roleService.create(role, "test");
		
		QueryFilter filter = new QueryFilter(QueryFilterType.AND);
		filter.addClause(WhereClause.eq("id", role2.getId()));
		
		List<Role> repoFind = roleService.repoFind(filter);
		System.out.println(repoFind);
	}

}
