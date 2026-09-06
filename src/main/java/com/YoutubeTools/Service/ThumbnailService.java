package com.YoutubeTools.Service;

import org.springframework.stereotype.Service;

@Service 
public class ThumbnailService {
    public String extractVideoId(String url){
        if(url.matches("^[a-zA-Z-0-9_-]{11}$")){
            return url;
        } 
        String patterns[] = {
            "https?://(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]{11})",
            "https?://(?:www\\.)?youtube\\.com/embed/([a-zA-Z0-9_-]{11})",
            "https?://youtu\\.be/([a-zA-Z0-9_-]{11})"
        };

        for(String pattern : patterns){
            java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = compiledPattern.matcher(url);
            if(matcher.find()){
                return matcher.group(1);
            }
        }
        return null;
    }
}
