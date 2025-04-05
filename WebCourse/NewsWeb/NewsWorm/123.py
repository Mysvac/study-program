import pymysql
import csv
import chardet

# 数据库连接
connection = pymysql.connect(
    host='localhost',    # 数据库主机
    user='root',         # 数据库用户名
    password='itintin0811.',   # 数据库密码
    database='news123',     # 数据库名称
    charset='utf8mb4'    # 设置字符集
)

# 创建游标
cursor = connection.cursor()

# 获取CSV文件路径
csv_file_path = 'news_details(1).csv'

# 检测并打印文件编码
def detect_encoding(file_path):
    with open(file_path, 'rb') as file:
        raw_data = file.read()
        result = chardet.detect(raw_data)
        return result['encoding']

detected_encoding = detect_encoding(csv_file_path)
print(f"Detected encoding: {detected_encoding}")

# 打开CSV文件，使用检测到的编码或默认使用 utf-8-sig
with open(csv_file_path, 'r', encoding=detected_encoding or 'utf-8-sig', errors='replace') as file:
    reader = csv.DictReader(file)

    # 创建一个列表来存储所有的插入数据
    insert_data = []

    # 遍历每一行数据并准备插入到数据库中
    for row in reader:
        # 获取每个字段
        title = row.get('标题', '')
        date = row.get('日期', '')
        summary = row.get('内容简介', '')
        link = row.get('链接', '')
        image_link = row.get('图片链接', '')

        # 如果日期格式不符合，进行格式转换（例如：2024.12.13 转为 2024-12-13）
        if '.' in date:
            date = date.replace('.', '-')

        content = row.get('新闻详情内容', '[]')
        images = row.get('详情页图片链接', '[]')
        author = row.get('供稿', '[]')

        # 将每行数据添加到 insert_data 列表
        insert_data.append((title, date, summary, link, image_link, content, images, author))

    # 使用 executemany 批量插入数据
    query = """
    INSERT INTO newsdetail (title, date, summary, link, image_link, content, images, author)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    """
    try:
        cursor.executemany(query, insert_data)
        connection.commit()  # 提交事务
        print("数据成功导入到数据库！")
    except pymysql.MySQLError as e:
        print(f"数据库错误: {e}")
        connection.rollback()  # 回滚事务
    finally:
        cursor.close()  # 关闭游标
        connection.close()  # 关闭连接