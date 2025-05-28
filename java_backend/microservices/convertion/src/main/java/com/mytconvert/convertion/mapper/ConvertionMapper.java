package com.mytconvert.convertion.mapper;


import java.util.Map;

import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.entity.Mp4Convertion;

public class ConvertionMapper {

    /**
     * Converts a map of payload tags to a Convertion object.
     *
     * @param payload A map containing the data for the conversion.  The expected keys are:
     *                - "youtubeUrl" (String, required): The URL of the YouTube video.
     *                - "quality" (String, optional): The desired video quality.
     *                - "bitrate" (String, optional): The desired audio bitrate.
     * @return A Convertion object populated with the data from the payload.
     * @throws IllegalArgumentException if the "youtubeUrl" is missing or invalid.
     */
    public Convertion mapPayloadToConvertion(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException("Payload cannot be null or empty.");
        }

        String youtubeUrl = (String) payload.get("youtubeUrl");
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("YouTube URL is required.");
        }

        String quality = (String) payload.get("quality");
        String bitrate = (String) payload.get("bitrate");

        Convertion convertion = new Convertion();
        convertion.setYoutubeUrl(youtubeUrl);
        if(quality == null && bitrate == null){
            throw new IllegalArgumentException("You must provide quality or bitrate.");
        }
        
        return convertion;
    }
}