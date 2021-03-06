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
    val targetDirectoryArg = args[2]

    val templateDirectoryFile = File(templateDirectoryArg)
    val targetDirectoryFile = File(targetDirectoryArg)

    // Check template directory

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

    // Check target directory

    if (!targetDirectoryFile.exists()) {
        logger.warn("Creating target directory '$targetDirectoryArg'...")

        targetDirectoryFile.mkdirs()
    }

    val targetDirectoryNodesFile = Paths.get(targetDirectoryFile.toString(), "nodes").toFile()

    if (!targetDirectoryNodesFile.exists()) {
        logger.warn("Creating target directory '$targetDirectoryNodesFile'...")
        targetDirectoryNodesFile.mkdirs()
    }

    val targetDirectory =
        if (targetDirectoryFile.path.endsWith(File.separator)) targetDirectoryFile.path else targetDirectoryFile.path + File.separator

    // Loading definition file...

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

    val httpTemplateResolver = FileTemplateResolver()

    httpTemplateResolver.characterEncoding = StandardCharsets.UTF_8.name()
    httpTemplateResolver.templateMode = TemplateMode.HTML
    httpTemplateResolver.prefix = templateDirectory
    httpTemplateResolver.suffix = ".html"

    val httpTemplateEngine = TemplateEngine()
    httpTemplateEngine.addTemplateResolver(httpTemplateResolver)

    val cssTemplateResolver = FileTemplateResolver()

    cssTemplateResolver.characterEncoding = StandardCharsets.UTF_8.name()
    cssTemplateResolver.templateMode = TemplateMode.CSS
    cssTemplateResolver.prefix = templateDirectory
    cssTemplateResolver.suffix = ".css"

    val cssTemplateEngine = TemplateEngine()
    cssTemplateEngine.addTemplateResolver(cssTemplateResolver)

    val cssGenerator = CSSGenerator(targetDirectory,model)

    cssGenerator.generate(cssTemplateEngine)

    val indexGenerator = IndexPageGenerator(targetDirectory,model)

    indexGenerator.generate(httpTemplateEngine)

    val indexImageGenerator = IndexImagePageGenerator(targetDirectory,model)

    indexImageGenerator.generate(httpTemplateEngine)

    val nodeTypeIndexPageGenerator = NodeTypeIndexPageGenerator(targetDirectory,model)

    for (type in model.nodeTypeSet) {
        nodeTypeIndexPageGenerator.generate(httpTemplateEngine, type)
    }

    val nodePagesGenerator = NodePagesGenerator(targetDirectory,model)

    nodePagesGenerator.generate(httpTemplateEngine)

    val starterGenerator = StarterIndexPageGenerator(targetDirectory,model)

    starterGenerator.generate(httpTemplateEngine)

    val leaveGenerator = LeaveIndexPageGenerator(targetDirectory,model)

    leaveGenerator.generate(httpTemplateEngine)

    val mostCausesGenerator = MostCausesIndexPageGenerator(targetDirectory,model)

    mostCausesGenerator.generate(httpTemplateEngine)

    val mostConsequencesGenerator = MostConsequencesIndexPageGenerator(targetDirectory,model)

    mostConsequencesGenerator.generate(httpTemplateEngine)

    val mostConnnectedGenerator = MostConnectedIndexPageGenerator(targetDirectory,model)

    mostConnnectedGenerator.generate(httpTemplateEngine)

    val orphanGenerator = OrphanIndexPageGenerator(targetDirectory,model)

    orphanGenerator.generate(httpTemplateEngine)

    val noDocGenerator = NoDocIndexPageGenerator(targetDirectory,model)

    noDocGenerator.generate(httpTemplateEngine)

    logger.info("Done.")
}