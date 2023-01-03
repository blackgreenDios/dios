package com.blackgreen.dios.entities.bbs;

import java.util.Date;
import java.util.Objects;

public class CommentEntity {
    //
    private int index;

    private Integer commentIndex;

    private String userEmail;

    private int articleIndex;

    private String content;

    private Date writtenOn;

    private Date modifiedOn;

    private boolean isSecret;

    public int getIndex() {
        return index;
    }

    public CommentEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public Integer getCommentIndex() {
        return commentIndex;
    }

    public CommentEntity setCommentIndex(Integer commentIndex) {
        this.commentIndex = commentIndex;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public CommentEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public CommentEntity setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
        return this;
    }

    public String getContent() {
        return content;
    }

    public CommentEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getWrittenOn() {
        return writtenOn;
    }

    public CommentEntity setWrittenOn(Date writtenOn) {
        this.writtenOn = writtenOn;
        return this;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public CommentEntity setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    public Boolean getSecret() {
        return isSecret;
    }

    public CommentEntity setSecret(Boolean secret) {
        isSecret = secret;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentEntity that = (CommentEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
