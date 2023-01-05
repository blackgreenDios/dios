package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.vos.GoodsVo;
import com.blackgreen.dios.vos.ReviewVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IGoodsMapper {
    ItemColorEntity[] selectColor();

    ItemColorEntity selectColorByItemIndex(@Param(value = "itemIndex")int itemIndex);
    ItemColorEntity[] selectColorsByItemIndex(@Param(value = "itemIndex")int itemIndex);
    ItemSizeEntity[] selectSizeByItemIndex(@Param(value = "itemIndex")int itemIndex);
    SellerEntity[] selectSeller();

    ItemSizeEntity[] selectSize();
    ItemCategoryEntity[] selectItemCategory();
    GoodsVo selectItemByIndex (@Param(value = "index") int index);

    ItemCategoryEntity selectItemCategoryById(@Param(value = "id") String id);

    SellerEntity selectSellerByIndexExceptImage(@Param(value = "index") int index);

    SellerEntity selectSellerImageByIndex(@Param(value = "index") int index);

    ItemImgEntity selectItemImageByIndex(@Param(value = "index") int index);

    GoodsVo selectItemByIndexExceptImage(@Param(value = "index") int index);

    ItemEntity selectItemTitleImageByIndex(@Param(value = "index") int index);

    ItemSizeEntity selectItemSizeById(@Param(value = "id") String id);
    ItemColorEntity selectItemColorById(@Param(value = "id") String id);

    int insertItemImage(ItemImgEntity image);

    int insertItem(ItemEntity item);


    ReviewVo[] selectReviewsByGoodsIndex(@Param(value = "itemIndex") int itemIndex);

    ReviewImageEntity[] selectReviewImagesByGoodsIndexExceptData(@Param(value = "reviewIndex") int reviewIndex);

    ReviewImageEntity selectReviewImageByIndex(@Param(value = "index") int index);

    ReviewEntity selectReviewByIndex(@Param(value = "index") int index);

    int insertItemColor(ItemColorEntity color);
    int insertItemSize(ItemSizeEntity size);
    int insertReview(ReviewEntity review);

    int insertReviewImage(ReviewImageEntity reviewImage);

    int deleteReviewByIndex(int index);

    int deleteItemByIndex (@Param(value = "index") int index);

    int updateReview(ReviewEntity review);

    int updateItem (ItemEntity item);

    int updateItemColor(ItemColorEntity[] itemColor);
    int updateItemSize(ItemSizeEntity itemSize);


    int selectItemCountByCategoryId(@Param(value = "categoryId") String categoryId,
                                    @Param(value = "criterion") String criterion,
                                    @Param(value = "keyword") String keyword);


    GoodsVo[] selectItemsByCategoryId(@Param(value = "categoryId") String categoryId,
                                      @Param(value = "criterion") String criterion,
                                      @Param(value = "keyword") String keyword,
                                      @Param(value = "limit") int limit,
                                      @Param(value = "offset") int offset);

}
