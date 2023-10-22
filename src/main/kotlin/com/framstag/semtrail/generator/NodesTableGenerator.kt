package com.framstag.semtrail.generator

import com.framstag.semtrail.model.Node
import com.framstag.semtrail.model.Page
import mu.KLogging
import org.thymeleaf.context.Context
import java.nio.file.Paths

class NodesTableGenerator(private val name: String, private val types: Set<String>): Generator() {

    private val templateFile = "index"
    private val targetFile = "$name.html"

    companion object : KLogging()

    override fun getPages(data : PagesData): List<Page> {
        return listOf(Page(Paths.get(targetFile),name));
    }

    override fun generate(data : GenerateData) {
        logger.info("${this.javaClass.simpleName}: $templateFile -> $targetFile")

        val context = Context()

        val nodeList : List<Node> = data.model.nodeMap.values.filter {
            if (types.isEmpty()) true else types.contains(it.type)
        }.sortedBy {
            it.name
        }.toList()

        bindDataToContext(context,data)

        context.setVariable("nodes",nodeList)

        val file = Paths.get(data.config.targetDirectory, targetFile).toFile()
        val writer = file.printWriter()
        data.templateEngine.process(templateFile,context,writer)
        writer.close()
    }
}