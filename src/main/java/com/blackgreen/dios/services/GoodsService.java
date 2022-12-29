package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.goods.AddReviewResult;
import com.blackgreen.dios.enums.goods.GoodsResult;
import com.blackgreen.dios.enums.goods.ReviewDeleteResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IGoodsMapper;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.vos.GoodsVo;
import com.blackgreen.dios.vos.ReviewVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service(value = "com.blackgreen.dios.services.GoodsService")
public class GoodsService {
    private final IGoodsMapper goodsMapper;

    @Autowired
    public GoodsService(IGoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public ItemColorEntity[] getColor() {
        return this.goodsMapper.selectColor();
    }

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

    public SizeEntity[] getSize() {
        return this.goodsMapper.selectSize();
    }

    public GoodsVo getItem(int index) {
        return this.goodsMapper.selectItemByIndexExceptImage(index);
    }

    // 대표 이미지 업데이트
//    @Transactional
//    public Enum<? extends IResult> updateTitleImage(ItemEntity item, MultipartFile[] images) throws UnexpectedRollbackException {
//
//        if (images != null && images.length > 0) {
//            for (MultipartFile image : images) {
//                ItemEntity itemTitleImage = this.goodsMapper.selectItemByIndex(item.getIndex());
//                itemTitleImage.setTitleImageData(item.getTitleImageData());
//                itemTitleImage.setTitleImageMime(item.getTitleImageMime());
//                itemTitleImage.setTitleImageName(item.getTitleImageName());
//            }
//        }
//        return CommonResult.SUCCESS;
//    }

    @Transactional
    public Enum<? extends IResult> addItem(ItemEntity item, MultipartFile images) throws IOException {


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

    public ItemImgEntity getImage(int index) {
        return this.goodsMapper.selectItemImageByIndex(index);
    }


    public SellerEntity getSellerImage(int index) {
        return this.goodsMapper.selectSellerImageByIndex(index);
    }

    public ItemEntity getItemTitleImage(int index) {
        return this.goodsMapper.selectItemTitleImageByIndex(index);
    }

    public ItemColorEntity getItemColor(String id) {
        return this.goodsMapper.selectColorById(id);
    }

    public Enum<? extends IResult> addItemImage(ItemImgEntity image) {
        return this.goodsMapper.insertItemImage(image) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    public ReviewVo[] getReviews(int itemIndex) {

        ReviewVo[] reviews = this.goodsMapper.selectReviewsByGoodsIndex(itemIndex);

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
    public Enum<? extends IResult> deleteReview(ReviewEntity review) {
        ReviewEntity existingReview = this.goodsMapper.selectReviewByIndex(review.getIndex());//이 안에 적어준 comment.getIndex() 은 자스에서 넘어온 인덱스 값임
        if (existingReview == null) {
            return ReviewDeleteResult.NO_SUCH_Review;
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

        return this.goodsMapper.updateReview
                (existingReview) > 0 ?
                CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> addReview(UserEntity user, ReviewEntity review, MultipartFile[] images) throws IOException, RollbackException {

//        review.setUserEmail("guswl049012@naver.com");

        if (user == null) {
            return AddReviewResult.NOT_SIGNED;
        }
        review.setUserEmail(user.getEmail());
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
}
