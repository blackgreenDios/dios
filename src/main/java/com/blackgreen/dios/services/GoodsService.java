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


//        item = this.goodsMapper.selectItemByIndex(item.getIndex()); //item의 getIndex를 넘겨준적 없으니깐 0일것임,
//                                                                      그래서 값을 넘겨야함/ 그런데 이건 select해와서 그런거임. insert하는데 굳이 고를 필요 없음
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
                (paging.requestPage - 1) * paging.countPerPage); //상품 index를 기준으로 review select

        for (ReviewVo review : reviews) {
            ReviewImageEntity[] reviewImage = this.goodsMapper.selectReviewImagesByGoodsIndexExceptData(review.getIndex());
            int[] reviewImageIndexes = Arrays.stream(reviewImage).mapToInt(ReviewImageEntity::getIndex).toArray();
            review.setImageIndexes(reviewImageIndexes);
        }

        return reviews;
    }

    public ReviewVo[] getReviews(int itemIndex) {

        ReviewVo[] reviews = this.goodsMapper.selectReviewsByGoodsIndex(itemIndex); //상품 index를 기준으로 review select
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

    /*리뷰 삭제*/
    public Enum<? extends IResult> deleteReview(UserEntity user, ReviewEntity review) {

        ReviewEntity existingReview = this.goodsMapper.selectReviewByIndex(review.getIndex());//이 안에 적어준 comment.getIndex() 은 자스에서 넘어온 인덱스 값임
        if (existingReview == null) {
            return ReviewDeleteResult.NO_SUCH_Review;
        }
        if (user == null || !user.getEmail().equals(existingReview.getUserEmail())) {
            return ReviewDeleteResult.NOT_ALLOWED;
        }
//        if (user == null || !user.getEmail().equals(existingReview.getUserEmail())) { // 쌤은 existingCommentfh 로 바꿨는데
//            return ReviewDeleteResult.NOT_ALLOWED;
//        }
        return this.goodsMapper.deleteReviewByIndex(review.getIndex()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public Enum<? extends IResult> recoverReview(ReviewEntity review, UserEntity user) {
        // 수정 성공
        // 실패
        // 수정할 댓글 없음
        // 로그인이 안되어있고 + 수정하려는 댓글이 니 댓글이 아닌 경우

        ReviewEntity existingReview = this.goodsMapper.selectReviewByIndex(review.getIndex());
        if (existingReview == null) {
            return ReviewDeleteResult.NO_SUCH_Review;
        }
        if (user == null || !user.getEmail().equals(existingReview.getUserEmail())) {
            return ReviewDeleteResult.NOT_ALLOWED;
        }

        existingReview.setContent(review.getContent());

        // 점수 수정한게 있을때만 새로운 정보를 set 해주기
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
        // 상품 구매 하지 않은 사람에게 권한없음 return
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
        // 수정 성공
        // 실패
        // 수정할 댓글 없음
        // 로그인이 안되어있고 + 수정하려는 댓글이 니 댓글이 아닌 경우

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

        item.setCategoryId(existingItem.getCategoryId());// 이게 cad
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

        //새로 저장할 내용을 set해주깅 > 수정된 내용이 저장됨
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
    public Enum<? extends IResult> deleteItem(ItemEntity item, UserEntity user) { // 서비스에서 매개변수5개까진 ㄱㅊ 초과는 뭔가 문제있음
        // 게시글 삭제  결과 예상 : 삭제 성공, 실패: deleteArticle =0
        // 로그인이 안되어있고 + 삭제하려는 게시글이 니 글이 아닌 경우 : 보안조치, 자스 같은 경우엔 클라이언트가 수정할수있기때문에 백에서도 해줘야 함 n
        // 삭제하려는 게시글이 존재하지 않을때 : null일때
        ItemEntity existingItem = this.goodsMapper.selectItemByIndex(item.getIndex());//이 안에 적어준 article.getIndex() 은 자스에서 넘어온 인덱스 값임

        if (existingItem == null) {
            return CommonResult.FAILURE;
        }

        if (user == null || !user.isAdmin()) { // 쌤은 existingCommentfh 로 바꿨는데
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
