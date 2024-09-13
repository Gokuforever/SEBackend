package com.sorted.commons.beans;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class Media {
	private String document_id; // URL or path to the image or video
	private Integer order; // Order in which the media should be displayed

	public enum MediaType {
		IMAGE;
	}
}
