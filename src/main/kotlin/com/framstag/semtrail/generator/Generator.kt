package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Page
import org.thymeleaf.TemplateEngine

abstract class Generator {
    abstract fun getPages():List<Page>
    abstract fun generate(templateEngine: TemplateEngine)
}
