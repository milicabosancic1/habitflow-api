package com.habitflow.api.dto;

public class WeeklyInsightResponse {

    private String message;

    public WeeklyInsightResponse() {}

    public WeeklyInsightResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
