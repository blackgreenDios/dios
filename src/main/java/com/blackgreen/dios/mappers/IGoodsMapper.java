package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.vos.GoodsVo;
import com.blackgreen.dios.vos.ReviewVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IGoodsMapper {
    ItemColorEntity[] selectColor();

    ItemColorEntity selectColorById(@Param(value = "id") String id);

    SellerEntity[] selectSeller();

    SizeEntity[] selectSize();

    ItemCategoryEntity[] selectItemCategory();

    ItemCategoryEntity selectItemCategoryById(@Param(value = "id") String id);

    SellerEntity selectSellerByIndexExceptImage(@Param(value = "index") int index);

    SellerEntity selectSellerImageByIndex(@Param(value = "index") int index);

    ItemImgEntity selectItemImageByIndex(@Param(value = "index") int index);

    GoodsVo selectItemByIndexExceptImage(@Param(value = "index") int index);

    ItemEntity selectItemTitleImageByIndex(@Param(value = "index") int index);

    int insertItemImage(ItemImgEntity image);

    int insertItem(ItemEntity item);


    ReviewVo[] selectReviewsByGoodsIndex(@Param(value = "itemIndex") int itemIndex);

    ReviewImageEntity[] selectReviewImagesByGoodsIndexExceptData(@Param(value = "reviewIndex") int reviewIndex);

    ReviewImageEntity selectReviewImageByIndex(@Param(value = "index") int index);

    ReviewEntity selectReviewByIndex(@Param(value = "index") int index);

    int insertReview(ReviewEntity review);

    int insertReviewImage(ReviewImageEntity reviewImage);

    int deleteReviewByIndex(int index);

    int updateReview (ReviewEntity review);

}
