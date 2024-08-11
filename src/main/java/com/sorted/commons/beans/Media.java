package com.sorted.commons.beans;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class Media {
	private MediaType type; // "image" or "video"
	private String url; // URL or path to the image or video
	private String alt_text; // Alternative text for accessibility
	private Integer order; // Order in which the media should be displayed

	public enum MediaType {
		IMAGE;
	}
}
