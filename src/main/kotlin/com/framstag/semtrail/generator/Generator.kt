package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Page
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

abstract class Generator {
    fun bindDataToContext(context : Context, data : GenerateData) {
        context.setVariable("model",data.model)
        context.setVariable("config",data.config)
    }

    abstract fun getPages(data : PagesData):List<Page>
    abstract fun generate(data : GenerateData)
}
