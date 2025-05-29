package com.mytconvert.convertion.mapper;

import com.mytconvert.convertion.dto.ConvertionDTO;
import com.mytconvert.convertion.dto.Mp3ConvertionDTO;
import com.mytconvert.convertion.dto.Mp4ConvertionDTO;
import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.entity.Mp3Convertion;
import com.mytconvert.convertion.entity.Mp4Convertion;
import com.mytconvert.security.utils.JwtUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConvertionMapper {

    /**
     * Converts a map of payload tags to a Convertion object.
     *
     * @param payload A map containing the data for the convertion.  The expected keys are:
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
        Long fileSize = (Long) payload.get("fileSize");

        Convertion convertion = new Convertion(JwtUtils.getCurrentUserId().get(), youtubeUrl, JwtUtils.getCurrentUserData().get().getUsername(),fileSize);

        convertion.setYoutubeUrl(youtubeUrl);
        if(quality == null && bitrate == null){
            throw new IllegalArgumentException("You must provide quality or bitrate.");
        }

        return convertion;
    }

    public ConvertionDTO mapEntitytoDTO(Convertion convertion) {
        if (convertion instanceof Mp3Convertion) {
            Mp3Convertion mp3Convertion = (Mp3Convertion) convertion;
            return new Mp3ConvertionDTO(
                    mp3Convertion.getId(),
                    mp3Convertion.getUserName(),
                    mp3Convertion.getYoutubeUrl(),
                    mp3Convertion.getFileSize(),
                    mp3Convertion.getCreatedAt(),
                    mp3Convertion.getBitRate()
            );
        } else if (convertion instanceof Mp4Convertion) {
            Mp4Convertion mp4Convertion = (Mp4Convertion) convertion;
            return new Mp4ConvertionDTO(
                    mp4Convertion.getId(),
                    mp4Convertion.getUserName(),
                    mp4Convertion.getYoutubeUrl(),
                    mp4Convertion.getFileSize(),
                    mp4Convertion.getCreatedAt(),
                    mp4Convertion.getVideoResolution()
            );
        } else {
            return new ConvertionDTO(
                    convertion.getId(),
                    convertion.getUserName(),
                    convertion.getYoutubeUrl(),
                    convertion.getFileSize(),
                    convertion.getCreatedAt()
            );
        }
    }
}