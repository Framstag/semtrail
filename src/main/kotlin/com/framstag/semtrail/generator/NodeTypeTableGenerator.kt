package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Model
import com.framstag.semtrail.model.Page
import mu.KLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.file.Paths

class NodeTypeTableGenerator(private val nodeType : String):Generator() {

    val targetFile="${nodeType.substring(1)}_index.html"
    val templateFile="index"

    companion object : KLogging()

    override fun getPages(data : PagesData): List<Page> {
        return listOf(Page(Paths.get(targetFile), data.model.nodeTypes[nodeType]!!.name));
    }

    override fun generate(data : GenerateData) {
        logger.info("${this.javaClass.simpleName}: $templateFile -> $targetFile")

        val context = Context()

        val nodeList = data.model.nodeMap.values.toMutableList().filter {
                node -> node.type == nodeType
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