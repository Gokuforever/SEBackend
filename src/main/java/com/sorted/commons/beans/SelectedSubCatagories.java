package com.sorted.commons.beans;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Data
@FieldNameConstants
public class SelectedSubCatagories {
	private String sub_category;
	private List<String> selected_attributes;
}
