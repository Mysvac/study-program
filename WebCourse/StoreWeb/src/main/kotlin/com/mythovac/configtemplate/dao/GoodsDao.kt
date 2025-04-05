package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Goods
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface GoodsDao {
    fun findAll(): List<Goods>
    fun findAllAble(): List<Goods>
    fun findByGoodsid(goodsid: Long): Goods?
    fun findByAttr(goodsid: Long = -1, author: String = "鎿堏琞", goodstype: String = "鎿堏琞", goodsname: String = "鎿堏琞") : List<Goods>
    fun insert(goods: Goods)
    fun update(goods: Goods)
    fun deleteByGoodsid(goodsid: Long)
}