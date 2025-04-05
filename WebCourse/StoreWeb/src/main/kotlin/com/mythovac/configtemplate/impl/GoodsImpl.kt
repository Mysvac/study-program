package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.GoodsDao
import com.mythovac.configtemplate.entity.Goods
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class GoodsImpl(private val jdbcTemplate: JdbcTemplate) : GoodsDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Goods> { rs, _ ->
        Goods(
            goodsid = rs.getLong("goodsid"),
            goodstype = rs.getString("goodstype"),
            goodsname = rs.getString("goodsname"),
            stock = rs.getInt("stock"),
            price = rs.getInt("price"),
            sales = rs.getInt("sales"),
            author = rs.getString("author"),
            profile = rs.getString("profile")?:"无",
            available = rs.getInt("available")
        )
    }

    // 查询全部
    override fun findAll(): List<Goods> {
        val sql = "SELECT * FROM goods"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // 查询全部可售的书籍
    override fun findAllAble(): List<Goods> {
        val sql = "SELECT * FROM goods WHERE available = 1"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // 根据 goodsid 查询书籍
    override fun findByGoodsid(goodsid: Long): Goods? {
        val sql = "SELECT * FROM goods WHERE goodsid = ?"
        try{
            val res = jdbcTemplate.queryForObject(sql, rowMapper, goodsid)
            return res
        }
        catch(e: EmptyResultDataAccessException){
            return null
        }
    }

    // 根据特征查询书籍，宽泛查询
    override fun findByAttr(goodsid: Long, author: String, goodstype: String, goodsname: String): List<Goods> {
        val sql = "SELECT * FROM goods WHERE goodsid = ? OR author LIKE ? OR goodstype LIKE ? OR goodsname LIKE ?"
        return jdbcTemplate.query(sql, rowMapper, goodsid, "%$author%", "%$goodstype%", "%$goodsname%")
    }

    // 插入
    override fun insert(goods: Goods) {
        val sql = "INSERT INTO goods(goodstype, goodsname, stock, price, sales, author, profile, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql,
            goods.goodstype,
            goods.goodsname,
            goods.stock,
            goods.price,
            goods.sales,
            goods.author,
            goods.profile,
            goods.available)
    }

    // 修改数据
    override fun update(goods: Goods) {
        val sql = "UPDATE goods SET goodstype = ?, goodsname = ?, stock = ?, price = ?, sales = ?, author = ?, profile = ?, available = ? WHERE goodsid = ?"
        jdbcTemplate.update(sql,
            goods.goodstype,
            goods.goodsname,
            goods.stock,
            goods.price,
            goods.sales,
            goods.author,
            goods.profile,
            goods.available,
            goods.goodsid)
    }

    // 删除对应书籍
    override fun deleteByGoodsid(goodsid: Long) {
        val sql = "DELETE FROM goods WHERE goodsid = ?"
        jdbcTemplate.update(sql, goodsid)
    }

}