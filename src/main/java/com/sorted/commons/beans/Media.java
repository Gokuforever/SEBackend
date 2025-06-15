package com.sorted.commons.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {
	private String document_id; // URL or path to the image or video
	private Integer order; // Order in which the media should be displayed
	private String key;
	private String src_url;

	public enum MediaType {
		IMAGE;
	}
}
