package com.mythovac.configtemplate.dao

import com.mythovac.configtemplate.entity.Cartgoods
import org.springframework.stereotype.Repository

/**
 * Dao 类
 * 仅接口
 * */
@Repository
interface CartgoodsDao {
    fun findCartgoodsByUid(uid: String): List<Cartgoods>
}