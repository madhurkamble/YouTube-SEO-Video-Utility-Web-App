package com.YoutubeTools.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.YoutubeTools.Model.VideoDetails;
import com.YoutubeTools.Service.ThumbnailService;
import com.YoutubeTools.Service.YoutubeService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class YoutubeVideoController {

    private final YoutubeService youtubeService;
    private final ThumbnailService thumbnailService;

    @GetMapping("/youtube/video-details")
    public String showVideoForm() {
        return "video-details";
    }

    @PostMapping("/youtube/video-details")
    public String getVideoDetails(
            @RequestParam("videoUrlOrId") String videoUrlOrId,
            Model model) {

        // Extract video ID
        String videoId =
                thumbnailService.extractVideoId(videoUrlOrId);

        if (videoId == null) {

            model.addAttribute(
                    "error",
                    "Invalid YouTube URL or Video ID"
            );

            model.addAttribute(
                    "videoUrlOrId",
                    videoUrlOrId
            );

            return "video-details";
        }

        // Fetch complete video details
        VideoDetails videoDetails =
                youtubeService.fetchVideoDetailsFull(videoId);

        if (videoDetails == null) {

            model.addAttribute(
                    "error",
                    "Failed to fetch video details"
            );

            model.addAttribute(
                    "videoUrlOrId",
                    videoUrlOrId
            );

            return "video-details";
        }

        model.addAttribute(
                "videoDetails",
                videoDetails
        );

        model.addAttribute(
                "videoUrlOrId",
                videoUrlOrId
        );

        return "video-details";
    }
}