package com.socion.session.dto.v2;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class SessionAdditionalLinksDTO {
    @NotEmpty
//    @Pattern(regexp = ".*\\..*$", message = " Please insert a valid url")
    private String url;
    @NotNull(message = "UserId must not be Empty")
    @NotEmpty
    private String userId;

    public SessionAdditionalLinksDTO() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
