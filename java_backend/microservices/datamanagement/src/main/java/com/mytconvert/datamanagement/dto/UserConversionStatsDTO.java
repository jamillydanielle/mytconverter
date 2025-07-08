package com.mytconvert.datamanagement.dto;

public class UserConversionStatsDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private int mp3Conversions;
    private int mp4Conversions;
    private double mp3TotalMinutes;
    private double mp4TotalMinutes;
    private String preferredFormat;
    
    public UserConversionStatsDTO() {
    }
    
    public UserConversionStatsDTO(Long userId, String userName, String userEmail, 
                                 int mp3Conversions, int mp4Conversions, 
                                 double mp3TotalMinutes, double mp4TotalMinutes, 
                                 String preferredFormat) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.mp3Conversions = mp3Conversions;
        this.mp4Conversions = mp4Conversions;
        this.mp3TotalMinutes = mp3TotalMinutes;
        this.mp4TotalMinutes = mp4TotalMinutes;
        this.preferredFormat = preferredFormat;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getMp3Conversions() {
        return mp3Conversions;
    }

    public void setMp3Conversions(int mp3Conversions) {
        this.mp3Conversions = mp3Conversions;
    }

    public int getMp4Conversions() {
        return mp4Conversions;
    }

    public void setMp4Conversions(int mp4Conversions) {
        this.mp4Conversions = mp4Conversions;
    }

    public double getMp3TotalMinutes() {
        return mp3TotalMinutes;
    }

    public void setMp3TotalMinutes(double mp3TotalMinutes) {
        this.mp3TotalMinutes = mp3TotalMinutes;
    }

    public double getMp4TotalMinutes() {
        return mp4TotalMinutes;
    }

    public void setMp4TotalMinutes(double mp4TotalMinutes) {
        this.mp4TotalMinutes = mp4TotalMinutes;
    }

    public String getPreferredFormat() {
        return preferredFormat;
    }

    public void setPreferredFormat(String preferredFormat) {
        this.preferredFormat = preferredFormat;
    }
}