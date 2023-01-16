package com.blackgreen.dios.vos.bbs;

import com.blackgreen.dios.entities.bbs.CommentEntity;

public class CommentVo extends CommentEntity {
    private String userNickname;

    private boolean isSigned;

    private boolean isMine;

    private boolean isLiked;

    private int likeCount;



    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public void setSigned(boolean signed) {
        isSigned = signed;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}


