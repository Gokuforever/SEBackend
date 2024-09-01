package com.sorted.commons.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class Pagination {

	@Value("${se.portal.default.page}")
	private int default_page;

	@Value("${se.portal.default.size}")
	private int default_size;

	private int page;
	private int size;

	public Pagination(int page, int size) {
		if (page < 0 || size <= 0) {
			this.page = default_page;
			this.size = default_size;
		} else {
			this.page = page;
			this.size = size;
		}
	}
}
