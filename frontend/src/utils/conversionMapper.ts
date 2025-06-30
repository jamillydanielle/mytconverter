import { Conversion } from "@/types/Conversion";
import { ConversionDTO } from "@/types/ConversionDTO";

/**
 * Groups conversions with the same YouTube URL and user, keeping flags for available formats
 * @param conversions List of conversions to be grouped
 * @returns List of DTOs with grouped conversions
 */
export const groupConversions = (conversions: Conversion[]): ConversionDTO[] => {
  const groupMap: Record<string, ConversionDTO> = {};
  
  for (const conversion of conversions) {
    // Create a composite key using YouTube URL and user ID to ensure conversions
    // from the same video but different users are not grouped
    const key = `${conversion.youtubeUrl}_${conversion.user?.id || 'unknown'}`;
    
    let dto = groupMap[key];
    
    // If this is the first time we encounter this conversion, initialize the DTO
    if (!dto) {
      dto = {
        id: conversion.id,
        userId: conversion.user?.id,
        userName: conversion.user?.name,
        userEmail: conversion.user?.email,
        youtubeVideoName: conversion.youtubeVideoName || 'Unknown Video',
        youtubeUrl: conversion.youtubeUrl || 'Unknown URL',
        createdAt: conversion.createdAt,
        length: conversion.length,
        hasMP3: false,
        hasMP4: false
      };
    }
    
    // Update flags and file names based on format
    if (conversion.format?.toUpperCase() === 'MP3') {
      dto.hasMP3 = true;
      dto.mp3InternalFileName = conversion.internalFileName;
    } else if (conversion.format?.toUpperCase() === 'MP4') {
      dto.hasMP4 = true;
      dto.mp4InternalFileName = conversion.internalFileName;
    }
    
    groupMap[key] = dto;
  }
  
  return Object.values(groupMap);
};