package com.YoutubeTools.Model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Video {

    private String videoId;

    private String videoTitle;

    private String channelTitle;

    private List<String> tags;

    public String getTagsAsString() {
        if (tags == null || tags.isEmpty()) {
            return "";
        }

        return String.join(", ", tags);
    }
}