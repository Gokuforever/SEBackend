package com.example.test.repository;

import org.springframework.stereotype.Repository;

import com.example.test.entity.Users;
import com.example.test.helper.BaseMySqlRepository;

@Repository
public interface UsersRepository extends BaseMySqlRepository<Integer, Users> {

}
