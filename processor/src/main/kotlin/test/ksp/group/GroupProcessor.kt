package test.ksp.group

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import test.ksp.group.annotation.Group

class GroupProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    companion object {
        private fun KSFile.getSimpleName(): String {
            check(fileName.endsWith(".kt"))
            return fileName.substring(0, fileName.length - 3)
        }
    }

    private var grouped: Map<KSFile, Sequence<KSClassDeclaration>>? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val files = resolver.getAllFiles()
        grouped = files.filter { file ->
            file.annotations.any {
                val declaration = it.annotationType.resolve().declaration
                val c = Group::class.java
                check(c.isAnnotation)
                declaration.packageName.asString() == c.packageName && declaration.simpleName.asString() == c.simpleName
            }
        }.associateWith { group ->
            val postfix = group.getSimpleName()
            val packageTarget = group.packageName.asString()
            files.filter {
                val packageName = it.packageName.asString()
                (packageName == packageTarget || packageName.startsWith("$packageTarget.")) &&
                it.fileName != group.fileName && it.fileName.endsWith(group.fileName)
            }.map { file ->
                file.declarations.filterIsInstance<KSClassDeclaration>().filter {
                    val simpleName = it.simpleName.asString()
                    it.classKind == ClassKind.INTERFACE &&
                            simpleName != postfix &&
                            simpleName.endsWith(postfix)
                }.single()
            }
        }
        return emptyList()
    }

    override fun finish() {
        val grouped = requireNotNull(grouped)
        grouped.forEach { (group, interfaces) ->
            val packageTarget = group.packageName.asString()
            val postfix = group.getSimpleName()
            val type = TypeSpec.interfaceBuilder(postfix).also { builder ->
                builder.addProperties(
                    interfaces.map {
                        val simpleName = it.simpleName.asString()
                        val type = ClassName(it.packageName.asString(), simpleName)
                        val name = simpleName.substring(0, simpleName.lastIndexOf(postfix)).lowercase()
                        PropertySpec.builder(name, type).build()
                    }.toList()
                )
            }.build()
            val file = FileSpec.builder(packageTarget, postfix)
                .addType(type)
                .build()
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false),
                packageName = packageTarget,
                fileName = postfix
            ).use { os ->
                OutputStreamWriter(os, StandardCharsets.UTF_8).use { writer ->
                    file.writeTo(writer)
                }
            }
        }
    }
}
