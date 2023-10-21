package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import com.framstag.semtrail.model.Page
import mu.KLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class NodePagesGenerator():Generator() {

    companion object : KLogging()

    override fun getPages(data : PagesData): List<Page> {
        val pages = mutableListOf<Page>()

        for (node in data.model.nodeMap.values) {
            pages.add(Page(Paths.get("nodes/${node.hashCode()}.html"),node.name))
        }

        return pages
    }

    override fun generate(data : GenerateData) {

        for (node in data.model.nodeMap.values) {
            val context = Context()

            bindDataToContext(context,data)

            context.setVariable("node",node)

            val templateFile="node"
            val targetFile="${node.hashCode()}.html"

            val file = Paths.get(data.config.targetDirectory, "nodes", targetFile).toFile()

            logger.info("${this.javaClass.simpleName}: $templateFile -> $targetFile")

            val writer = file.printWriter()
            data.templateEngine.process(templateFile,context,writer)
            writer.close()
        }
    }
}
