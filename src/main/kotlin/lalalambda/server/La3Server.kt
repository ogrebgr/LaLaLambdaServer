package lalalambda.server

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import com.bolyartech.forge.server.config.FileForgeServerConfigurationLoader
import com.bolyartech.forge.server.config.ForgeConfigurationException
import com.bolyartech.forge.server.config.ForgeServerConfiguration
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.jetty.ForgeJettyConfiguration
import com.bolyartech.forge.server.jetty.ForgeJettyConfigurationLoaderFile
import com.bolyartech.totoproverka3.server.main.MainModule
import com.bolyartech.totoproverka3.server.main.NotFoundResponse
import lalalambda.aws.AwsLambdaDispatcher
import lalalambda.server.misc.InternalServerErrorResponse
import lalalambda.simple.SimpleLambdaDispatcher
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class La3Server constructor(
    private val awsDispatcher: AwsLambdaDispatcher,
    private val disSimple: SimpleLambdaDispatcher
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun start() {
        var jettyConfigPath = ForgeJettyConfigurationLoaderFile.detectJettyConfigFilePath()

        if (jettyConfigPath != null) {
            val loader = ForgeJettyConfigurationLoaderFile(jettyConfigPath)

            val conf: ForgeJettyConfiguration
            try {
                conf = loader.load()
            } catch (e: ForgeConfigurationException) {
                throw IllegalStateException(e)
            }

            val configDir: String = if (conf.configDir.isEmpty()) {
                val f = File(jettyConfigPath)
                f.parent
            } else {
                conf.configDir
            }

            val forgeConfLoader = FileForgeServerConfigurationLoader(configDir)
            val forgeConf: ForgeServerConfiguration = try {
                forgeConfLoader.load()
            } catch (e: ForgeConfigurationException) {
                logger.error("Cannot load forge.conf")
                exitProcess(1)
            }

            val server = Server(
                MainModule(awsDispatcher, disSimple),
                RouteHandler { NotFoundResponse() },
                RouteHandler { InternalServerErrorResponse() })

            initLog(configDir, forgeConf.serverLogName)
            server.start(
                conf,
                forgeConf.isPathInfoEnabled,
                forgeConf.maxSlashesInPathInfo
            )
        } else {
            logger.error("No configuration. Aborting.")
        }
    }

    private fun initLog(configDir: String, logFilenamePrefix: String = "", serverNameSuffix: String = "") {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        val jc = JoranConfigurator()
        jc.context = context
        context.reset()

        context.putProperty("application-name", logFilenamePrefix + serverNameSuffix)

        val f = File(configDir, "logback.xml")
        println("Will try logback config: " + f.absolutePath)
        if (f.exists()) {
            val logbackConfigFilePath = f.absolutePath
            try {
                jc.doConfigure(logbackConfigFilePath)
                logger.info("+++ logback initialized OK")
            } catch (e: JoranException) {
                e.printStackTrace()
            }
        } else {
            println("!!! No logback configuration file found. Using default conf")
        }
    }
}