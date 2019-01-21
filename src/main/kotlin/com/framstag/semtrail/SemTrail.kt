package com.framstag.semtrail

import mu.KotlinLogging
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

val logger = KotlinLogging.logger("main")

fun main(args : Array<String>) {
    if (args.size !=3) {
        logger.error("SemTrail <description file.semtrail <template directory> <target directory>")
        return
    }

    val filename = args[0]
    val templateDirectoryArg = args[1]
    val targetDirectory = args[2]

    val templateDirectoryFile = File(templateDirectoryArg)

    if (!templateDirectoryFile.exists()) {
        logger.error("Template directory '$templateDirectoryArg' does not exist!")
        return
    }

    if (!templateDirectoryFile.isDirectory) {
        logger.error("Template directory '$templateDirectoryArg' is not a directory!")
        return
    }

    val templateDirectory =
        if (templateDirectoryFile.path.endsWith(File.separator)) templateDirectoryFile.path else templateDirectoryFile.path + File.separator

    logger.info("Loading SemTrail file '$filename'...")

    val scanner = Scanner(args[0])
    val model = Model()
    val modelBuilder = ModelBuilder(model)
    val parser = Parser(scanner, modelBuilder)

    if (!parser.parse()) {
        logger.error("Error while parsing file!")

        return
    }

    logger.info("Generating web page based on templates in directory '$templateDirectory' to directory '$targetDirectory'...")

    val templateResolver = FileTemplateResolver()

    templateResolver.characterEncoding = StandardCharsets.UTF_8.name()
    templateResolver.templateMode = TemplateMode.HTML
    templateResolver.prefix = templateDirectory
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

    val orphanGenerator = OrphanIndexPageGenerator(targetDirectory,model)

    orphanGenerator.generate(templateEngine)

    val starterGenerator = StarterIndexPageGenerator(targetDirectory,model)

    starterGenerator.generate(templateEngine)

    val leaveGenerator = LeaveIndexPageGenerator(targetDirectory,model)

    leaveGenerator.generate(templateEngine)

    logger.info("Done.")
}