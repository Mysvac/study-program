package com.mythovac.configtemplate.service

import com.mythovac.configtemplate.entity.*
import com.mythovac.configtemplate.impl.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 服务层
 * 数据库操作的二次封装
 * 实现了控制层需要的方法
 * */
@Service
class UserService(private val jdbcTemplate: JdbcTemplate, private val passwordEncoder: PasswordEncoder) {
    private var userProfileImpl: UserProfileImpl = UserProfileImpl(jdbcTemplate)
    private var usersImpl: UsersImpl = UsersImpl(jdbcTemplate)
    private var goodsImpl: GoodsImpl = GoodsImpl(jdbcTemplate)
    private var cartImpl: CartImpl = CartImpl(jdbcTemplate)
    private var ordersImpl: OrdersImpl = OrdersImpl(jdbcTemplate)
    private var billImpl: BillImpl = BillImpl(jdbcTemplate)
    private var cartgoodsImpl: CartgoodsImpl = CartgoodsImpl(jdbcTemplate)
    private var userInfoImpl: UserInfoImpl = UserInfoImpl(jdbcTemplate)

    /**
     * 注册普通账号
     * */
    fun signUp(uid: String, password: String): Boolean {
        if( usersImpl.findByUid(uid) == null ) {
            // 密码加密存储
            val newPassword = passwordEncoder.encode(password)
            usersImpl.insert(Users(uid = uid, password = newPassword, grade = "vip"))
            return true
        }
        return false
    }

    /**
     * 登入 不存在就注册，存在就密码验证
     * */
    fun signIn(uid: String, password: String): Boolean {
        val res: Users = usersImpl.findByUid(uid) ?: return signUp(uid, password)

        // 因为数据库内的密码加密了，所以要使用编码器，验证密码
        return passwordEncoder.matches(password, res.password)
    }

    /**
     * 查询指定uid用户的权限
     * */
    fun findGradeByUid(uid: String): String{
        val res: Users? = usersImpl.findByUid(uid)
        return res?.grade ?: "banned"
    }

    /**
     * 设置用户权限
     * */
    fun setUserGrade(uid: String,grade: String): Boolean {
        if(grade != "admin" && grade != "vip" && grade != "banned") return false
        val res: Users = usersImpl.findByUid(uid) ?: return false
        usersImpl.update(Users(uid = uid, password = res.password, grade = grade))
        return true
    }

    /**
     * 查询用户详情信息
     * */
    fun findUserInfoByUid(uid: String): UserInfo? {
        val user: Users = usersImpl.findByUid(uid) ?: return null
        val profile: UserProfile = userProfileImpl.findByUid(uid) ?: return null

        val userInfo = UserInfo(
            uid=uid,
            grade=user.grade,
            gender = profile.gender,
            address = profile.address,
            username = profile.username,
            email = profile.email,
            profile = profile.profile
        )
        return userInfo
    }

    /**
     * 根据uid
     * 查询用户的 账号 密码 权限 信息
     * */
    fun findUsersByUid(uid: String): Users? {
        return usersImpl.findByUid(uid)
    }


    /**
     * 根据特征查询全部用户具体信息
     * */
    fun findUserInfoByAttr(uid: String = "屙蠺錒", username: String="屙蠺錒"): List<UserInfo>{
        return userInfoImpl.findUserIndoByAttr(uid=uid,username=username)
    }

    /**
     * 查询购物车数据 按照uid
     * */
    fun findCartgoodsByUid(uid: String): List<Cartgoods> {
        return cartgoodsImpl.findCartgoodsByUid(uid)
    }

    /**
     * 查询全部图书
     * */
    fun findAllGoods(): List<Goods> {
        return goodsImpl.findAll()
    }

    /**
     * 查询全部正在售卖的图书信息
     * */
    fun findAllAbleGoods(): List<Goods> {
        return goodsImpl.findAllAble ()
    }

    /**
     * 根据特征查询图书
     * */
    fun findGoodsByAttr(goodsid: Long = -1, author: String = "鎿乸", goodstype: String = "鎿乸", goodsname: String = "鎿乸"): List<Goods> {
        return goodsImpl.findByAttr(goodsid, author, goodstype, goodsname)
    }

    /**
     * 设置(修改)图书信息
     * */
    fun setGoodsInfo(goods: Goods) {
        goodsImpl.update(goods)
    }

    /**
     * 查询指定用户的订单和账单
     * */
    fun findBillAndOrderByUid(uid : String): List<BillDetail>{
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findByAttr(uid=uid)
        if(orders.isNotEmpty()){
            for(order in orders){
                val goodsname = goodsImpl.findByGoodsid(order.goodsid)!!.goodsname
                res.add(BillDetail(
                    billid = order.billid,
                    uid = order.uid,
                    goodsid = order.goodsid,
                    amount = order.amount,
                    status = order.status,
                    otime = order.otime,
                    sumprice = order.sumprice,
                    goodsname = goodsname
                ))
            }
        }
        val bills = billImpl.findByAttr(uid=uid)
        if(bills.isNotEmpty()){
            for (bill in bills){
                val goodsname = goodsImpl.findByGoodsid(bill.goodsid)!!.goodsname
                res.add(BillDetail(
                    billid = bill.billid,
                    uid = bill.uid,
                    goodsid = bill.goodsid,
                    amount = bill.amount,
                    status = bill.status,
                    otime = bill.otime,
                    sumprice = bill.sumprice,
                    goodsname = goodsname
                ))
            }
        }
        return res
    }

