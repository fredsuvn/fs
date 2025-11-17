import org.gradle.api.Plugin
import org.gradle.api.Project
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

class PluginLogger : Plugin<Project> {

    private lateinit var level: Level
    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project
        level = levelFrom(project.property("pluginLogLevel")?.toString())
        project.extensions.add("pLogger", this)
    }

    private fun levelFrom(level: String?): Level {
        if (level == null) {
            return Level.INFO;
        }
        try {
            return Level.valueOf(level.uppercase())
        } catch (ignored: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid plugin-log level: $level. " +
                    "Plugin-log level must be in [${Level.entries.joinToString(", ")}].",
                ignored
            );
        }
    }

    fun trace(vararg msg: Any?) {
        log(Level.TRACE, *msg)
    }

    fun debug(vararg msg: Any?) {
        log(Level.DEBUG, *msg)
    }

    fun info(vararg msg: Any?) {
        log(Level.INFO, *msg)
    }

    fun warn(vararg msg: Any?) {
        log(Level.WARN, *msg)
    }

    fun error(vararg msg: Any?) {
        log(Level.ERROR, *msg)
    }

    private fun log(level: Level, vararg msg: Any?) {
        if (this.level.value > level.value) {
            return;
        }
        val output = "[${formatter.format(LocalDateTime.now())}]" +
            "[${level.name}]: " +
            "[${project.name}]" +
            msg.joinToString(" ");
        println(output)
    }

    private enum class Level {

        TRACE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4),
        ;

        val value: Int;

        constructor(value: Int) {
            this.value = value;
        }
    }
}