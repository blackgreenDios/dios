package com.blackgreen.dios.vos.goods;

import com.blackgreen.dios.entities.store.ReviewEntity;

public class ReviewVo extends ReviewEntity {
    private String userNickname;
    private int[] imageIndexes;

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public int[] getImageIndexes() {
        return imageIndexes;
    }

    public void setImageIndexes(int[] imageIndexes) {
        this.imageIndexes = imageIndexes;
    }


}
