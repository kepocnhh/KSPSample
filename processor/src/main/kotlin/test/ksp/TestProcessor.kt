package test.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid

class TestProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    class Provider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
            return TestProcessor(environment.codeGenerator)
        }
    }

    private var files: Sequence<KSFile>? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
//        val visitor = object : KSVisitorVoid() {
//            override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
//                classDeclaration.getDeclaredFunctions().map { it.accept(this, Unit) }
//            }
//
//            override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
//                functions.add(function)
//            }
//
//            override fun visitFile(file: KSFile, data: Unit) {
//                val packageName = file.packageName.asString()
//                if (packageName == packageTarget || packageName.startsWith("$packageTarget.")) {
//                    println(file.fileName)
//                }
//            }
//        }
        val packageTarget = "test.sample.foundation.provider"
        files = resolver.getAllFiles().filter {
            val packageName = it.packageName.asString()
            packageName == packageTarget || packageName.startsWith("$packageTarget.")
        }
        return emptyList()
    }

    override fun finish() {
        val files = requireNotNull(files)
        val packageName = "test.sample.implementation.provider.test"
        val fileName = "TestProvider"
        val triple = "\"\"\""
        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false),
            packageName = packageName,
            fileName = fileName
        ).use {
            it.write("""
                package $packageName
                
                class TestProvider {
                    val foo = $triple
                        ${files.joinToString(separator = "\n") { ksFile ->  ksFile.fileName }}
                    $triple.trimIndent()
                }
                
            """.trimIndent().toByteArray())
        }
    }
}
