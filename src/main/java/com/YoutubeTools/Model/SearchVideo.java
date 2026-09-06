package com.YoutubeTools.Model;

import java.util.List;

import org.jspecify.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class SearchVideo {
    private Video primaryVideo;
    private List<Video> relatedVideos;

    public String getAllTagsAsString() {

        if (relatedVideos == null || relatedVideos.isEmpty()) {
            return "";
        }

        StringBuilder allTags = new StringBuilder();

        for (Video video : relatedVideos) {

            if (video.getTags() != null) {

                for (String tag : video.getTags()) {

                    if (allTags.length() > 0) {
                        allTags.append(", ");
                    }

                    allTags.append(tag);
                }
            }
        }

        return allTags.toString();
    }
}
