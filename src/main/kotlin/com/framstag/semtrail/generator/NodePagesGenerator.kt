package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import com.framstag.semtrail.model.Page
import mu.KLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class NodePagesGenerator(private val targetDirectory: String, private val model: Model):Generator() {

    companion object : KLogging()

    override fun getPages(): List<Page> {
        val pages = mutableListOf<Page>()

        for (node in model.nodeMap.values) {
            pages.add(Page(Paths.get("nodes/${node.hashCode()}.html"),node.name))
        }

        return pages;
    }

    override fun generate(templateEngine: TemplateEngine) {

        for (node in model.nodeMap.values) {
            val context = Context()

            context.setVariable("model",model)
            context.setVariable("node",node)

            val templateFile="node"
            val targetFile="${node.hashCode()}.html"

            val file = Paths.get(targetDirectory, "nodes", targetFile).toFile()

            logger.info("${this.javaClass.simpleName}: $templateFile -> $targetFile")

            val writer = file.printWriter()
            templateEngine.process(templateFile,context,writer)
            writer.close()
        }
    }
}
