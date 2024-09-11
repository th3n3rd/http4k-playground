package com.example.architecture

import com.example.guessing.common.UseCase
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import org.http4k.events.Event
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.pathString

data class ArchitectureCanvas(val modules: List<Module> = emptyList()) {
    fun render(directory: Path) {
        val module = modules.first { !it.shared }
        val shared = modules.first { it.shared }

        val infra = module.infra + shared.infra
        val useCases = module.useCases + shared.useCases
        val domain = module.domain + shared.domain

        val data: String = """
        [
            {
                name: "Infrastructure",
                radius: 200,
                color: "lightblue",
                distribution: "even-around",
                components: [${infra.joinToString { "{ id: \"${it.simpleName}\", shared: ${it.shared} }"} }]
            },
            {
                name: "Use Cases",
                radius: 150,
                color: "lightgreen",
                distribution: "even-around",
                components: [${useCases.joinToString { "{ id: \"${it.simpleName}\", shared: ${it.shared} }"} }]
            },
            {
                name: "Domain",
                radius: 110,
                color: "lightcoral",
                distribution: "even-random",
                components: [${domain.joinToString { "{ id: \"${it.simpleName}\", shared: ${it.shared} }"} }]
            }
        ];
        """.trimIndent()

        val template = Files.readString(Path.of("src/main/resources/architecture-canvas-template.html"))
        val rendered = template.replace("{{data}}", data)

        if (!Files.isDirectory(directory)) {
            Files.createDirectory(directory)
        }
        Files.write(Path.of(directory.pathString, "canvas.html"), rendered.toByteArray())
    }

    data class Module(
        val name: String,
        val useCases: List<Component> = emptyList(),
        val domain: List<Component> = emptyList(),
        val infra: List<Component> = emptyList(),
    ) {
        val shortName = name.compacted()
        val shared = infra.all { it.shared }
            && useCases.all { it.shared }
            && domain.all { it.shared }

        companion object {
            fun of(packagePath: String): Module {
                val scanResult = ClassGraph()
                    .acceptPackages(packagePath)
                    .enableAllInfo()
                    .scan()

                val useCases = mutableListOf<Component>()
                val domain = mutableListOf<Component>()
                val infra = mutableListOf<Component>()

                scanResult.allClasses
                    .filter { it.belongsToAnySubpackageOf(packagePath) }
                    .filter { it.isNotSyntheticCode() }
                    .filter { it.isProductionCode() }
                    .filter { it.isTopLevelCode() }
                    .filter { it.isNotMarkingInterface() }
                    .forEach {
                        when {
                            it.isInfrastructure() -> infra.add(Component(it.name))
                            it.isUseCase() -> useCases.add(Component(it.name))
                            it.packageName == packagePath -> domain.add(Component(it.name))
                        }
                    }

                return Module(
                    name = packagePath,
                    useCases = useCases,
                    domain = domain,
                    infra = infra,
                )
            }
        }
    }

    data class Component(val name: String) {
        val packagePath = name.split(".").dropLast(1).joinToString(".")
        val simpleName = name.split(".").last()
        val shared = name.contains(".common.")
    }

    companion object {
        fun of(rootPackage: String): ArchitectureCanvas {
            val scanResult = ClassGraph()
                .acceptPackages(rootPackage)
                .enableAllInfo()
                .scan()

            val modules = scanResult.allClasses
                .filter { it.belongsToChildPackageOf(rootPackage) }
                .groupBy { it.packageName }
                .map { Module.of(it.key) }

            return ArchitectureCanvas(modules)
        }
    }
}

private fun ClassInfo.isNotSyntheticCode() = !simpleName.endsWith("Kt")

private fun ClassInfo.isProductionCode() = !classpathElementURL.path.contains("/test/")
    && !classpathElementURL.path.contains("/test-classes/")

private fun ClassInfo.isTopLevelCode() = !name.contains("$")

private fun ClassInfo.isInfrastructure() = packageName.endsWith(".infra")

private fun ClassInfo.isUseCase() = simpleName == UseCase::class.simpleName || implementsInterface(UseCase::class.java)

private fun ClassInfo.isNotMarkingInterface() = !listOf(
    Event::class.simpleName,
    UseCase::class.simpleName,
).contains(simpleName)

private fun ClassInfo.belongsToChildPackageOf(rootPackage: String) =
    packageName.split(".").size == rootPackage.split(".").size + 1

private fun ClassInfo.belongsToAnySubpackageOf(rootPackage: String) = packageName.contains(rootPackage)

private fun String.compacted(): String {
    val packages = split(".")
    val compressedParentPackages = packages
        .dropLast(1)
        .joinToString(".") {
            it.first().toString()
        }
    return compressedParentPackages + "." + packages.last()
}