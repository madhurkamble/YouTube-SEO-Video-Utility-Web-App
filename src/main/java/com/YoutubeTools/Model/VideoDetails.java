package com.YoutubeTools.Model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoDetails {

    private String videoId;
    private String title;
    private String description;
    private String channelTitle;
    private String publishedAt;
    private String thumbnailUrl;
    private List<String> tags;
}