    /**
     * 查询指定用户的订单和账单
     * 根据特征
     * */
    fun findBillAndOrderByAttr(uid : String,billid: Long,goodsname: String): List<BillDetail>{
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findByAttr(uid=uid)
        if(orders.isNotEmpty()){
            for(order in orders){
                val tmpGoodsname = goodsImpl.findByGoodsid(order.goodsid)!!.goodsname
                if(order.billid==billid || tmpGoodsname.contains(goodsname)){
                    res.add(BillDetail(
                        billid = order.billid,
                        uid = order.uid,
                        goodsid = order.goodsid,
                        amount = order.amount,
                        status = order.status,
                        otime = order.otime,
                        sumprice = order.sumprice,
                        goodsname = tmpGoodsname
                    ))
                }
            }
        }
        val bills = billImpl.findByAttr(uid=uid)
        if(bills.isNotEmpty()){
            for (bill in bills){
                val tmpGoodsname = goodsImpl.findByGoodsid(bill.goodsid)!!.goodsname
                if(bill.billid==billid || tmpGoodsname.contains(goodsname)){
                    res.add(BillDetail(
                        billid = bill.billid,
                        uid = bill.uid,
                        goodsid = bill.goodsid,
                        amount = bill.amount,
                        status = bill.status,
                        otime = bill.otime,
                        sumprice = bill.sumprice,
                        goodsname = tmpGoodsname
                    ))
                }
            }
        }
        return res
    }

    /**
     * 查询指定的订单，根据订单号billid
     * */
    fun findOrderByBillid(billid: Long): Orders?{
        return ordersImpl.findByAttr(billid = billid).firstOrNull()
    }

    /**
     * 查询全部的订单
     * */
    fun findAllOrders(): List<BillDetail>{
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findAll()
        if(orders.isNotEmpty()){
            for(order in orders){
                val goodsname = goodsImpl.findByGoodsid(order.goodsid)!!.goodsname
                res.add(BillDetail(
                    billid = order.billid,
                    uid = order.uid,
                    goodsid = order.goodsid,
                    amount = order.amount,
                    status = order.status,
                    otime = order.otime,
                    sumprice = order.sumprice,
                    goodsname = goodsname
                ))
            }
        }
        return res
    }

    /**
     * 查询全部的订单和账单
     * */
    fun findAllOrdersAndBill(): List<BillDetail>{
        ordersImpl.clearStatus()
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findAll()
        if(orders.isNotEmpty()){
            for(order in orders){
                val goodsname = goodsImpl.findByGoodsid(order.goodsid)!!.goodsname
                res.add(BillDetail(
                    billid = order.billid,
                    uid = order.uid,
                    goodsid = order.goodsid,
                    amount = order.amount,
                    status = order.status,
                    otime = order.otime,
                    sumprice = order.sumprice,
                    goodsname = goodsname
                ))
            }
        }
        val bills = billImpl.findAll()
        if(bills.isNotEmpty()){
            for (bill in bills){
                val goodsname = goodsImpl.findByGoodsid(bill.goodsid)!!.goodsname
                res.add(BillDetail(
                    billid = bill.billid,
                    uid = bill.uid,
                    goodsid = bill.goodsid,
                    amount = bill.amount,
                    status = bill.status,
                    otime = bill.otime,
                    sumprice = bill.sumprice,
                    goodsname = goodsname
                ))
            }
        }
        return res
    }

    /**
     * 根据特征查询订单
     * */
    fun findOrdersByAttr(billid: Long=-1, uid: String="琺鑪覭", goodsid: Long=-1): List<BillDetail> {
        val res: MutableList<BillDetail> = mutableListOf()
        val orders = ordersImpl.findByAttr(uid = uid, goodsid = goodsid, billid = billid)
        if(orders.isNotEmpty()){
            for(order in orders){
                val goodsname = goodsImpl.findByGoodsid(order.goodsid)!!.goodsname
                res.add(BillDetail(
                    billid = order.billid,
                    uid = order.uid,
                    goodsid = order.goodsid,
                    amount = order.amount,
                    status = order.status,
                    otime = order.otime,
                    sumprice = order.sumprice,
                    goodsname = goodsname
                ))
            }
        }
        return res
    }

    /**
     * 删除图书
     * */
    fun deleteGoods(goodsid: Long): Boolean {
        // 有订单，不能删除
        if(billImpl.findByAttr(goodsid=goodsid).isNotEmpty() || ordersImpl.findByAttr(goodsid=goodsid).isNotEmpty()) {
            return false
        }
        goodsImpl.deleteByGoodsid(goodsid)
        return true
    }

