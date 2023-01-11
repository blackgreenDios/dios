package com.blackgreen.dios.entities.bbs;

import java.util.Date;
import java.util.Objects;

public class CommentLikeEntity {
    private String userEmail;

    private int commentIndex;

    private Date createdOn;

    public String getUserEmail() {
        return userEmail;
    }

    public CommentLikeEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public int getCommentIndex() {
        return commentIndex;
    }

    public CommentLikeEntity setCommentIndex(int commentIndex) {
        this.commentIndex = commentIndex;
        return this;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public CommentLikeEntity setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentLikeEntity that = (CommentLikeEntity) o;
        return Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail);
    }
}
