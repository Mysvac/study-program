package com.mythovac.configtemplate.impl

import com.mythovac.configtemplate.dao.CartgoodsDao
import com.mythovac.configtemplate.entity.Cartgoods
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

/**
 * Dao 类 的实现
 * 通过SQL语句和Entity
 * 直接实现数据库操作
 * */
class CartgoodsImpl(private val jdbcTemplate: JdbcTemplate) : CartgoodsDao  {
    // 映射，方便查询的结果存入List<Entity>
    private val rowMapper = RowMapper<Cartgoods> { rs, _ ->
        Cartgoods(
            uid = rs.getString("uid"),
            goodsid = rs.getLong("goodsid"),
            stock = rs.getInt("stock"),
            amount = rs.getInt("amount"),
            goodsname = rs.getString("goodsname"),
            price = rs.getInt("price"),
            author = rs.getString("author"),
            available = rs.getInt("available")
        )
    }

    // 根据uid查询对应用户的购物车内容
    override fun findCartgoodsByUid(uid: String): List<Cartgoods> {
        val sql = """
            SELECT uid,cart.goodsid AS goodsid,stock,amount,goodsname,price,author,available FROM cart
            JOIN goods ON cart.goodsid = goods.goodsid
            WHERE uid = ?
        """
        return jdbcTemplate.query(sql, rowMapper, uid)
    }
}