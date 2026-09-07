package com.habitflow.api.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Minimalan model odgovora Anthropic Messages API-ja (samo polja koja koristimo). */
@JsonIgnoreProperties(ignoreUnknown = true)
class AnthropicMessageResponse {

    private List<ContentBlock> content;

    public List<ContentBlock> getContent() { return content; }
    public void setContent(List<ContentBlock> content) { this.content = content; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ContentBlock {
        private String type;
        private String text;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
