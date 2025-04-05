package  com.example.newsweb.service;

import com.example.newsweb.dao.NewsDAOImpl;
import com.example.newsweb.model.News;
import java.sql.SQLException;

public class TestNewsDAO {

    public static void main(String[] args) {
        // 创建 NewsDAOImpl 实例
        NewsDAOImpl newsDAO = new NewsDAOImpl();

        // 测试的新闻 ID，可以替换成数据库中存在的 ID
        int testNewsId = 1;

        try {
            // 调用 getNewsById 方法
            News news = newsDAO.getNewsById(testNewsId);

            // 如果返回结果不为 null，输出新闻详情
            if (news != null) {
                System.out.println("新闻 ID: " + news.getId());
                System.out.println("标题: " + news.getTitle());
                System.out.println("日期: " + news.getDate());
                System.out.println("链接: " + news.getLink());
                System.out.println("图片链接: " + news.getImageLink());

                System.out.println("内容:");
                for (String contentItem : news.getContent()) {
                    System.out.println(contentItem);
                }

                System.out.println("图片:");
                for (String image : news.getImages()) {
                    System.out.println(image);
                }

                System.out.println("作者:");
                for (String author : news.getAuthor()) {
                    System.out.println(author);
                }
            } else {
                System.out.println("没有找到 ID 为 " + testNewsId + " 的新闻！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
