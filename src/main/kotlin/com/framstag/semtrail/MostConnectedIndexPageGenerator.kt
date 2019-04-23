package com.framstag.semtrail

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class MostConnectedIndexPageGenerator(private val targetDirectory: String, private val model: Model) {

    fun generate(templateEngine: TemplateEngine) {
        val context = Context()

        val nodeList = model.nodeMap.values.toMutableList().sortedByDescending {
            it.fromNodes.size + it.toNodes.size
        }

        context.setVariable("model",model)
        context.setVariable("nodes",nodeList)

        val file = Paths.get(targetDirectory, "most_connected.html").toFile()

        val writer = file.printWriter()

        templateEngine.process("index",context,writer)

        writer.close()
    }
}