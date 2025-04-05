package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.CartDao
import com.mythovac.configtemplate.entity.Cart
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.SQLException

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
@Repository
class CartImpl(private val jdbcTemplate: JdbcTemplate) : CartDao {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Cart> { rs, _ ->
        Cart(
            uid = rs.getString("uid"),
            goodsid = rs.getLong("goodsid"),
            amount = rs.getInt("amount")
        )
    }

    // 根据 uid 或 goodsid 查询列表(多本查询）
    override fun findByAttr(uid: String, goodsid: Long): List<Cart> {
        val sql = "SELECT * FROM cart WHERE uid = ? OR goodsid = ?"
        return jdbcTemplate.query(sql, rowMapper, uid, goodsid)
    }

    // 单本查询
    override fun findByUidAndGoodsid(uid: String, goodsid: Long): Cart? {
        val sql = "SELECT * FROM cart WHERE  uid = ? AND goodsid = ?"
        try{
            val res = jdbcTemplate.queryForObject(sql, rowMapper, uid, goodsid)
            return res
        }
        catch(e: EmptyResultDataAccessException){
            return null
        }
    }
    // 删除
    override fun deleteByUid(uid: String) {
        val sql = "DELETE FROM cart WHERE uid = ?"
        jdbcTemplate.update(sql, uid)
    }
    // 删除
    override fun deleteByUidAndGoodsid(uid: String, goodsid: Long) {
        val sql = "DELETE FROM cart WHERE uid = ? AND goodsid = ?"
        jdbcTemplate.update(sql, uid, goodsid)
    }
    // 修改
    override fun update(cart: Cart) {
        if(cart.amount <=0 ){
            deleteByUidAndGoodsid(cart.uid, cart.goodsid)
            return
        }
        val sql = "UPDATE cart SET amount = ? WHERE uid = ? AND goodsid = ?"
        jdbcTemplate.update(sql, cart.amount, cart.uid, cart.goodsid)
    }
    // 插入
    override fun insert(cart: Cart) {
        val sql = "INSERT INTO cart(uid, goodsid, amount) VALUES(?, ?, ?)"
        jdbcTemplate.update(sql, cart.uid, cart.goodsid, cart.amount)
    }
}