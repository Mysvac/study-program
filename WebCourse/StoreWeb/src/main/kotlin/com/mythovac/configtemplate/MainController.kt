package com.mythovac.configtemplate

import com.mythovac.configtemplate.entity.Goods
import com.mythovac.configtemplate.manager.AdManager
import com.mythovac.configtemplate.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping



/**
 * 控制层
 * 网站根目录映射
 * 等同于 /page/main
 * */
@Controller("main-controller")
class MainController(private val userService: UserService) {
    @GetMapping("/")
    fun hello(request: HttpServletRequest, model: Model): String {
        // 模板model
        val session = request.getSession(false)
        if(session != null) {
            val uid: String = session.getAttribute("uid") as String
            val grade: String = session.getAttribute("grade") as String
            model.addAttribute("uid",uid)
            model.addAttribute("grade",grade)
        }

        val goods: List<Goods> = userService.findAllAbleGoods()
        model.addAttribute("goodsList", goods)
        model.addAttribute("AdEnable", AdManager.getEnable())
        model.addAttribute("AdLocation", AdManager.getLocation())

        return "main_page.html"
    }

    // SET GLOBAL log_bin_trust_function_creators = 1;

}