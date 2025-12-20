package com.sorted.commons.entity.beans;

import lombok.Builder;

@Builder
public class Recommendation {

    private String title;
    private String text;
    private int rating;
}
