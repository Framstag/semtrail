package com.framstag.semtrail.parser

import mu.KLogging

class List : Type {
    companion object : KLogging()

    private val member: MutableList<Type> = mutableListOf()

    override fun isList(): Boolean {
        return true
    }

    override fun toList(): List {
        return this
    }

    fun addMember(type: Type) {
        member.add(type)
    }

    fun getMember(): kotlin.collections.List<Type> {
        return member.toList()
    }
}
