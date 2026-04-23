package space.sunqian.fs.build.processing

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * FsProcessor is the processor for this lib.
 */
@SupportedAnnotationTypes(value = ["space.sunqian.fs.build.processing.AutoVersion"])
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
class FsProcessor : AbstractProcessor() {

    private lateinit var sourceVersion: SourceVersion

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        val javaVersion = System.getProperty("java.version")

        fun versionToNumber(version: String): Int {
            val dashDot = version.indexOf('-')
            if (dashDot < 0) {
                return version.toInt()
            }
            return version.substring(0, dashDot).toInt()
        }

        fun javaMajorVersion(version: String): Int {
            val dot1 = version.indexOf('.')
            if (dot1 < 0) {
                return versionToNumber(version)
            }
            val firstNum: Int = versionToNumber(version.substring(0, dot1))
            if (firstNum >= 9) {
                return firstNum
            }
            val dot2 = version.indexOf('.', dot1 + 1)
            if (dot2 < 0) {
                return versionToNumber(version.substring(dot1 + 1))
            }
            return versionToNumber(version.substring(dot1 + 1, dot2))
        }

        sourceVersion = SourceVersion.valueOf("RELEASE_${javaMajorVersion(javaVersion)}")
    }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        println(
            "processing ${annotations.joinToString { it.toString() }}: " +
                "${roundEnv.getElementsAnnotatedWith(AutoVersion::class.java)}"
        )
        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return sourceVersion
    }
}