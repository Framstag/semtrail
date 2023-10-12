package com.framstag.semtrail

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.Encoder
import com.framstag.semtrail.parser.*
import com.framstag.semtrail.generator.*
import com.framstag.semtrail.model.ModelBuilder
import com.framstag.semtrail.model.Model
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

val logger = KotlinLogging.logger("main")

fun createApplicationContext(modelBuilder: ModelBuilder): LookupContext {
    val appLookup = LookupContext()
    val semtrailLookup = LookupContext(appLookup)
    val configLookup = LookupContext(semtrailLookup)

    appLookup.addFunction(
        FunctionDefinition(
            "semtrail",
            modelBuilder::onSemtrail,
            1,
            true,
            semtrailLookup
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "log",
            modelBuilder::log,
            1
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "config",
            modelBuilder::onConfig,
            0,
            true,
            configLookup
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "node",
            modelBuilder::onNode,
            3,
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "edge",
            modelBuilder::onEdge,
            3
        )
    )

    configLookup.addFunction(
        FunctionDefinition(
            "nodeType",
            modelBuilder::onNodeType,
            3
        )
    )

    configLookup.addFunction(
        FunctionDefinition(
            "edgeType",
            modelBuilder::onEdgeType,
            3
        )
    )

    return appLookup
}

fun initializeLogging() {
    val logCtx : LoggerContext=LoggerFactory.getILoggerFactory() as LoggerContext

    logCtx.reset()

    val logEncoder: PatternLayoutEncoder = PatternLayoutEncoder()
    logEncoder.context = logCtx
    logEncoder.pattern = "%d{HH:mm:ss.SSS} %-5level %msg%n"
    logEncoder.start()

    val logConsoleAppender = ConsoleAppender<ILoggingEvent>()
    logConsoleAppender.context = logCtx
    logConsoleAppender.name = "console"
    logConsoleAppender.encoder = logEncoder
    logConsoleAppender.start()

    val log = logCtx.getLogger("ROOT")
    log.level = Level.INFO
    log.addAppender(logConsoleAppender)
}

fun main(args: Array<String>) {
    initializeLogging()

    if (args.size != 3) {
        logger.error("SemTrail <description file> <template directory> <target directory>")
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

    val parser = ASTParser(scanner)

    logger.info("Parsing to AST...")
    val functionCall = parser.parse()
    logger.info("Parsing to AST done.")

    if (!parser.hasErrors() && functionCall != null) {
        logger.info("OK")
    } else {
        logger.info("ERROR")
        return
    }

    val modelBuilder = ModelBuilder(model)

    val appLookup = createApplicationContext(modelBuilder)
    val executor = Executor()

    executor.evaluate(functionCall, appLookup)

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

    val cssGenerator = CSSGenerator(targetDirectory, model)

    cssGenerator.generate(cssTemplateEngine)

    val indexGenerator = IndexPageGenerator(targetDirectory, model)

    indexGenerator.generate(httpTemplateEngine)

    val indexImageGenerator = IndexImagePageGenerator(targetDirectory, model)

    indexImageGenerator.generate(httpTemplateEngine)

    val nodeTypeIndexPageGenerator = NodeTypeIndexPageGenerator(targetDirectory, model)

    for (type in model.nodeTypes.values.stream().sorted().toList()) {
        nodeTypeIndexPageGenerator.generate(httpTemplateEngine, type.type)
    }

    val nodePagesGenerator = NodePagesGenerator(targetDirectory, model)

    nodePagesGenerator.generate(httpTemplateEngine)

    val starterGenerator = StarterIndexPageGenerator(targetDirectory, model)

    starterGenerator.generate(httpTemplateEngine)

    val leaveGenerator = LeaveIndexPageGenerator(targetDirectory, model)

    leaveGenerator.generate(httpTemplateEngine)

    val mostCausesGenerator = MostCausesIndexPageGenerator(targetDirectory, model)

    mostCausesGenerator.generate(httpTemplateEngine)

    val mostConsequencesGenerator = MostConsequencesIndexPageGenerator(targetDirectory, model)

    mostConsequencesGenerator.generate(httpTemplateEngine)

    val mostConnnectedGenerator = MostConnectedIndexPageGenerator(targetDirectory, model)

    mostConnnectedGenerator.generate(httpTemplateEngine)

    val orphanGenerator = OrphanIndexPageGenerator(targetDirectory, model)

    orphanGenerator.generate(httpTemplateEngine)

    val noDocGenerator = NoDocIndexPageGenerator(targetDirectory, model)

    noDocGenerator.generate(httpTemplateEngine)

    logger.info("Done.")
}