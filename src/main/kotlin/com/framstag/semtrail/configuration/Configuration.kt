package com.framstag.semtrail.configuration

import com.framstag.semtrail.generator.Generator
import com.framstag.semtrail.model.Page

class Configuration {
    var targetDirectory: String = ""
    val generators : MutableList<Generator> = mutableListOf()
    val pages :  MutableList<Page> = mutableListOf()
}