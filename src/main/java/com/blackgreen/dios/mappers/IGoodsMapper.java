package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.vos.goods.GoodsVo;
import com.blackgreen.dios.vos.goods.ReviewVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IGoodsMapper {

    ItemColorEntity selectColorByItemIndex(@Param(value = "itemIndex") int itemIndex);

    ItemColorEntity[] selectColorsByItemIndex(@Param(value = "itemIndex") int itemIndex);

    ItemSizeEntity[] selectSizeByItemIndex(@Param(value = "itemIndex") int itemIndex);

    SellerEntity[] selectSeller();

    ItemSizeEntity[] selectSize();

    ItemCategoryEntity[] selectItemCategory();

    GoodsVo selectItemByIndex(@Param(value = "index") int index);

    ItemCategoryEntity selectItemCategoryById(@Param(value = "id") String id);

    SellerEntity selectSellerByIndexExceptImage(@Param(value = "index") int index);

    SellerEntity[] selectSellersByIndexExceptImage(@Param(value = "index") int index);

    SellerEntity selectSellerImageByIndex(@Param(value = "index") int index);

    ItemImgEntity selectItemImageByIndex(@Param(value = "index") int index);

    GoodsVo selectItemByIndexExceptImage(@Param(value = "index") int index);

    ItemEntity selectItemTitleImageByIndex(@Param(value = "index") int index);

    ItemColorEntity selectItemColorByItemIndexColor(@Param(value = "itemIndex") int itemIndex,
                                                    @Param(value = "color") String color);

    ItemSizeEntity selectItemSizeByItemIndexSize(@Param(value = "itemIndex") int itemIndex,
                                                 @Param(value = "size") String size);

    int insertItemImage(ItemImgEntity image);

    int insertItem(ItemEntity item);


    ReviewVo[] selectReviewsByGoodsIndex(@Param(value = "itemIndex") int itemIndex);

    ReviewImageEntity[] selectReviewImagesByGoodsIndexExceptData(@Param(value = "reviewIndex") int reviewIndex);

    ReviewImageEntity selectReviewImageByIndex(@Param(value = "index") int index);

    ReviewEntity selectReviewByIndex(@Param(value = "index") int index);

    int insertItemColor(ItemColorEntity color);

    int insertItemSize(ItemSizeEntity size);

    int insertReview(ReviewEntity review);

    int insertCartItem(CartEntity cart);

    int insertReviewImage(ReviewImageEntity reviewImage);

    int deleteReviewByIndex(int index);

    int deleteItemByIndex(@Param(value = "index") int index);

    int deleteItemColorByItemIndexColor(@Param(value = "itemIndex") int itemIndex,
                                        @Param(value = "color") String color);

    int deleteItemSizeByItemIndexSize(@Param(value = "itemIndex") int itemIndex,
                                      @Param(value = "size") String size);

    int updateReview(ReviewEntity review);

    int updateItem(ItemEntity item);


    int updateItemColor(ItemColorEntity[] itemColor);

    int updateItemSize(ItemSizeEntity itemSize);

    int selectItemsCount();

    GoodsVo[] selectItemExceptImages(
            @Param(value = "limit") int limit,
            @Param(value = "offset") int offset);

    ItemEntity[] selectItemsTitleImage();

//    GoodsVo[] selectItemsByCategoryId(@Param(value = "limit") int limit,
//                                      @Param(value = "offset") int offset);

    int[] selectIndex();

}
