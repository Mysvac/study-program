import re
import requests
import pandas
from config import mysql_local
import pymysql

class NewsInfo:
    def init (self):
        self.conn = pymysql.connect(**mysql_local)

        self.cursor = self.conn.cursor()

    #1.获取响应数据
    def get_res(self,url):
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 SLBrowser/9.0.5.9101 SLBChan/105 SLBVPV/64-bit'
        }
        res = requests.get(url, headers=headers)
        res.encoding = res.apparent_encoding
        return res

    #2.解析数据
    def date_re(self,res):
        text = res.text
        news_inform1 = re.findall('<div class="news_time">(.*?)</div>', text)
        news_inform1_first_14 = news_inform1[:14]
        news_inform2 = re.findall('<div class="news_title">(.*?)</div>', text)
        news_inform2_first_14 = news_inform2[:14]
        news_inform3 = re.findall(r'<div class="news_text">\s*(.*?)\s*</div>', text, re.DOTALL)
        news_inform3_first_14 = news_inform3[:14]
        picture_url = re.findall('<div class="news_imgs"><img src=(.*?)>', text)
        cleaned_url = [item.split("'")[1] for item in picture_url]
        final_urls = ['https://www.usst.edu.cn/' + url.lstrip('/') for url in cleaned_url]

        return  news_inform1_first_14,news_inform2_first_14,news_inform3_first_14,final_urls

    #3.存储
    def save_csv(self,time,title,inform,picture_url):

        df = pandas.DataFrame()
        df["时间"] = time
        df["标题"] = title
        df["内容"] = inform
        df["图片url"] = picture_url
        df.to_csv("新闻信息.csv", index=False)

    def save_mysql(self,time,title,inform,picture_url):
        data=list(zip(time, title, inform, picture_url))
        sql="INSERT INTO news_info (time, title, inform, picture_url)VALUES (%s,%s,%s,%s);";
        self.conn = pymysql.connect(**mysql_local)
        self.cursor = self.conn.cursor()
        self.cursor.executemany(sql,data)
        self.conn.commit()

    def main(self):
        time_list=[]
        title_list=[]
        inform_list=[]
        url_list=[]
        for num in range(1,11):
            page_url=f"https://www.usst.edu.cn/slyw/list{num}.htm"
            res=self.get_res(page_url)
            time,title,inform,picture_url=self.date_re(res)
            self.save_mysql(time,title,inform,picture_url)
            time_list.extend(time)
            title_list.extend(title)
            inform_list.extend(inform)
            url_list.extend(picture_url)


        self.save_csv(time_list,title_list,inform_list,url_list)



if __name__ == '__main__':
    fi = NewsInfo()
    fi.main()

