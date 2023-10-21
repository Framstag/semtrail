package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import com.framstag.semtrail.model.Page
import mu.KLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class OrphansTableGenerator():Generator() {

    private val templateFile = "index"
    private val targetFile = "orphans.html"
    private val label = "Orphans"

    companion object : KLogging()

    override fun getPages(data : PagesData): List<Page> {
        return listOf(Page(Paths.get(targetFile),label));
    }

    override fun generate(data : GenerateData) {
        logger.info("${this.javaClass.simpleName}: $templateFile -> $targetFile")

        val context = Context()

        val nodeList = data.model.nodeMap.values.toMutableList().filter {
                node -> node.fromNodes.isEmpty() && node.toNodes.isEmpty()
        }.sortedBy {
            it.name
        }

        bindDataToContext(context,data)

        context.setVariable("nodes",nodeList)

        val file = Paths.get(data.config.targetDirectory, targetFile).toFile()
        val writer = file.printWriter()
        data.templateEngine.process(templateFile,context,writer)
        writer.close()
    }
}