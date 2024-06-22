package com.sorted.commons.utils;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DataJSList implements Serializable{

	private static final long serialVersionUID = 1667648277175268988L;
	private List<?> data;
}
