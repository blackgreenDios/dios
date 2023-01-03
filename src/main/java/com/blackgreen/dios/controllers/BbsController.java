package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.bbs.*;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.bbs.WriteResult;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.services.BbsService;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
import com.blackgreen.dios.vos.bbs.CommentVo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Controller(value = "com.blackgreen.dios.controllers.BbsController")
@RequestMapping(value = "/dios")
public class BbsController {
    private final BbsService bbsService;

    @Autowired
    public BbsController(BbsService bbsService) {
        this.bbsService = bbsService;
    }

    @GetMapping(value = "write",produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(@SessionAttribute(value = "user", required = false) UserEntity user,
                                 @RequestParam(value = "bid",  required = false) String bid) {

        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/member/login");
        } else {
            modelAndView = new ModelAndView("bbs/write");
            if (bid == null || this.bbsService.getBoard(bid) == null) {
                modelAndView.addObject("result", CommonResult.FAILURE.name());
            } else {
                modelAndView.addObject("result", CommonResult.SUCCESS.name());
                BoardEntity board = this.bbsService.getBoard(bid);

                modelAndView.addObject("board", board);
                modelAndView.addObject("bid", board);

            }
        }
        return modelAndView;

    }
    @PostMapping(value = "write",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(@SessionAttribute(value = "user", required = false) UserEntity user,
                            @RequestParam(value = "bid", required = false) String bid, ArticleEntity article) {
        Enum<?> result;
        int index=0;
        JSONObject responseObject = new JSONObject();
        if (user == null) {
            result = WriteResult.NOT_ALLOWED;
        } else if (bid == null) {
            result = WriteResult.NO_SUCH_BOARD;

        } else {
            article.setUserEmail(user.getEmail());
            article.setBoardId(bid);
            result = this.bbsService.write(article);
            if (result == CommonResult.SUCCESS) {
                responseObject.put("aid", article.getIndex());
            }
        }
        responseObject.put("result",result.name().toLowerCase());
        return responseObject.toString();
    }
    @GetMapping(value = "read",produces = MediaType.TEXT_HTML_VALUE)

    @ResponseBody
    public ModelAndView getRead(@SessionAttribute(value = "user",required = false)UserEntity user,
                                    @RequestParam(value = "aid")int aid){
        ModelAndView modelAndView = new ModelAndView("bbs/read");
        ArticleReadVo article =this.bbsService.readArticle(aid);
        modelAndView.addObject("article",article);

        if (article!=null){
            BoardEntity board=this.bbsService.getBoard(article.getBoardId());
            modelAndView.addObject("board",board);
            modelAndView.addObject("isLiked",article.isLiked());
            modelAndView.addObject("isSigned",article.isSigned());

        }
        return modelAndView;

    }
    @PostMapping(value = "read",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRead(@SessionAttribute(value = "user",required = false)UserEntity user, CommentEntity comment){
        JSONObject responseObject = new JSONObject();
        if (user == null) {
            responseObject.put("result",CommonResult.FAILURE.name().toLowerCase());
        }else {
            comment.setUserEmail(user.getEmail());
            Enum<?> result=this.bbsService.writeComment(comment);
            responseObject.put("result",result.name().toLowerCase());

        }
        return responseObject.toString();
    }
    @DeleteMapping(value = "read",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteRead(@SessionAttribute(value = "user",required = false)UserEntity user,
                             @RequestParam(value = "aid")int aid){
        ArticleEntity article=new ArticleEntity();
        article.setIndex(aid);
        Enum<?> result=this.bbsService.deleteArticle(article,user);
        JSONObject responseObject=new JSONObject();
        responseObject.put("result",result.name().toLowerCase());
        if(result==CommonResult.SUCCESS){
            responseObject.put("bid",article.getBoardId());
        }
        return responseObject.toString();

    }
    @GetMapping(value = "comment",produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getComment(@SessionAttribute(value = "user",required = false) UserEntity user,                                @RequestParam(value = "aid") int articleIndex) {
        JSONArray responseArray=new JSONArray();
        CommentVo[] comments=this.bbsService.getComments(articleIndex,user);
        for(CommentVo comment:comments){
            JSONObject commentObject=new JSONObject();
            commentObject.put("index",comment.getIndex());
            commentObject.put("commentIndex",comment.getCommentIndex());
            commentObject.put("userEmail",comment.getUserEmail());
            commentObject.put("articleIndex",comment.getArticleIndex());
            commentObject.put("content",comment.getContent());
            commentObject.put("writtenOn",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comment.getWrittenOn()));
            commentObject.put("userNickname",comment.getUserNickname());
//            commentObject.put("isSigned",user!=null);
//            commentObject.put("isMine",user!=null && user.getEmail().equals(comment.getUserEmail()));
            commentObject.put("isSigned",comment.isSigned());
            commentObject.put("isMine",user!=null && user.getEmail().equals(comment.getUserEmail()));
            commentObject.put("isLiked",comment.isLiked());
            commentObject.put("likeCount",comment.getLikeCount());
            commentObject.put("isSecret",comment.getSecret());

            responseArray.put(commentObject);
        }

        return responseArray.toString();
    }
    @DeleteMapping(value = "comment",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteComment(@SessionAttribute(value ="user",required = false)UserEntity user,
                                CommentEntity comment){
        Enum<?> result=this.bbsService.deleteComment(comment,user);
        JSONObject responseJson=new JSONObject();
        responseJson.put("result",result.name().toLowerCase());
        return responseJson.toString();
    }
    @PatchMapping(value = "comment",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchComment(@SessionAttribute(value ="user",required = false)UserEntity user,
                               CommentEntity comment){
        Enum<?> result=this.bbsService.updateComment(comment,user);
        JSONObject responseJson=new JSONObject();
        responseJson.put("result",result.name().toLowerCase());
        return responseJson.toString();

    }
    @PostMapping(value = "comment-like",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postCommentLike(@SessionAttribute(value = "user",required = false)UserEntity user,
                                  CommentLikeEntity commentLike){
        JSONObject responseJson=new JSONObject();
        Enum<?> result=this.bbsService.likeComment(commentLike,user);
        responseJson.put("result",result.name().toLowerCase());
        return responseJson.toString();
    }
    @DeleteMapping(value = "comment-like",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteCommentLike(@SessionAttribute(value = "user",required = false)UserEntity user,
                                    CommentLikeEntity commentLike){
        JSONObject responseJson=new JSONObject();
        Enum<?> result=this.bbsService.unlikeComment(commentLike,user);
        responseJson.put("result",result.name().toLowerCase());
        return responseJson.toString();
    }
    //다운로드
    @RequestMapping(value = "image",method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id")int id){
        ImageEntity image =this.bbsService.getImage(id);
        if(image==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers=new HttpHeaders();
        headers.add("Content-Type",image.getFileMime());
        return new ResponseEntity<>(image.getData(),headers,HttpStatus.OK);

    }

    //업로드
    @RequestMapping(value = "image",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postImage(@RequestParam(value = "upload") MultipartFile file) throws IOException {
        ImageEntity image=new ImageEntity();
        image.setFileName(file.getOriginalFilename());
        image.setFileMime(file.getContentType());
        image.setData(file.getBytes());

        Enum<?> result=this.bbsService.addImage(image);
        JSONObject responseObject=new JSONObject();
        responseObject.put("result",result.name().toLowerCase());
        if(result==CommonResult.SUCCESS){
            responseObject.put("url","http://localhost:8080/dios/image?id="+image.getIndex());

        }

        return responseObject.toString();
    }
    @GetMapping(value = "modify",produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(@SessionAttribute(value = "user",required = false)UserEntity user,
                                  @RequestParam(value = "aid")int aid){
        ModelAndView modelAndView = new ModelAndView("bbs/modify");

        ArticleEntity article=new ArticleEntity();
        article.setIndex(aid);
        Enum<?> result=this.bbsService.prepareModifyArticle(article,user);
        modelAndView.addObject("article",article);
        if(result==CommonResult.SUCCESS){
            modelAndView.addObject("board",this.bbsService.getBoard(article.getBoardId()));
        }
        modelAndView.addObject("result",result.name());

        return modelAndView;

    }
    @PatchMapping(value = "modify",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModify (@SessionAttribute(value = "user",required = false)UserEntity user,
                               @RequestParam(value = "aid")int aid,
                               ArticleEntity article) {

        article.setIndex(aid);
        Enum<?> result=this.bbsService.modifyArticle(article,user);
        JSONObject responseObject=new JSONObject();
        responseObject.put("result",result.name().toLowerCase());
        if (result==CommonResult.SUCCESS){
            responseObject.put("aid",aid);
        }
        return responseObject.toString();

    }
    @GetMapping(value = "list",produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@RequestParam(value = "bid")String bid,
                                @RequestParam(value = "page",required = false,defaultValue = "1")Integer page,
                                @RequestParam(value = "criterion",required = false)String criterion,
                                @RequestParam(value = "keyword",required = false)String keyword){
        page=Math.max(1,page);

        ModelAndView modelAndView=new ModelAndView("bbs/list");
        BoardEntity board=this.bbsService.getBoard(bid);
        modelAndView.addObject("board",board);
        if (board!=null){
            int totalCount=this.bbsService.getArticleCount(board,criterion,keyword);

            PagingModel paging=new PagingModel(totalCount,page);
            modelAndView.addObject("paging",paging);

            ArticleReadVo[] articles=this.bbsService.getArticles(board,paging,criterion,keyword);
            modelAndView.addObject("articles",articles);

        }
        return modelAndView;
    }
//    @PostMapping(value = "article-like",produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public String postArticleLike(@SessionAttribute(value = "user",required = false)UserEntity user,
//                                  ArticleLikeEntity articleLike){
//        JSONObject responseJson=new JSONObject();
//        Enum<?> result=this.bbsService.likeArticle(articleLike,user);
//        responseJson.put("result",result.name().toLowerCase());
//        return responseJson.toString();
//    }
//    @DeleteMapping(value = "article-like",produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public String deleteArticleLike(@SessionAttribute(value = "user",required = false)UserEntity user,
//                                    ArticleLikeEntity articleLike){
//        JSONObject responseJson=new JSONObject();
//        Enum<?> result=this.bbsService.unlikeArticle(articleLike,user);
//        responseJson.put("result",result.name().toLowerCase());
//        return responseJson.toString();
//    }

    @PostMapping(value = "article-like",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postArticleLike(@SessionAttribute(value = "user",required = false)UserEntity user,
                                  @RequestParam(value = "aid")int aid){
        JSONObject responseObject = new JSONObject();
        ArticleReadVo article =this.bbsService.getArticle(aid);
        Enum<?> result;
        if(article==null){
            result=CommonResult.FAILURE;
        }else {
            result=this.bbsService.toggleArticleLike(article,user);
            if(result ==CommonResult.SUCCESS){
                responseObject.put("isLiked",this.bbsService.getArticleLiked(article,user));
                responseObject.put("likeCount",article.getLikeCount());
                responseObject.put("aid", aid);



            }
        }
        responseObject.put("result",result.name().toLowerCase());
        return responseObject.toString();
    }


}
