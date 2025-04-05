package com.mythovac.configtemplate.component

import com.mythovac.configtemplate.entity.Goods
import com.opencsv.CSVReader
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStreamReader


/**
 * 程序启动监听
 * 创建表格，保证表格存在
 * 默认在程序启动时创建表格（如果不存在）
 * 根据参数，设置程序结束时，是否删除表格
 * */
@Component
class TableManager(private val jdbcTemplate: JdbcTemplate, private val passwordEncoder: PasswordEncoder) : CommandLineRunner {

    /**
     * 获取application.properties中的自定义参数
     * 控制是否在结束时删除数据表
     * true or false
     * */
    @Value("\${drop.tables.on.close}")
    private lateinit var dropTablesOnClose: String

    /**
     * 程序启动时，如果表不存在，自动创建
     * */
    override fun run(vararg args: String?) {
        // 创建数据表
        createTableUser()
        createTableUserProfile()
        createTableGoods()
        createTableCart()
        createTableOperation()
        createTableBill()
        createTriggerTrgOrders()
        createTriggerTrgUsers()
        // 获取数据信息，从excel表格中
        insertGoods()
        // 插入管理员的数据
        insertAdmin()
        // insertAdminProfile()
    }

    /**
     * 程序结束时，删除表
     * 可不启用，通过设置 drop.tables.on.close
     * */
    @PreDestroy
    fun onShutdown() {
        if(dropTablesOnClose.toBoolean()){
            dropTriggerTrgUsers()
            dropTriggerTrgOrders()
            dropTableBill()
            dropTableOperation()
            dropTableCart()
            dropTableGoods()
            dropTableUserProfile()
            dropTableUser()
            println("已删除表。")
        }
        else{
            println("当前选择不删除表。")
        }
    }


    /**
     * 执行SQL语句
     * 创建和删除表
     * */
    private fun createTableUser() { jdbcTemplate.execute(createTableUserSQL) }
    private fun dropTableUser() { jdbcTemplate.execute(dropTableUserSQL) }

    private fun createTableUserProfile(){ jdbcTemplate.execute(createTableUserProfileSQL) }
    private fun dropTableUserProfile() { jdbcTemplate.execute(dropTableUserProfileSQL) }

    private fun createTableGoods(){ jdbcTemplate.execute(createTableGoodsSQL) }
    private fun dropTableGoods() { jdbcTemplate.execute(dropTableGoodsSQL) }

    private fun createTableCart(){ jdbcTemplate.execute(createTableCartSQL) }
    private fun dropTableCart() { jdbcTemplate.execute(dropTableCartSQL) }

    private fun createTableOperation(){ jdbcTemplate.execute(createTableOrdersSQL) }
    private fun dropTableOperation() { jdbcTemplate.execute(dropTableOrdersSQL) }

    private fun createTableBill(){ jdbcTemplate.execute(createTableBillSQL) }
    private fun dropTableBill() { jdbcTemplate.execute(dropTableBillSQL) }


    private fun createTriggerTrgOrders(){ jdbcTemplate.execute(createTriggerTrgOrdersSQL) }
    private fun dropTriggerTrgOrders(){ jdbcTemplate.execute(dropTriggerTrgOrdersSQL) }

    private fun createTriggerTrgUsers() {jdbcTemplate.execute(createTriggerTrgUsersSQL)}
    private fun dropTriggerTrgUsers() {jdbcTemplate.execute(dropTriggerTrgUsersSQL)}

    // 创建初始的管理员账号
    private fun insertAdmin(){
        val adminPwd: String = passwordEncoder.encode("adminPwd")
        val insertAdminSQL = "INSERT INTO users (uid, password, grade) VALUES (?, ?, ?)"
        jdbcTemplate.update(insertAdminSQL, "admin", adminPwd,"admin")
    }


    // 插入商品信息
    private fun insertGoods(){
        val insertGoodsSQL = "INSERT INTO goods (goodsname, goodstype, stock, price, sales, author, profile, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        try{
            // 从静态资源的csv表中加载图书信息
            val csvFile: String = "static/goods.csv"
            val resource = ClassPathResource(csvFile)

            resource.inputStream.use { inputStream ->
                // 获取csv内容
                val reader = CSVReader(InputStreamReader(inputStream))

                // 插入数据库
                var line: Array<String>?
                reader.readNext() // 跳过标题
                while (reader.readNext().also { line = it } != null) {
                    if(line!!.size < 8) continue
                    val goods = Goods(
                        goodsid = 0,
                        goodsname = line[0],
                        goodstype = line[1],
                        stock = line[2].toInt(),
                        price = line[3].toInt(),
                        sales = line[4].toInt(),
                        author = line[5],
                        profile = line[6],
                        available = line[7].toInt())
                    jdbcTemplate.update(insertGoodsSQL, goods.goodsname, goods.goodstype, goods.stock, goods.price, goods.sales, goods.author, goods.profile, goods.available)
                }
                // 处理CSV文件的逻辑
            }

        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }


