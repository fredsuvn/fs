package space.sunqian.fs.build.processing

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * FsProcessor is the processor for this lib.
 */
@SupportedAnnotationTypes(value = ["space.sunqian.fs.build.processing.AutoVersion"])
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class FsProcessor : AbstractProcessor() {

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
}