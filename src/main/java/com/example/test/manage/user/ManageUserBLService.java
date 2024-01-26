package com.example.test.manage.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.entity.Payout_Details;
import com.example.test.entity.service.PayoutDetailsService;
import com.example.test.helper.AggregationFilter.QueryFilter;
import com.example.test.helper.AggregationFilter.QueryFilterType;
import com.example.test.helper.AggregationFilter.WhereClause;
import com.example.test.helper.Response;

@RestController
//@RequestMapping("/user")
public class ManageUserBLService {

	@Autowired
	private PayoutDetailsService payoutDetailsService;

	@PostMapping("/test")
	public String test() {
		return "Working";
	}

	@PostMapping("/login")
	public Response login() {

//		LoginReqBean req = request.getGenericRequestDataObject(LoginReqBean.class);
//
//		if (!StringUtils.hasText(req.getMobileNo())) {
//			// throw exception
//		}
//		if (!StringUtils.hasText(req.getPassword())) {
//			// throw exception
//		}

		QueryFilter qf = new QueryFilter(QueryFilterType.AND);
		qf.addClause(WhereClause.eq(Payout_Details.Fields.account_number, "50100214445016"));
//		qf.addClause(WhereClause.eq(Payout_Details.Fields.password, "Goku#5132"));

		List<Payout_Details> listU = payoutDetailsService.repoFind(qf);
		System.out.println(listU);

		return null;

	}

}
