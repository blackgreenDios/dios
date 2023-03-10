package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.goods.AddReviewResult;
import com.blackgreen.dios.enums.goods.ModifyItemResult;
import com.blackgreen.dios.enums.goods.ReviewDeleteResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IGoodsMapper;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.vos.goods.GoodsVo;
import com.blackgreen.dios.vos.goods.ReviewVo;
import com.blackgreen.dios.vos.goods.GoodsVo;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.FetchProfile;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

@Service(value = "com.blackgreen.dios.services.GoodsService")
public class GoodsService {
    private final IGoodsMapper goodsMapper;

    @Autowired
    public GoodsService(IGoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

//    public ItemColorEntity[] getColor() {
//        return this.goodsMapper.selectColor();
//    }

    public SellerEntity[] getSeller() {
        return this.goodsMapper.selectSeller();
    }

    public SellerEntity getBrand(int index) {
        return this.goodsMapper.selectSellerByIndexExceptImage(index);
    }


    public ItemCategoryEntity[] getItemCategory() {
        return this.goodsMapper.selectItemCategory();
    }

    public ItemCategoryEntity getCategory(String id) {
        return this.goodsMapper.selectItemCategoryById(id);
    }

    public ItemSizeEntity[] getSize() {
        return this.goodsMapper.selectSize();
    }

    public ItemColorEntity[] getItemColors(int itemIndex) {
        return this.goodsMapper.selectColorsByItemIndex(itemIndex);
    }

    public ItemSizeEntity[] getItemSize(int itemIndex) {
        return this.goodsMapper.selectSizeByItemIndex(itemIndex);
    }

    public GoodsVo getItem(int index) {
        return this.goodsMapper.selectItemByIndexExceptImage(index);
    }


    @Transactional
    public Enum<? extends IResult> addItem(UserEntity user, ItemEntity item, MultipartFile images) throws IOException {


//        item = this.goodsMapper.selectItemByIndex(item.getIndex()); //item??? getIndex??? ???????????? ???????????? 0?????????,
//                                                                      ????????? ?????? ????????????/ ????????? ?????? select????????? ????????????. insert????????? ?????? ?????? ?????? ??????
//        ItemImgEntity itemImg = new ItemImgEntity();
//        itemImg.setIndex(item.getIndex());

        System.out.println(item);
        System.out.println(images);
        item.setTitleImageData(images.getBytes());
        item.setTitleImageMime(images.getContentType());
        item.setTitleImageName(images.getName());


        return this.goodsMapper.insertItem(item) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    @Transactional
    public Enum<? extends IResult> addItemColors(ItemColorEntity[] itemColors) {

        int result = 0;
        for (ItemColorEntity ItemColor : itemColors) {
            result += this.goodsMapper.insertItemColor(ItemColor);
        }

        return result == itemColors.length
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> addItemSizes(ItemSizeEntity[] ItemSizes) {

        int result = 0;
        for (ItemSizeEntity ItemSize : ItemSizes) {
            result += this.goodsMapper.insertItemSize(ItemSize);
        }

        return result == ItemSizes.length
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    public ItemImgEntity getImage(int index) {
        return this.goodsMapper.selectItemImageByIndex(index);
    }


    public SellerEntity getSellerImage(int index) {
        return this.goodsMapper.selectSellerImageByIndex(index);
    }

    public ItemEntity getItemTitleImage(int index) {
        return this.goodsMapper.selectItemTitleImageByIndex(index);
    }

    public Enum<? extends IResult> addItemImage(ItemImgEntity image) {
        return this.goodsMapper.insertItemImage(image) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    public ReviewVo[] getReviewsPaging(int itemIndex, PagingModel paging) {

        ReviewVo[] reviews = this.goodsMapper.selectReviewsByGoodsIndexPaging(
                itemIndex,
                paging.countPerPage,
                (paging.requestPage - 1) * paging.countPerPage); //?????? index??? ???????????? review select

        for (ReviewVo review : reviews) {
            ReviewImageEntity[] reviewImage = this.goodsMapper.selectReviewImagesByGoodsIndexExceptData(review.getIndex());
            int[] reviewImageIndexes = Arrays.stream(reviewImage).mapToInt(ReviewImageEntity::getIndex).toArray();
            review.setImageIndexes(reviewImageIndexes);
        }

        return reviews;
    }

    public ReviewVo[] getReviews(int itemIndex) {

        ReviewVo[] reviews = this.goodsMapper.selectReviewsByGoodsIndex(itemIndex); //?????? index??? ???????????? review select
        for (ReviewVo review : reviews) {
            ReviewImageEntity[] reviewImage = this.goodsMapper.selectReviewImagesByGoodsIndexExceptData(review.getIndex());
            int[] reviewImageIndexes = Arrays.stream(reviewImage).mapToInt(ReviewImageEntity::getIndex).toArray();
            review.setImageIndexes(reviewImageIndexes);
        }
        return reviews;
    }


    public ReviewImageEntity getReviewImage(int index) {
        return this.goodsMapper.selectReviewImageByIndex(index);
    }

    /*?????? ??????*/
    public Enum<? extends IResult> deleteReview(UserEntity user, ReviewEntity review) {

        ReviewEntity existingReview = this.goodsMapper.selectReviewByIndex(review.getIndex());//??? ?????? ????????? comment.getIndex() ??? ???????????? ????????? ????????? ??????
        if (existingReview == null) {
            return ReviewDeleteResult.NO_SUCH_Review;
        }
        if (user == null || !user.getEmail().equals(existingReview.getUserEmail())) {
            return ReviewDeleteResult.NOT_ALLOWED;
        }
//        if (user == null || !user.getEmail().equals(existingReview.getUserEmail())) { // ?????? existingCommentfh ??? ????????????
//            return ReviewDeleteResult.NOT_ALLOWED;
//        }
        return this.goodsMapper.deleteReviewByIndex(review.getIndex()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public Enum<? extends IResult> recoverReview(ReviewEntity review, UserEntity user) {
        // ?????? ??????
        // ??????
        // ????????? ?????? ??????
        // ???????????? ??????????????? + ??????????????? ????????? ??? ????????? ?????? ??????

        ReviewEntity existingReview = this.goodsMapper.selectReviewByIndex(review.getIndex());
        if (existingReview == null) {
            return ReviewDeleteResult.NO_SUCH_Review;
        }
        if (user == null || !user.getEmail().equals(existingReview.getUserEmail())) {
            return ReviewDeleteResult.NOT_ALLOWED;
        }

        existingReview.setContent(review.getContent());

        // ?????? ???????????? ???????????? ????????? ????????? set ?????????
        if (review.getScore() != 0) {
            existingReview.setScore(review.getScore());
        }

        return this.goodsMapper.updateReview
                (existingReview) > 0 ?
                CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> addReview(UserEntity user, ReviewVo review, MultipartFile[] images) throws IOException, RollbackException {
        if (user == null) {
            return AddReviewResult.NOT_SIGNED;
        }
        review.setUserEmail(user.getEmail());
        review.setUserNickname(user.getNickname());
        // ?????? ?????? ?????? ?????? ???????????? ???????????? return
        OrderEntity[] existingOrders = this.goodsMapper.selectOrderByItemIndexEmailStatus(review.getItemIndex(), user.getEmail(), 3);

        if (existingOrders.length == 0) {
            return AddReviewResult.NOT_ALLOWED;
        }

        if (this.goodsMapper.insertReview(review) == 0) {
            return AddReviewResult.FAILURE;
        }

        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                ReviewImageEntity reviewImage = new ReviewImageEntity();
                reviewImage.setReviewIndex(review.getIndex());
                reviewImage.setData(image.getBytes());
                reviewImage.setType(image.getContentType());
                if (this.goodsMapper.insertReviewImage(reviewImage) == 0) {
                    throw new RollbackException();
                }
            }
        }

        return AddReviewResult.SUCCESS;
    }


    public GoodsVo[] getItems(PagingModel paging, String categoryId) {
        return this.goodsMapper.selectItemExceptImages(
                paging.countPerPage,
                (paging.requestPage - 1) * paging.countPerPage,
                categoryId);
    }

    public ItemEntity[] getItemImages() {
        return this.goodsMapper.selectItemsTitleImage();
    }

//    public GoodsVo[] getItems( PagingModel paging) {
//
//        return this.goodsMapper.selectItemsByCategoryId(
//                paging.countPerPage,
//                (paging.requestPage - 1) * paging.countPerPage);
//    }

    @Transactional
    public Enum<? extends IResult> prepareModifyItem(ItemEntity item, UserEntity user) {
        // ?????? ??????
        // ??????
        // ????????? ?????? ??????
        // ???????????? ??????????????? + ??????????????? ????????? ??? ????????? ?????? ??????

//        if (user == null) {
//            return ModifyItemResult.NOT_SIGNED;
//        }

        ItemEntity existingItem = this.goodsMapper.selectItemByIndex(item.getIndex());
//        if (existingItem == null) {
//            return ModifyItemResult.NO_SUCH_Item;
//        }
//        if (!existingItem.getUserEmail().equals(user.getEmail())) {
//            return ModifyItemResult.NOT_ALLOWED;
//        }

        item.setCategoryId(existingItem.getCategoryId());// ?????? cad
        item.setSellerIndex(existingItem.getSellerIndex());
        item.setItemName(existingItem.getItemName());
        item.setItemDetail(existingItem.getItemDetail());
        item.setPrice(existingItem.getPrice());
        item.setCount(existingItem.getCount());
        item.setCreatedOn(existingItem.getCreatedOn());
        item.setTitleImageData(existingItem.getTitleImageData());
        item.setTitleImageMime(existingItem.getTitleImageMime());
        item.setTitleImageName(existingItem.getTitleImageName());

        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> ModifyItem(ItemEntity item, UserEntity user, MultipartFile images) throws IOException {
//        if (user == null) {
//            return ModifyItemResult.NOT_SIGNED;
//        }
        GoodsVo existingItem = this.goodsMapper.selectItemByIndex(item.getIndex());
        if (existingItem == null) {
            return ModifyItemResult.NO_SUCH_Item;
        }

//        if (!existingItem.getUserEmail().equals(user.getEmail())) {
//            return ModifyItemResult.NOT_ALLOWED;
//        }

        //?????? ????????? ????????? set????????? > ????????? ????????? ?????????
        existingItem.setCategoryId(item.getCategoryId());
        existingItem.setSellerIndex(item.getSellerIndex());
        existingItem.setItemName(item.getItemName());
        existingItem.setItemDetail(item.getItemDetail());
        existingItem.setPrice(item.getPrice());
        existingItem.setCount(item.getCount());
        existingItem.setCreatedOn(new Date());


        existingItem.setTitleImageData(images.getBytes());
        existingItem.setTitleImageMime(images.getContentType());
        existingItem.setTitleImageName(images.getName());

        System.out.println(item.getTitleImageName() == null ? "null" : "good");

        return this.goodsMapper.updateItem(existingItem) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> deleteItem(ItemEntity item, UserEntity user) { // ??????????????? ????????????5????????? ?????? ????????? ?????? ????????????
        // ????????? ??????  ?????? ?????? : ?????? ??????, ??????: deleteArticle =0
        // ???????????? ??????????????? + ??????????????? ???????????? ??? ?????? ?????? ?????? : ????????????, ?????? ?????? ????????? ?????????????????? ??????????????????????????? ???????????? ????????? ??? n
        // ??????????????? ???????????? ???????????? ????????? : null??????
        ItemEntity existingItem = this.goodsMapper.selectItemByIndex(item.getIndex());//??? ?????? ????????? article.getIndex() ??? ???????????? ????????? ????????? ??????

        if (existingItem == null) {
            return CommonResult.FAILURE;
        }

        if (user == null || !user.isAdmin()) { // ?????? existingCommentfh ??? ????????????
            return CommonResult.FAILURE;
        }

        item.setCategoryId(existingItem.getCategoryId());
        return this.goodsMapper.deleteItemByIndex(item.getIndex()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> addCartItem(UserEntity user, CartEntity cart) throws IOException, RollbackException {
        if (user == null) {
            return AddReviewResult.NOT_SIGNED;
        }
        cart.setUserEmail(user.getEmail());
        if (this.goodsMapper.insertCartItem(cart) == 0) {
            return AddReviewResult.FAILURE;
        }
        return AddReviewResult.SUCCESS;
    }

    public int getItemCount(String categoryId) {
        return this.goodsMapper.selectItemsCount(categoryId);
    }

    public int getReviewCount(int itemIndex) {
        return this.goodsMapper.selectReviewCountByItemIndex(itemIndex);
    }

    @Transactional
    public Enum<? extends IResult> deleteColors(ItemColorEntity[] colors) {
        ItemColorEntity[] existingItemColor = new ItemColorEntity[colors.length];
        for (ItemColorEntity color : colors) {
            for (int i = 0; i < colors.length; i++) {
                existingItemColor[i] = this.goodsMapper.selectItemColorByItemIndexColor(color.getItemIndex(), color.getColor());
                color.setColor(existingItemColor[i].getColor());
            }

            if (existingItemColor != null && this.goodsMapper.deleteItemColorByItemIndexColor(color.getItemIndex(), color.getColor()) > 0) {
                return CommonResult.SUCCESS;
            }
        }
        return CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> deleteSizes(ItemSizeEntity[] sizes) {
        ItemSizeEntity[] existingItemSize = new ItemSizeEntity[sizes.length];
        for (ItemSizeEntity size : sizes) {
            for (int i = 0; i < sizes.length; i++) {
                existingItemSize[i] = this.goodsMapper.selectItemSizeByItemIndexSize(size.getItemIndex(), size.getSize());
                size.setSize(existingItemSize[i].getSize());
            }

            if (existingItemSize != null && this.goodsMapper.deleteItemSizeByItemIndexSize(size.getItemIndex(), size.getSize()) > 0) {
                return CommonResult.SUCCESS;
            }
        }
        return CommonResult.FAILURE;
    }

    public int[] getIndex() {
        return this.goodsMapper.selectIndex();
    }

}
