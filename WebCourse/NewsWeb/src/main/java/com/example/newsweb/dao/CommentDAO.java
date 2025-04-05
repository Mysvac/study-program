package com.example.newsweb.dao;

import com.example.newsweb.model.Comment;
import java.util.List;

public interface CommentDAO extends DAO{
    // 添加评论
    boolean addComment(Comment comment);

    // 获取指定新闻的所有评论
    List<Comment> getCommentsByNewsId(int newsId);
}
