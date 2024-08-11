package com.sorted.commons.beans;

import java.util.List;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class SelectedSubCatagories {
	private String sub_category;
	private List<String> selected_attributes;
}
