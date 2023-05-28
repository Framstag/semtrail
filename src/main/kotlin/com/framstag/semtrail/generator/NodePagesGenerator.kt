package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class NodePagesGenerator(private val targetDirectory: String, private val model: Model) {

    fun generate(templateEngine: TemplateEngine) {

        for (node in model.nodeMap.values) {
            val context = Context()

            context.setVariable("model",model)
            context.setVariable("node",node)

            val file = Paths.get(targetDirectory, "nodes", "${node.hashCode()}.html").toFile()

            val writer = file.printWriter()

            templateEngine.process("node",context,writer)

            writer.close()
        }
    }
}
