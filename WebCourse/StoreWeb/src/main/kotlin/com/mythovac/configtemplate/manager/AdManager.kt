package com.mythovac.configtemplate.manager

object AdManager {
    private var enable: Boolean = true
    private var location: String = "move"

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }
    fun getEnable(): Boolean {
        return enable
    }
    fun setLocation(location: String) {
        if(location == "move" || location == "left" || location == "right") {
            this.location = location
        }
    }
    fun getLocation(): String {
        return location
    }
}