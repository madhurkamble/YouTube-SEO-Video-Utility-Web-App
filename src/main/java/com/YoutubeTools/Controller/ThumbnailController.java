package com.YoutubeTools.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import com.YoutubeTools.Service.ThumbnailService;

@Controller
public class ThumbnailController {

    @Autowired
    ThumbnailService service;

    @GetMapping("/thumbnail")
    public String getThumbnail() {
        return "thumbnails";
    }

    @PostMapping("/get-thumbnail")
    public String showThumbnail(
            @RequestParam("videoUrlOrId") String videoUrlOrId,
            Model model) {

        String videoId = service.extractVideoId(videoUrlOrId);

        if (videoId == null) {
            model.addAttribute("error",
                    "Invalid YouTube URL or Video ID");

            return "thumbnails";
        }

        String thumbnailUrl =
                "https://img.youtube.com/vi/"
                + videoId
                + "/hqdefault.jpg";

        model.addAttribute("thumbnailUrl", thumbnailUrl);
        model.addAttribute("videoId", videoId);

        return "thumbnails";
    }


    @GetMapping("/download-thumbnail")
    public ResponseEntity<byte[]> downloadThumbnail(
            @RequestParam("videoId") String videoId) {

        String thumbnailUrl =
                "https://img.youtube.com/vi/"
                + videoId
                + "/hqdefault.jpg";

        RestClient restClient = RestClient.create();

        byte[] image = restClient.get()
                .uri(thumbnailUrl)
                .retrieve()
                .body(byte[].class);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"youtube-thumbnail-"
                                + videoId
                                + ".jpg\""
                )
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}