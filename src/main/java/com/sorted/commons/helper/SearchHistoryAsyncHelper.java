package com.sorted.commons.helper;

import com.sorted.commons.entity.mongo.Search_History;
import com.sorted.commons.entity.service.Search_History_Service;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SearchHistoryAsyncHelper {

	@Autowired
	private Search_History_Service search_History_Service;

	@Async
	public void createSearchHistory(String user_id, int user_type_id, SEFilter filterSE) {

		Search_History history = new Search_History();
		history.setFilter(filterSE);
		history.setUser_id(user_id);
		history.setUser_type_id(user_type_id);

		search_History_Service.create(history, user_id);
	}
}
