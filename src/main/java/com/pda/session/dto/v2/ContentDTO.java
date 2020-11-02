package com.pda.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentDTO implements Serializable {


    private String name;
    private String contentType;
    private String url;
    private String vimeo_url;
    private Long topicId;
    private String vimeoId;

    public String getVimeoId() {
        return vimeoId;
    }

    public void setVimeoId(String vimeoId) {
        this.vimeoId = vimeoId;
    }

    public ContentDTO(String name, String contentType, String url, String vimeo_url, Long topicId) {
        this.name = name;
        this.contentType = contentType;
        this.url = url;
        this.vimeo_url = vimeo_url;
        this.topicId = topicId;
    }

    public ContentDTO() {
    }

    public String getVimeo_url() {
        return vimeo_url;
    }

    public void setVimeo_url(String vimeo_url) {
        this.vimeo_url = vimeo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }


    public String getContentType() {
        return contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
