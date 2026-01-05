import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertTrue

class FsGradleTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `tes plugin fs`() {
        newPropertiesFile(testProjectDir)
        File(testProjectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("fs")
            }
            fsLogger.trace("test", "trace")
            fsLogger.debug("test", "debug")
            fsLogger.info("test", "info")
            fsLogger.warn("test", "warn")
            fsLogger.error("test", "error")
        """.trimIndent()
        )
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("tasks", "--all")
            .withPluginClasspath()
            .build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    private fun newPropertiesFile(testProjectDir: File) {
        val propertiesPath = Paths.get("").toAbsolutePath().resolveSibling("gradle.properties")
        File(testProjectDir, "gradle.properties")
            .writeText(propertiesPath.toFile().readText())
    }
}