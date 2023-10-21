package com.framstag.semtrail.generator

import com.framstag.semtrail.configuration.Configuration
import com.framstag.semtrail.model.Model
import org.thymeleaf.TemplateEngine

class GenerateData(val templateEngine: TemplateEngine, val config : Configuration, val model : Model)