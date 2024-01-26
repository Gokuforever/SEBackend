package com.example.test.entity.service;

import org.springframework.stereotype.Service;

import com.example.test.entity.Payout_Details;
import com.example.test.repository.PayoutDetailsRepository;

@Service
public class PayoutDetailsService extends GenericEntityServiceImpl<Integer, Payout_Details, PayoutDetailsRepository> {

	@Override
	protected Class<PayoutDetailsRepository> getRepoClass() {
		return PayoutDetailsRepository.class;
	}

//	// @Autowired
//	public PayoutDetailsService(PayoutDetailsRepository payoutDetailsRepository) {
//		super(payoutDetailsRepository);
//	}

}
