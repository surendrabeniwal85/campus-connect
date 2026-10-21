package com.campusconnect.backend.institute.classroom.dto;
import java.util.Map; public record ApiError(int status,String message,Map<String,String> fieldErrors) {}