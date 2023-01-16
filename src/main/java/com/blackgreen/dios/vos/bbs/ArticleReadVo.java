package com.blackgreen.dios.vos.bbs;

import com.blackgreen.dios.entities.bbs.ArticleEntity;

public class ArticleReadVo extends ArticleEntity {
    private String userNickname;

    private byte[] userImage;

    private String userImageType;
    private int commentCount;

    private boolean isMine;

    private boolean isSigned;

    private boolean isLiked;

    private int likeCount;


    public byte[] getUserImage() {
        return userImage;
    }

    public ArticleReadVo setUserImage(byte[] userImage) {
        this.userImage = userImage;
        return this;
    }

    public String getUserImageType() {
        return userImageType;
    }

    public ArticleReadVo setUserImageType(String userImageType) {
        this.userImageType = userImageType;
        return this;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public ArticleReadVo setSigned(boolean signed) {
        isSigned = signed;
        return this;
    }

    public boolean isMine() {
        return isMine;
    }

    public ArticleReadVo setMine(boolean mine) {
        isMine = mine;
        return this;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public ArticleReadVo setLiked(boolean liked) {
        isLiked = liked;
        return this;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public ArticleReadVo setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

}

