package com.framstag.semtrail

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class NodePagesGenerator(private val targetDirectory: String, private val model: Model) {

    fun generate(templateEngine: TemplateEngine) {

        for (node in model.nodeMap.values) {
            val context = Context()

            context.setVariable("model",model)
            context.setVariable("node",node)

            val file = File(Paths.get(targetDirectory, "nodes", "${node.name}.html").toUri())

            val writer = file.printWriter()

            templateEngine.process("node",context,writer)

            writer.close()
        }
    }
}