package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
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

        val file = Paths.get(targetDirectory, "index_image.html").toFile()

        val writer = file.printWriter()

        templateEngine.process("image",context,writer)

        writer.close()
    }
}