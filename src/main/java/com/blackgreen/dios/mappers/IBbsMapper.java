package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.bbs.*;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
import com.blackgreen.dios.vos.bbs.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IBbsMapper {
    BoardEntity selectBoardById(@Param(value = "id") String id);

    int insertArticle(ArticleEntity article);

    ArticleReadVo selectArticleByIndex(@Param(value = "index") int index);

    int updateArticle(ArticleEntity article);

    ArticleReadVo[] selectArticlesByBoardId(@Param(value = "boardId") String boardId,
                                            @Param(value = "criterion") String criterion,
                                            @Param(value = "keyword") String keyword,
                                            @Param(value = "limit") int limit,
                                            @Param(value = "offset") int offset);

    int insertComment(CommentEntity comment);

    int deleteArticleByIndex(@Param(value = "index") int index);

    int deleteCommentByIndex(@Param(value = "index") int index);

    CommentVo[] selectCommentByArticleIndex(@Param(value = "articleIndex") int articleIndex,
                                            @Param(value = "userEmail") String userEmail);

    CommentEntity selectCommentByIndex(@Param(value = "index") int index);

    int updateComment(CommentEntity comment);

    int deleteCommentLike(CommentLikeEntity commentLike);

    int insertCommentLike(CommentLikeEntity commentLike);

    ImageEntity selectImageByIndex(@Param(value = "index") int index);

    int insertImage(ImageEntity image);

    int selectArticleCountByBoardId(@Param(value = "boardId") String boardId,
                                    @Param(value = "criterion") String criterion,
                                    @Param(value = "keyword") String keyword);


    ArticleLikeEntity selectArticleLike(@Param(value = "articleIndex") int articleIndex,
                                        @Param(value = "userEmail") String userEmail);

    int insertArticleLike(ArticleLikeEntity articleLike);

    int deleteArticleLike(ArticleLikeEntity articleLike);

    //콩지
    int selectArticleCountByUserEmailFromQna(@Param(value = "userEmail") String userEmail,
                                             @Param(value = "criterion") String criterion,
                                             @Param(value = "keyword") String keyword);

    int selectArticleCountByUserEmailFromFree(@Param(value = "userEmail") String userEmail,
                                              @Param(value = "criterion") String criterion,
                                              @Param(value = "keyword") String keyword);


    ArticleReadVo[] selectArticlesByUserEmailFree(@Param(value = "userEmail") String userEmail,
                                                  @Param(value = "criterion") String criterion,
                                                  @Param(value = "keyword") String keyword,
                                                  @Param(value = "limit") int limit,
                                                  @Param(value = "offset") int offset);

    ArticleReadVo[] selectArticlesByUserEmailQna(@Param(value = "userEmail") String userEmail,
                                                 @Param(value = "criterion") String criterion,
                                                 @Param(value = "keyword") String keyword,
                                                 @Param(value = "limit") int limit,
                                                 @Param(value = "offset") int offset);

    UserEntity selectAdminByUser(@Param(value = "email") String email);


}
