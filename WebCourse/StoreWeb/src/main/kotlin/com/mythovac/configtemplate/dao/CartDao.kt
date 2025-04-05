package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Cart
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface CartDao {
    fun insert(cart: Cart)
    fun update(cart: Cart)
    fun deleteByUid(uid: String)
    fun deleteByUidAndGoodsid(uid: String, goodsid: Long)
    fun findByUidAndGoodsid(uid: String, goodsid: Long): Cart?
    fun findByAttr(uid: String = "-1", goodsid: Long = -1): List<Cart>
}