    /**
     * 具体的创建和删除的SQL语句
     * */
    // 账号基本信息表
    private val createTableUserSQL = """
        CREATE TABLE IF NOT EXISTS users (
        uid CHAR(23) PRIMARY KEY,
        password CHAR(63) NOT NULL,
        grade CHAR(7) NOT NULL CHECK( grade IN ('admin','vip','banned') )
        );
    """

    // 个性化信息表
    private val createTableUserProfileSQL = """
        CREATE TABLE IF NOT EXISTS userProfile (
        uid CHAR(23) PRIMARY KEY,
        gender CHAR(7) CHECK( gender IN ('male','female','secrecy') ),
        address CHAR(47),
        username CHAR(23),
        email CHAR(47),
        profile CHAR(47),
        CONSTRAINT FK_userProfile FOREIGN KEY (uid) REFERENCES users (uid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    // 书籍信息表
    private val createTableGoodsSQL = """
        CREATE TABLE IF NOT EXISTS goods (
        goodsid BIGINT AUTO_INCREMENT PRIMARY KEY,
        goodsname CHAR(23) NOT NULL,
        goodstype CHAR(13) NOT NULL,
        stock INT NOT NULL CHECK( stock >= 0 ),
        price INT NOT NULL CHECK( price >= 0 ),
        sales INT NOT NULL CHECK( sales >= 0 ),
        author CHAR(23) NOT NULL,
        profile CHAR(255) NOT NULL,
        available BOOL DEFAULT 0 CHECK ( available IN (0,1) )
        );
    """

    // 购物车表
    private val createTableCartSQL = """
        CREATE TABLE IF NOT EXISTS cart (
        uid CHAR(23) NOT NULL,
        goodsid BIGINT NOT NULL,
        amount INT NOT NULL CHECK( amount > 0 ),
        PRIMARY KEY (uid, goodsid),
        CONSTRAINT KF_cart_account FOREIGN KEY (uid) REFERENCES users (uid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        CONSTRAINT KF_cart_gid FOREIGN KEY (goodsid) REFERENCES goods (goodsid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    // 下单操作表
    private val createTableOrdersSQL = """
        CREATE TABLE IF NOT EXISTS orders (
        billid BIGINT AUTO_INCREMENT PRIMARY KEY,
        uid CHAR(23) NOT NULL,
        goodsid BIGINT NOT NULL,
        amount INT NOT NULL CHECK( amount > 0 ),
        status CHAR(11) NOT NULL CHECK( status IN ('ongoing','finish','suspend') ),
        otime DATETIME NOT NULL, 
        sumprice BIGINT NOT NULL CHECK( sumprice >= 0 ),
        CONSTRAINT KF_operation_account FOREIGN KEY (uid) REFERENCES users (uid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        CONSTRAINT KF_operation_gid FOREIGN KEY (goodsid) REFERENCES goods (goodsid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        INDEX idx_status (status)
        );
    """

    // 已完成的记录表
    private val createTableBillSQL = """
        CREATE TABLE IF NOT EXISTS bill (
        billid BIGINT PRIMARY KEY,
        uid CHAR(23) NOT NULL,
        goodsid BIGINT NOT NULL,
        amount INT NOT NULL CHECK( amount > 0 ),
        status CHAR(11) NOT NULL CHECK( status IN ('finish','suspend') ),
        otime DATETIME NOT NULL, 
        sumprice BIGINT NOT NULL,
        CONSTRAINT KF_bill_account FOREIGN KEY (uid) REFERENCES users (uid) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        CONSTRAINT KF_bill_gid FOREIGN KEY (goodsid) REFERENCES goods (goodsid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        );
    """

    private val createTriggerTrgOrdersSQL = """
        CREATE TRIGGER TRG_orders
        AFTER UPDATE ON orders
        FOR EACH ROW
        BEGIN
            IF NEW.status IN ('finish', 'suspend') THEN
                -- 将符合条件的记录插入到 bill 表
                INSERT INTO bill (billid, uid, goodsid, amount, status, otime, sumprice)
                VALUES (NEW.billid, NEW.uid, NEW.goodsid, NEW.amount, NEW.status, NEW.otime, NEW.sumprice);

            END IF;
        END;
    """


    private val createTriggerTrgUsersSQL = """
        CREATE TRIGGER TRG_users
        AFTER INSERT ON users
        FOR EACH ROW
        BEGIN
            INSERT INTO userProfile (uid, gender) VALUES (NEW.uid, 'secrecy');
        END;
    """

    /**
     * 删除表的语句
     * */
    private val dropTableUserSQL = "DROP TABLE IF EXISTS users;"

    private val dropTableUserProfileSQL = "DROP TABLE IF EXISTS userProfile;"

    private val dropTableGoodsSQL = "DROP TABLE IF EXISTS goods;"

    private val dropTableCartSQL = "DROP TABLE IF EXISTS cart;"

    private val dropTableOrdersSQL = "DROP TABLE IF EXISTS orders;"

    private val dropTableBillSQL = "DROP TABLE IF EXISTS bill;"


    private val dropTriggerTrgOrdersSQL = "DROP TRIGGER IF EXISTS TRG_orders;"

    private val dropTriggerTrgUsersSQL = "DROP TRIGGER IF EXISTS TRG_users"
}