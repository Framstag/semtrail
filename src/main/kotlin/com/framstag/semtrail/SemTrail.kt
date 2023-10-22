package com.framstag.semtrail

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import com.framstag.semtrail.configuration.Configuration
import com.framstag.semtrail.parser.*
import com.framstag.semtrail.generator.*
import com.framstag.semtrail.dsl.ParserCallback
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

fun createApplicationContext(callback: ParserCallback): LookupContext {
    val appLookup = LookupContext()
    val semtrailLookup = LookupContext(appLookup)
    val configLookup = LookupContext(semtrailLookup)
    val reportLookup = LookupContext(semtrailLookup)

    appLookup.addFunction(
        FunctionDefinition(
            "semtrail",
            callback::onSemtrail,
            1,
            true,
            semtrailLookup
        )
    )

    semtrailLookup.addFunction(
        FunctionDefinition(
            "log",
            callback::log,
            1
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "config",
            callback::onConfig,
            0,
            true,
            configLookup
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "report",
            callback::onReport,
            0,
            true,
            reportLookup
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "node",
            callback::onNode,
            3,
        )
    )
    semtrailLookup.addFunction(
        FunctionDefinition(
            "edge",
            callback::onEdge,
            3
        )
    )

    configLookup.addFunction(
        FunctionDefinition(
            "nodeType",
            callback::onNodeType,
            3
        )
    )

    configLookup.addFunction(
        FunctionDefinition(
            "edgeType",
            callback::onEdgeType,
            3
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "nodesTable",
            callback::onNodesTable,
            1
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "nodesTable",
            callback::onNodesTable,
            2
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "allNodesImage",
            callback::onAllNodesImage,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "starterTable",
            callback::onStarterTable,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "leavesTable",
            callback::onLeavesTable,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "mostCausesTable",
            callback::onMostCausesTable,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "mostConsequencesTable",
            callback::onMostConsequencesTable,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "mostConnectedTable",
            callback::onMostConnectedTable,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "orphansTable",
            callback::onOrphansTable,
            0
        )
    )

    reportLookup.addFunction(
        FunctionDefinition(
            "noDocTable",
            callback::onNoDocTable,
            0
        )
    )

    return appLookup
}

fun initializeLogging() {
    val logCtx : LoggerContext=LoggerFactory.getILoggerFactory() as LoggerContext

    logCtx.reset()

    val logEncoder = PatternLayoutEncoder()
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
    val config = Configuration()

    config.targetDirectory = targetDirectory

    val parser = ASTParser(scanner)

    logger.info("Parsing to AST...")
    val functionCall = parser.parse()

    if (!parser.hasErrors() && functionCall != null) {
        logger.info("OK")
    } else {
        logger.info("ERROR")
        return
    }

    val parserCallback = ParserCallback(config, model)

    val appLookup = createApplicationContext(parserCallback)
    val executor = Executor()

    logger.info("Executing...")
    executor.evaluate(functionCall, appLookup)

    if (!executor.hasErrors()) {
        logger.info("OK")
    } else {
        logger.info("ERROR")
        return
    }

    logger.info("Generating report based on templates from '$templateDirectory' to '$targetDirectory'...")

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

    val pagesData = PagesData(model)
    config.generators.forEach {
        config.pages.addAll(it.getPages(pagesData))
    }

    val generateData = GenerateData(httpTemplateEngine, config, model)
    val nodePagesGenerator = NodePagesGenerator()
    nodePagesGenerator.generate(generateData)

    config.generators.forEach {
        it.generate(generateData)
    }

    logger.info("Done.")
}