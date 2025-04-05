package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Goods 书籍信息
 * 现在是商品信息，没有进行修改
 * */
data class Goods(
    var goodsid: Long,
    var goodsname: String,
    var goodstype: String,
    var stock: Int,
    var price: Int,
    var sales: Int,
    var author : String,
    var profile: String,
    var available: Int
)
