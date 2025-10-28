import java.io.File

fun newPropertiesFile(testProjectDir: File) {
    File(testProjectDir, "gradle.properties").writeText(
        """
        pluginLogLevel=INFO
        xGradlePluginRepo="https://maven.aliyun.com/repository/gradle-plugin"
        xMavenRepo="https://maven.aliyun.com/repository/public"
    """.trimIndent()
    )
}