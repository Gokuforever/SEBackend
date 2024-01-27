package com.example.test.entity.service;

import org.springframework.stereotype.Service;

import com.example.test.entity.mysql.Users;
import com.example.test.repository.mysql.UsersRepository;

@Service
public class UsersService extends GenericEntityServiceImpl<Long, Users, UsersRepository> {

	@Override
	protected Class<UsersRepository> getRepoClass() {
		return UsersRepository.class;
	}

}
