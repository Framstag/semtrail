package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class CSSGenerator(private val targetDirectory: String, private val model: Model) {

    fun generate(templateEngine: TemplateEngine) {
        val context = Context()

        context.setVariable("model",model)

        val file = Paths.get(targetDirectory, "standard.css").toFile()

        val writer = file.printWriter()

        templateEngine.process("standard",context,writer)

        writer.close()
    }
}