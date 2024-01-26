package com.example.test.repository;

import org.springframework.stereotype.Repository;

import com.example.test.entity.Payout_Details;
import com.example.test.helper.BaseMySqlRepository;

@Repository
public interface PayoutDetailsRepository extends BaseMySqlRepository<Payout_Details, Integer> {

}
