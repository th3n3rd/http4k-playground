package com.example.architecture

import io.github.classgraph.ClassGraph
import io.github.classgraph.PackageInfo
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.pathString

data class ArchitectureCanvas(val modules: List<Module> = emptyList()) {
    fun render(directory: Path) {
        val shared = modules.first { it.shared }
        val notShared = modules.filter { !it.shared }

        val renderedModules = notShared.joinToString {
            val module = it
            val infra = module.infra + shared.infra
            val useCases = module.useCases + shared.useCases
            val domain = module.domain + shared.domain

            val renderedLayers: String = """
            [
                {
                    id: "Infrastructure",
                    colour: "lightblue",
                    components: [${infra.joinToString { "{ id: \"${it.simpleName}\", shared: ${it.shared} }" }}]
                },
                {
                    id: "Use Cases",
                    colour: "lightgreen",
                    components: [${useCases.joinToString { "{ id: \"${it.simpleName}\", shared: ${it.shared} }" }}]
                },
                {
                    id: "Domain",
                    colour: "lightcoral",
                    components: [${domain.joinToString { "{ id: \"${it.simpleName}\", shared: ${it.shared} }" }}]
                }
            ]""".trimIndent()

            """
                {
                    id: "${it.simpleName}",
                    layers: $renderedLayers
                }
            """.trimIndent()
        }

        val template = Files.readString(Path.of("src/main/resources/architecture-canvas-template.html"))
        val rendered = template.replace("{{data}}", "[$renderedModules]")

        if (!Files.isDirectory(directory)) {
            Files.createDirectory(directory)
        }
        Files.write(Path.of(directory.pathString, "canvas.html"), rendered.toByteArray())
    }

    companion object {
        fun of(rootPackage: String): ArchitectureCanvas {
            val scanResult = ClassGraph()
                .acceptPackages(rootPackage)
                .enableAllInfo()
                .scan()

            val modules = scanResult.packageInfo
                .filter { it.isCore() }
                .sortedBy { it.name }
                .map { Module.of(it) }

            return ArchitectureCanvas(modules)
        }
    }
}

private fun PackageInfo.isCore() = hasAnnotation(Architecture.Core::class.java)