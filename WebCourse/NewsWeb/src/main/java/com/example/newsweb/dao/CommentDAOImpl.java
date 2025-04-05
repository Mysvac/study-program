package com.example.newsweb.dao;

import com.example.newsweb.model.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentDAOImpl implements CommentDAO{

    // 定义一些屏蔽词
    public static final List<String> BLOCKED_WORDS = Arrays.asList(
            "傻逼", "操", "贱人", "死丫头", "傻逼男", "狗日的",  // 脏话
            "杀人", "砍死", "爆炸", "炸弹", "自杀", "打死你",  // 暴力
            "性爱", "黄色", "AV", "妓女", "大鸡巴", "奶子",  // 色情
            "黑鬼", "白痴", "死穆斯林", "死日本人",  // 种族歧视
            "共产党", "毛泽东", "六四", "法轮功",  // 政治敏感
            "免费", "中奖", "链接点击", "信用卡信息"  // 恶意广告
    );
    @Override
    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (news_id, username, comment_text, date) VALUES (?, ?, ?, ?)";

        // 获取评论内容
        String commentText = comment.getCommentText();

        // 调用过滤屏蔽词的功能
        String filteredCommentText = filterBlockedWords(commentText);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, comment.getNewsId());
            stmt.setString(2, comment.getUsername());
            stmt.setString(3, filteredCommentText);  // 使用过滤后的评论内容
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));  // 保存评论时间

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 过滤评论中的屏蔽词
    private String filterBlockedWords(String commentText) {
        if (commentText == null || commentText.isEmpty()) {
            return commentText;
        }

        // 将屏蔽词替换为星号（*）
        for (String blockedWord : BLOCKED_WORDS) {
            commentText = commentText.replaceAll("(?i)" + blockedWord, "****");  // (?i)表示忽略大小写
        }

        return commentText;
    }

    @Override
    public List<Comment> getCommentsByNewsId(int newsId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE news_id = ? ORDER BY date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setNewsId(rs.getInt("news_id"));
                comment.setUsername(rs.getString("username"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setDate(rs.getTimestamp("date"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }
}