    /**
     * 删除用户
     * */
    fun deleteUsers(uid: String): Boolean {
        // 存在订单，不能删除
        if(billImpl.findByAttr(uid = uid).isNotEmpty() || ordersImpl.findByAttr(uid = uid).isNotEmpty()) {
            return false
        }
        usersImpl.deleteByUid(uid)
        return true
    }

    /**
     * 删除单条购物车数据
     * */
    fun deleteCart(uid: String,goodsid: Long): Boolean {
        cartImpl.deleteByUidAndGoodsid(uid,goodsid)
        return true
    }

    /**
     * 删除指定用户的全部购物车数据
     * */
    @Transactional
    fun deleteCartByUid(uid: String): Boolean {
        cartImpl.deleteByUid(uid)
        return true
    }


    /**
     * 加入购物车
     * */
    fun insertCart(uid: String, goodsid: Long, amount: Int = -1) {
        val cart: Cart? = cartImpl.findByUidAndGoodsid(uid,goodsid)
        if(cart == null) {
            cartImpl.insert(Cart(uid = uid, goodsid = goodsid, amount = 1))
            return
        }
        if(amount == -1){
            cartImpl.update(Cart(uid = uid, goodsid = goodsid, amount = cart.amount+1))
        }
        if(amount == 0 || amount<-1){
            deleteCart(uid,goodsid)
        }
        if(amount > 0){
            cartImpl.update(Cart(uid = uid, goodsid = goodsid, amount = amount))
        }
    }

    /**
     * 修改订单数据
     * */
    @Transactional
    fun setOrders(orders: Orders) {
        ordersImpl.update(orders)
        if(orders.status=="suspend"){
            val goods = goodsImpl.findByGoodsid(orders.goodsid)
            if(goods!=null){
                goods.stock+=orders.amount
                goodsImpl.update(goods)
            }
        }
    }

    /**
     * 插入一个新订单，同时减少库存数量
     * 去除购物车中同类项目（通过购物车购买时）
     * */
    @Transactional
    fun insertOrdersByAttr(uid: String, goodsid: Long, amount: Int = -1): String {
        if(amount<=0) return "购买数量异常"  // 数量错误
        val goods = goodsImpl.findByGoodsid(goodsid) ?: return "商品不存在" // 商品不存在
        if(goods.stock < amount) return "商品数量不足" // 数量不足
        if(goods.available == 0) return "商品不可出售" // 商品不可售

        goods.stock -= amount
        goodsImpl.update(goods)
        val sumprice = goods.price.toLong()*amount
        val time = getCurrentDateTime()
        val order = Orders(billid=0,uid=uid,goodsid=goodsid,amount = amount,otime = time,sumprice = sumprice,status = "ongoing")
        ordersImpl.insert(order)
        deleteCart(uid=uid,goodsid=goodsid)
        return "购买成功"
    }

    /**
     * 插入一个新订单，同时减少库存数量
     * 不修改购物车（直接下单，未通过购物车购买）
     * */
    @Transactional
    fun insertOneOrdersByAttr(uid: String, goodsid: Long, amount: Int = -1): String {
        if(amount<=0) return "购买数量异常"  // 数量错误
        val goods = goodsImpl.findByGoodsid(goodsid) ?: return "商品不存在" // 书籍不存在
        if(goods.stock < amount) return "商品数量不足" // 数量不足
        if(goods.available == 0) return "商品不可出售" // 书籍不可售

        goods.stock -= amount
        goodsImpl.update(goods)
        val sumprice = goods.price.toLong()*amount
        val time = getCurrentDateTime()
        val order = Orders(billid=0,uid=uid,goodsid=goodsid,amount = amount,otime = time,sumprice = sumprice,status = "ongoing")
        ordersImpl.insert(order)
        return "购买成功"
    }

    /**
     * 批量购买图书
     * */
    @Transactional
    fun buyAllGoods(cartgoodsList: List<Cartgoods>, uid: String){
        for (cartgoods in cartgoodsList) {
            insertOrdersByAttr(
                uid = uid,
                goodsid = cartgoods.goodsid,
                amount = cartgoods.amount
            )
        }
    }

    /**
     * 清空停售
     * */
    @Transactional
    fun clearUnableGoods(cartgoodsList: List<Cartgoods>, uid: String) {
        for (cartgoods in cartgoodsList) {
            if (cartgoods.available != 1) {
                deleteCart(uid = uid, goodsid = cartgoods.goodsid)
            }
        }
    }

    /**
     * 修改用户数据
     * */
    fun setUserProfile(userProfile: UserProfile){
        userProfileImpl.update(userProfile)
    }

    /**
     * 获取时间信息
     * */
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now() // 获取当前时间
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 定义格式
        return currentDateTime.format(formatter) // 格式化时间
    }
}