package com.framstag.semtrail.astparser

import mu.KLogging

class Map : Type {
    companion object : KLogging()

    private val map: MutableMap<Type,Type> = mutableMapOf()

    override fun isMap(): Boolean {
        return true
    }

    override fun toMap(): Map {
        return this
    }

    fun addKeyValue(key: Type, value: Type) {
       map[key]=value;
    }

    fun value():kotlin.collections.Map<Type,Type> {
        return map.toMap()
    }
}
