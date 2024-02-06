package com.sorted.portal.manage.user;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sorted.portal.beans.ActivityPermissions;
import com.sorted.portal.beans.Role_Activity_Permissions;
import com.sorted.portal.entity.mongo.Role;
import com.sorted.portal.entity.service.RoleService;
import com.sorted.portal.enums.Activity;
import com.sorted.portal.enums.Permissions;
import com.sorted.portal.enums.All_Status.Role_Status;

@RestController
public class ManageUserBLService {

	@Autowired
	private RoleService roleService;

//	@Autowired
//	private UsersService usersService;

	@PostMapping("/test")
	public String test() {
		return "Working";
	}

	@PostMapping("/create/role")
	public void create() {

//		Users users = new Users();
//		users.setName("yogesh");
//		
//		usersService.create(users, "test");
//		
//		List<Users> repoFindAll = usersService.repoFindAll();
//		
//		System.out.println(repoFindAll);
//		Role role = new Role();
//		role.setName("admin");
//
//		Role role2 = roleService.create(role, "test");
//		
//		QueryFilter filter = new QueryFilter(QueryFilterType.AND);
//		filter.addClause(WhereClause.eq("id", role2.getId()));
//		
//		List<Role> repoFind = roleService.repoFind(filter);
//		System.out.println(repoFind);

		Role role = new Role();
		role.setName("admin");
		role.setStatus(Role_Status.ACTIVE);
		Role_Activity_Permissions activity_Permissions = new Role_Activity_Permissions();
		activity_Permissions.setActivity(Activity.MANAGE_USER);
		ActivityPermissions ap = new ActivityPermissions();
		ap.setPermission(Permissions.EDIT);
		ActivityPermissions ap2 = new ActivityPermissions();
		ap2.setPermission(Permissions.VIEW);

		activity_Permissions.setPermissions(Arrays.asList(ap, ap2));
		role.setActivity_Permissions(Arrays.asList(activity_Permissions));

		Role create = roleService.create(role, "test");

		System.out.println(create);
	}

}
