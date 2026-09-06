package com.YoutubeTools.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.YoutubeTools.Model.SearchVideo;
import com.YoutubeTools.Service.YoutubeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeTagsController {

    private final YoutubeService youtubeService;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;


    private boolean isApiKeyConfigured() {

        return youtubeApiKey != null
                && !youtubeApiKey.isBlank();
    }


    @PostMapping("/search")
    public String videoTags(
            @RequestParam("videoTitle") String videoTitle,
            Model model) {

        if (!isApiKeyConfigured()) {

            model.addAttribute(
                    "error",
                    "YouTube API key is not configured."
            );

            return "home";
        }


        if (videoTitle == null
                || videoTitle.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Video Title is required."
            );

            return "home";
        }


        try {

            SearchVideo searchVideo =
                    youtubeService.searchVideo(videoTitle);


            model.addAttribute(
                    "primaryVideo",
                    searchVideo.getPrimaryVideo()
            );


            model.addAttribute(
                    "relatedVideos",
                    searchVideo.getRelatedVideos()
            );


            model.addAttribute(
                    "allTagsAsString",
                    searchVideo.getAllTagsAsString()
            );

        }
        catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "An error occurred while processing the request."
            );

            return "home";
        }


        return "home";
    }
}