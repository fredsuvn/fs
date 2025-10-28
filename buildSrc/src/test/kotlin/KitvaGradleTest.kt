import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class KitvaGradleTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `plugin should apply successfully`() {
        newPropertiesFile(testProjectDir)
        File(testProjectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("kitva")
            }
            pLogger.trace("test", "trace")
            pLogger.debug("test", "trace")
            pLogger.info("test", "trace")
            pLogger.warn("test", "trace")
            pLogger.error("test", "trace")
        """.trimIndent()
        )
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("tasks", "--all")
            .withPluginClasspath()
            .build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }
}