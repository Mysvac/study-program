package com.mythovac.configtemplate.entity

/**
 * Entity 实体类
 * Cartgoods 购物车详情
 * 用于多表连接时的纯储
 * cart + goods
 * */
data class Cartgoods(
    var uid: String,
    var goodsid: Long,
    var amount: Int,
    var stock: Int,
    var goodsname: String,
    var price: Int,
    var author : String,
    var available: Int
)
