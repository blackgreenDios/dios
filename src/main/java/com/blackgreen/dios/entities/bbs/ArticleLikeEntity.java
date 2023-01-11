package com.blackgreen.dios.entities.bbs;

import java.util.Date;
import java.util.Objects;

public class ArticleLikeEntity {
    private String userEmail;

    private int articleIndex;

    private Date createdOn;

    public String getUserEmail() {
        return userEmail;
    }

    public ArticleLikeEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public ArticleLikeEntity setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
        return this;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public ArticleLikeEntity setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleLikeEntity that = (ArticleLikeEntity) o;
        return articleIndex == that.articleIndex && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userEmail, articleIndex);
    }
}
