package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import com.framstag.semtrail.model.Page
import mu.KLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class AllNodesTableGenerator(private val targetDirectory: String, private val model: Model): Generator() {

    private val templateFile = "index"
    private val targetFile = "index.html"
    private val label = "All"

    companion object : KLogging()

    override fun getPages(): List<Page> {
        return listOf(Page(Paths.get(targetFile),label));
    }

    override fun generate(templateEngine: TemplateEngine) {
        logger.info("${this.javaClass.simpleName}: $templateFile -> $targetFile")

        val context = Context()

        val nodeList = model.nodeMap.values.toMutableList()

        nodeList.sortBy {
            it.name
        }

        context.setVariable("model",model)
        context.setVariable("nodes",nodeList)

        val file = Paths.get(targetDirectory, targetFile).toFile()
        val writer = file.printWriter()
        templateEngine.process(templateFile,context,writer)
        writer.close()
    }
}