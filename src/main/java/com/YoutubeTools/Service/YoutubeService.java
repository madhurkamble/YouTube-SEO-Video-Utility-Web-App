package com.YoutubeTools.Service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.YoutubeTools.Model.SearchVideo;
import com.YoutubeTools.Model.Video;
import com.YoutubeTools.Model.VideoDetails;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final WebClient.Builder webClientBuilder;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Value("${youtube.api.base.url}")
    private String youtubeApiBaseUrl;

    @Value("${youtube.api.max.related.videos}")
    private int youtubeRelatedVideosMax;

    public SearchVideo searchVideo(String videoTitle) {

        List<String> videoIds = searchVideoIds(videoTitle);

        if (videoIds.isEmpty()) {
            return SearchVideo.builder()
                    .primaryVideo(null)
                    .relatedVideos(Collections.emptyList())
                    .build();
        }

        String primaryVideoId = videoIds.get(0);

        List<String> relatedVideoIds =
                videoIds.size() > 1
                        ? videoIds.subList(1, videoIds.size())
                        : Collections.emptyList();

        Video primaryVideo = fetchVideoDetails(primaryVideoId);

        List<Video> relatedVideos =
                new java.util.ArrayList<>();

        for (String relatedVideoId : relatedVideoIds) {

            Video relatedVideo =
                    fetchVideoDetails(relatedVideoId);

            if (relatedVideo != null) {
                relatedVideos.add(relatedVideo);
            }
        }

        return SearchVideo.builder()
                .primaryVideo(primaryVideo)
                .relatedVideos(relatedVideos)
                .build();
    }

    private List<String> searchVideoIds(String videoTitle) {

        SearchApiResponse searchApiResponse =
                webClientBuilder
                        .baseUrl(youtubeApiBaseUrl)
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/search")
                                .queryParam("part", "snippet")
                                .queryParam("q", videoTitle)
                                .queryParam("type", "video")
                                .queryParam(
                                        "maxResults",
                                        youtubeRelatedVideosMax
                                )
                                .queryParam(
                                        "key",
                                        youtubeApiKey
                                )
                                .build())
                        .retrieve()
                        .bodyToMono(SearchApiResponse.class)
                        .block();


        if (searchApiResponse == null ||
                searchApiResponse.getItems() == null ||
                searchApiResponse.getItems().isEmpty()) {

            throw new RuntimeException(
                    "No videos found for the given title."
            );
        }


        List<String> videoIds =
                new java.util.ArrayList<>();


        for (SearchItem item :
                searchApiResponse.getItems()) {

            if (item.getId() != null &&
                    item.getId().getVideoId() != null) {

                videoIds.add(
                        item.getId().getVideoId()
                );
            }
        }

        return videoIds;
    }

    public Video fetchVideoDetails(String videoId) {

        VideoApiResponse videoApiResponse =
                webClientBuilder
                        .baseUrl(youtubeApiBaseUrl)
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/videos")
                                .queryParam("part", "snippet")
                                .queryParam("id", videoId)
                                .queryParam(
                                        "key",
                                        youtubeApiKey
                                )
                                .build())
                        .retrieve()
                        .bodyToMono(VideoApiResponse.class)
                        .block();


        if (videoApiResponse == null ||
                videoApiResponse.getItems() == null ||
                videoApiResponse.getItems().isEmpty()) {

            throw new RuntimeException(
                    "No video details found for the given ID."
            );
        }


        VideoItem videoItem =
                videoApiResponse.getItems().get(0);

        Snippet snippet =
                videoItem.getSnippet();


        if (snippet == null) {
            throw new RuntimeException(
                    "Video snippet not found."
            );
        }


        return Video.builder()
                .videoId(videoId)
                .channelTitle(
                        snippet.getChannelTitle()
                )
                .videoTitle(
                        snippet.getTitle()
                )
                .tags(
                        snippet.getTags() != null
                                ? snippet.getTags()
                                : Collections.emptyList()
                )
                .build();
    }

    public VideoDetails fetchVideoDetailsFull(String videoId) {

        VideoApiResponse videoApiResponse =
                webClientBuilder
                        .baseUrl(youtubeApiBaseUrl)
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/videos")
                                .queryParam(
                                        "part",
                                        "snippet"
                                )
                                .queryParam(
                                        "id",
                                        videoId
                                )
                                .queryParam(
                                        "key",
                                        youtubeApiKey
                                )
                                .build())
                        .retrieve()
                        .bodyToMono(VideoApiResponse.class)
                        .block();


        if (videoApiResponse == null ||
                videoApiResponse.getItems() == null ||
                videoApiResponse.getItems().isEmpty()) {

            return null;
        }


        VideoItem videoItem =
                videoApiResponse.getItems().get(0);

        Snippet snippet =
                videoItem.getSnippet();


        if (snippet == null) {
            return null;
        }


        String thumbnailUrl = "";

        if (snippet.getThumbnails() != null) {

            thumbnailUrl =
                    snippet.getThumbnails()
                            .getBestThumbnailUrl();
        }


        return VideoDetails.builder()
                .videoId(videoId)
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .channelTitle(snippet.getChannelTitle())
                .publishedAt(snippet.getPublishedAt())
                .thumbnailUrl(thumbnailUrl)
                .tags(
                        snippet.getTags() != null
                                ? snippet.getTags()
                                : Collections.emptyList()
                )
                .build();
    }

    @Data
    static class SearchApiResponse {
        List<SearchItem> items;
    }


    @Data
    static class SearchItem {
        Id id;
    }


    @Data
    static class Id {
        String videoId;
    }


    @Data
    static class VideoApiResponse {
        List<VideoItem> items;
    }


    @Data
    static class VideoItem {
        Snippet snippet;
    }


    @Data
    static class Snippet {

        String title;

        String description;

        String channelTitle;

        String publishedAt;

        List<String> tags;

        Thumbnails thumbnails;
    }


    @Data
    static class Thumbnails {

        Thumbnails maxres;

        Thumbnails high;

        Thumbnails medium;

        String url;


        @JsonProperty("default")
        Thumbnails _default;


        String getBestThumbnailUrl() {

            if (maxres != null) {
                return maxres.url;
            }

            if (high != null) {
                return high.url;
            }

            if (medium != null) {
                return medium.url;
            }

            return _default != null
                    ? _default.url
                    : "";
        }
    }   
}