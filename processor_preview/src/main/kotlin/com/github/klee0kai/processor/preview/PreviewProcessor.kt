package com.github.klee0kai.processor.preview

import com.github.klee0kai.processor.preview.ext.enclosingElements
import com.github.klee0kai.processor.preview.ext.findCommonPgk
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

@AutoService(Processor::class)
@SupportedAnnotationTypes("*")
class PreviewProcessor : AbstractProcessor() {

    lateinit var env: ProcessingEnvironment

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        env = processingEnv
    }

    override fun process(
        set: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?,
    ): Boolean {
        roundEnv?.rootElements
        val previewAnn = env.elementUtils
            .getTypeElement("androidx.compose.ui.tooling.preview.Preview") ?: return false
        val composableAnnCl = ClassName("androidx.compose.runtime", "Composable")
        val debugOnlyAnnCl =
            ClassName("com.github.klee0kai.thekey.core.utils.annotations", "DebugOnly")

        val previewMethods = roundEnv?.getElementsAnnotatedWith(previewAnn)
            ?.filterIsInstance<ExecutableElement>()

        if (previewMethods.isNullOrEmpty()) return false

        val commonPkg = previewMethods
            .mapNotNull { env.elementUtils.getPackageOf(it)?.toString() }
            .findCommonPgk()

        val contentTypeName = LambdaTypeName.get(
            returnType = Unit::class.asTypeName(),
        ).copy(
            annotations = listOf(
                AnnotationSpec.builder(composableAnnCl).build()
            )
        )

        val annotationsListTypeName = List::class.asTypeName()
            .parameterizedBy(
                KClass::class.asTypeName()
                    .parameterizedBy(
                        WildcardTypeName.producerOf(
                            Annotation::class.asClassName(),
                        )
                    )
            )


        val foundMethodType = TypeSpec.classBuilder("FoundPreviewMethod")
            .apply {
                addProperty(
                    PropertySpec
                        .builder("pkg", String::class)
                        .initializer("pkg")
                        .build()
                )
                addProperty(
                    PropertySpec
                        .builder("methodName", String::class)
                        .initializer("methodName")
                        .build()
                )
                addProperty(
                    PropertySpec
                        .builder("annotations", annotationsListTypeName)
                        .initializer("annotations")
                        .build()
                )
                addProperty(
                    PropertySpec
                        .builder("content", contentTypeName)
                        .initializer("content")
                        .build()
                )

                primaryConstructor(
                    FunSpec.constructorBuilder()
                        .apply {
                            addParameter("pkg", String::class)
                            addParameter("methodName", String::class)
                            addParameter("annotations", annotationsListTypeName)
                            addParameter("content", contentTypeName)
                        }
                        .build()
                )
            }
            .build()

        val foundMethodClName = ClassName("$commonPkg.gen.preview", "FoundPreviewMethod")

        val allPreviewFun = FunSpec.builder("allPreviews")
            .apply {
                returns(Sequence::class.asClassName().parameterizedBy(foundMethodClName))
                addAnnotation(debugOnlyAnnCl)

                beginControlFlow("return sequence { ")
                previewMethods.forEach { preview ->
                    val packageElement = preview.enclosingElements
                        .firstOrNull { it is PackageElement } as? PackageElement
                    val pkg = packageElement?.qualifiedName?.toString() ?: ""
                    val allAnnotations = CodeBlock.builder()
                        .apply {
                            add("listOf( ")
                            add(
                                preview.annotationMirrors
                                    .map { it.annotationType.asTypeName().toString() }
                                    .filter { it.startsWith("com.github.klee0kai.thekey.core.utils.annotations.IgnorePaparazzi") }
                                    .joinToString(",") { "${it}::class" }
                            )
                            add(" )")
                        }.build()

                    addStatement(
                        "yield( FoundPreviewMethod( %S, %S, %L, { %L.%L() } ) )",
                        pkg,
                        preview.simpleName,
                        allAnnotations,
                        pkg,
                        preview.simpleName,
                    )
                }
                endControlFlow()
            }
            .build()

        FileSpec.builder(
            foundMethodClName.packageName,
            "GenPreviews.kt"
        ).apply {
            addType(foundMethodType)
            addFunction(allPreviewFun)
        }.build()
            .writeTo(env.filer)

        return false
    }

}