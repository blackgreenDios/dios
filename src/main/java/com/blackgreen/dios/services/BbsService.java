package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.bbs.*;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.bbs.CommentDeleteResult;
import com.blackgreen.dios.enums.bbs.CommonUpdateResult;
import com.blackgreen.dios.enums.bbs.ModifyArticleResult;
import com.blackgreen.dios.enums.bbs.WriteResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IBbsMapper;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
import com.blackgreen.dios.vos.bbs.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "com.blackgreen.dios.services.BbsServices")
public class BbsService {
    private final IBbsMapper bbsMapper;

    @Autowired
    public BbsService(IBbsMapper bbsMapper) {
        this.bbsMapper=bbsMapper;
    }

    public BoardEntity getBoard(String id){
        return this.bbsMapper.selectBoardById(id);
    }
    public Enum<? extends IResult> write(ArticleEntity article) {
        BoardEntity board = this.bbsMapper.selectBoardById(article.getBoardId());
        if (board == null) {
            return WriteResult.NO_SUCH_BOARD;
        }
        return this.bbsMapper.insertArticle(article) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public UserEntity getUser(UserEntity user){
        return  this.bbsMapper.selectAdminByUser(user.getEmail());
    }
    public ArticleReadVo readArticle(int index) {

        ArticleReadVo article=this.bbsMapper.selectArticleByIndex(index);
        if (article!=null){
            article.setView(article.getView()+1);
            this.bbsMapper.updateArticle(article);
        }
        return article;

    }
    public ArticleReadVo[] getArticles(BoardEntity board, PagingModel paging, String criterion, String keyword) {

        return this.bbsMapper.selectArticlesByBoardId(
                board.getId(),
                criterion,
                keyword,
                paging.countPerPage,
                (paging.requestPage-1)*paging.countPerPage);

    }
    public Enum<? extends IResult>writeComment(CommentEntity comment){
        ArticleEntity article=this.bbsMapper.selectArticleByIndex(comment.getArticleIndex());
        if(article==null ){
            return CommonResult.FAILURE;
        }
        return this.bbsMapper.insertComment(comment)>0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
    public Enum<? extends IResult> deleteArticle(ArticleEntity article, UserEntity user){
        ArticleEntity existingArticle=this.bbsMapper.selectArticleByIndex(article.getIndex());
        if(existingArticle==null){
            return CommonResult.FAILURE;
        }
        if(user==null || !user.getEmail().equals(existingArticle.getUserEmail())){
            return CommonResult.FAILURE;
        }

        article.setBoardId(existingArticle.getBoardId());

        return this.bbsMapper.deleteArticleByIndex(article.getIndex())>0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
    public CommentVo[] getComments(int articleIndex, UserEntity signedUser){
        return this.bbsMapper.selectCommentByArticleIndex(articleIndex,
                signedUser==null ? null :signedUser.getEmail());

    }
    public Enum<?extends IResult> deleteComment(CommentEntity comment, UserEntity user){
        CommentEntity existingComment=this.bbsMapper.selectCommentByIndex(comment.getIndex());
        if(existingComment==null){
            return CommentDeleteResult.NO_SUCH_COMMENT;
        }
        if(user==null || !user.getEmail().equals(existingComment.getUserEmail())){
            return CommentDeleteResult.NOT_ALLOWED;
        }
        return this.bbsMapper.deleteCommentByIndex(comment.getIndex())>0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
    public Enum<?extends IResult> updateComment(CommentEntity comment,UserEntity user){
        CommentEntity existingComment=this.bbsMapper.selectCommentByIndex(comment.getIndex());
        if(existingComment==null){
            return CommonUpdateResult.NO_SUCH_COMMENT;
        }
        if(user==null || !user.getEmail().equals(existingComment.getUserEmail())){
            return CommonUpdateResult.NOT_ALLOWED;
        }

        existingComment.setContent(comment.getContent());

        return this.bbsMapper.updateComment(existingComment)>0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
    public Enum<? extends IResult>likeComment(CommentLikeEntity commentLike, UserEntity user){
        if(user==null){
            return CommonResult.FAILURE;
        }
        commentLike.setUserEmail(user.getEmail());
        return this.bbsMapper.insertCommentLike(commentLike) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE ;

    }
    public Enum<? extends IResult> unlikeComment(CommentLikeEntity commentLike, UserEntity user){
        if(user==null){
            return CommonResult.FAILURE;
        }
        commentLike.setUserEmail(user.getEmail());
        return this.bbsMapper.deleteCommentLike(commentLike) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE ;

    }

    public ImageEntity getImage(int index){
        return this.bbsMapper.selectImageByIndex(index);

    }
    public Enum<? extends IResult> addImage(ImageEntity image){
        return  this.bbsMapper.insertImage(image)>0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;

    }
    public Enum<? extends IResult> prepareModifyArticle(ArticleEntity article, UserEntity user) {
        if(user==null){
            return ModifyArticleResult.NOT_SIGNED;
        }
        ArticleEntity existingArticle=this.bbsMapper.selectArticleByIndex(article.getIndex());
        if(existingArticle==null){
            return ModifyArticleResult.NO_SUCH_ARTICLE;
        }
        if (!existingArticle.getUserEmail().equals((user.getEmail()))){
            return ModifyArticleResult.NOT_ALLOWED;
        }
        article.setIndex(existingArticle.getIndex());
        article.setUserEmail(existingArticle.getUserEmail());
        article.setBoardId(existingArticle.getBoardId());
        article.setTitle(existingArticle.getTitle());
        article.setContent(existingArticle.getContent());
        article.setView(existingArticle.getView());
        article.setWrittenOn(existingArticle.getWrittenOn());
        article.setModifiedOn(existingArticle.getModifiedOn());
        return CommonResult.SUCCESS;

    }
    public Enum<? extends IResult> modifyArticle(ArticleEntity article, UserEntity user){
        if(user==null){
            return ModifyArticleResult.NOT_SIGNED;
        }
        ArticleEntity existingArticle=this.bbsMapper.selectArticleByIndex(article.getIndex());
        if (existingArticle==null){
            return ModifyArticleResult.NO_SUCH_ARTICLE;
        }
        if (!existingArticle.getUserEmail().equals(user.getEmail())){
            return ModifyArticleResult.NOT_ALLOWED;
        }
        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingArticle.setModifiedOn(new Date());
        return this.bbsMapper.updateArticle(existingArticle) >0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public int getArticleCount(BoardEntity board, String criterion, String keyword){
        return this.bbsMapper.selectArticleCountByBoardId(board.getId(),criterion, keyword);

    }
//    public Enum<? extends IResult>likeArticle(ArticleLikeEntity articleLike, UserEntity user){
//        if(user==null){
//            return CommonResult.FAILURE;
//        }
//        articleLike.setUserEmail(user.getEmail());
//        return this.bbsMapper.insertArticleLike(articleLike) > 0
//                ? CommonResult.SUCCESS
//                : CommonResult.FAILURE ;
//
//    }
//    public Enum<? extends IResult> unlikeArticle(ArticleLikeEntity  articleLike, UserEntity user){
//        if(user==null){
//            return CommonResult.FAILURE;
//        }
//        articleLike.setUserEmail(user.getEmail());
//        return this.bbsMapper.deleteArticleLike(articleLike) > 0
//                ? CommonResult.SUCCESS
//                : CommonResult.FAILURE ;
//
//    }
    public ArticleReadVo getArticle(int index){
        return this.bbsMapper.selectArticleByIndex(index);

    }
    public boolean getArticleLiked(ArticleEntity article,UserEntity user){
        if(user==null){
            return false;
        }
        return this.bbsMapper.selectArticleLike(article.getIndex(),user.getEmail()) !=null;
    }
    public Enum<? extends IResult> toggleArticleLike(ArticleEntity article,UserEntity user){
        if(user==null){
            return CommonResult.FAILURE;
        }
        ArticleLikeEntity articleLike = new ArticleLikeEntity();
        articleLike.setArticleIndex(article.getIndex());
        articleLike.setUserEmail(user.getEmail());

        if (this.getArticleLiked(article,user)){
            this.bbsMapper.deleteArticleLike(articleLike);


        }else {
            this.bbsMapper.insertArticleLike(articleLike);

        }
        return CommonResult.SUCCESS;
    }

    public Enum<? extends IResult> commentSecretCheck(CommentEntity comment,UserEntity user){
        CommentEntity secretComment = this.bbsMapper.selectCommentByIndex(comment.getIndex());{
            return CommonResult.FAILURE;

        }
    }


}
