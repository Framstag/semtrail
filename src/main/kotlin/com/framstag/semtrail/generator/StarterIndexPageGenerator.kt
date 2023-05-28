package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class StarterIndexPageGenerator(private val targetDirectory: String, private val model: Model) {

    fun generate(templateEngine: TemplateEngine) {
        val context = Context()

        val nodeList = model.nodeMap.values.toMutableList().filter {
                node -> node.fromNodes.isEmpty() && node.toNodes.isNotEmpty()
        }.sortedBy {
            it.name
        }

        context.setVariable("model",model)
        context.setVariable("nodes",nodeList)

        val file = Paths.get(targetDirectory, "starter.html").toFile()

        val writer = file.printWriter()

        templateEngine.process("index",context,writer)

        writer.close()
    }
}