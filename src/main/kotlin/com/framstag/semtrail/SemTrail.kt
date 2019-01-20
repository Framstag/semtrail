package com.framstag.semtrail

import mu.KotlinLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.nio.charset.StandardCharsets

val logger = KotlinLogging.logger("main")

fun main(args : Array<String>) {
    if (args.size !=2) {
        logger.error("SemTrail <filename>.semtrail <target directory>")
        return
    }

    val filename = args[0]
    val targetDirectory = args[1]

    logger.info("Loading file '$filename'...")

    val scanner = Scanner(args[0])
    val model = Model()
    val modelBuilder = ModelBuilder(model)
    val parser = Parser(scanner, modelBuilder)

    if (!parser.parse()) {
        logger.error("Error while parsing file!")

        return
    }

    val templateResolver = FileTemplateResolver()

    templateResolver.characterEncoding = StandardCharsets.UTF_8.name()
    templateResolver.templateMode = TemplateMode.HTML
    templateResolver.prefix = "templates/"
    templateResolver.suffix = ".html"

    val templateEngine = TemplateEngine()
    templateEngine.addTemplateResolver(templateResolver)

    val indexGenerator = IndexPageGenerator(targetDirectory,model)

    indexGenerator.generate(templateEngine)

    val nodeTypeIndexPageGenerator = NodeTypeIndexPageGenerator(targetDirectory,model)

    for (type in model.nodeTypeSet) {
        nodeTypeIndexPageGenerator.generate(templateEngine, type)
    }

    val nodePagesGenerator = NodePagesGenerator(targetDirectory,model)

    nodePagesGenerator.generate(templateEngine)

    logger.info("Done.")
}