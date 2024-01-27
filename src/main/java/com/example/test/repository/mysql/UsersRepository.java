package com.example.test.repository.mysql;

import org.springframework.stereotype.Repository;

import com.example.test.entity.mysql.Users;
import com.example.test.helper.BaseMySqlRepository;

@Repository
public interface UsersRepository extends BaseMySqlRepository<Long, Users> {

}
