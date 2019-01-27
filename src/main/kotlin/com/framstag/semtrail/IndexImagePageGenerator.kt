package com.framstag.semtrail

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.CharsetEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class IndexImagePageGenerator(private val targetDirectory: String, private val model: Model) {

    fun generate(templateEngine: TemplateEngine) {
        val context = Context()

        val nodeList = model.nodeMap.values.toMutableList()

        nodeList.sortBy {
            it.name
        }

        context.setVariable("model",model)
        context.setVariable("nodes",nodeList)

        val file = File(Paths.get(targetDirectory, "index_image.html").toUri())

        val writer = file.printWriter()

        templateEngine.process("image",context,writer)

        writer.close()
    }
}