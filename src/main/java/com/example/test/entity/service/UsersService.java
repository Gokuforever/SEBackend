package com.example.test.entity.service;

import org.springframework.stereotype.Service;

import com.example.test.entity.Users;
import com.example.test.repository.UsersRepository;

@Service
public class UsersService extends GenericEntityServiceImpl<Integer, Users, UsersRepository> {

	@Override
	protected Class<UsersRepository> getRepoClass() {
		return UsersRepository.class;
	}

}
