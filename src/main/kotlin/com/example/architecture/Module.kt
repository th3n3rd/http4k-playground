package com.example.architecture

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.PackageInfo

data class Module(
    val info: PackageInfo,
    val useCases: List<Component> = emptyList(),
    val domain: List<Component> = emptyList(),
    val infra: List<Component> = emptyList(),
) {
    val name: String = info.name
    val simpleName = name.split(".").last()
    val shortName = name.compacted()
    val shared = infra.all { it.shared }
        && useCases.all { it.shared }
        && domain.all { it.shared }

    companion object {
        fun of(packageInfo: PackageInfo): Module {
            val scanResult = ClassGraph()
                .acceptPackages(packageInfo.name)
                .enableAllInfo()
                .scan()

            val useCases = mutableListOf<Component>()
            val domain = mutableListOf<Component>()
            val infra = mutableListOf<Component>()

            scanResult.allClasses
                .filter { it.belongsToAnySubpackageOf(packageInfo.name) }
                .filter { it.isNotSyntheticCode() }
                .filter { it.isProductionCode() }
                .filter { it.isTopLevelCode() }
                .filter { it.isNotExcluded() }
                .forEach {
                    when {
                        it.isInfrastructure() -> infra.add(Component(it))
                        it.isUseCase() -> useCases.add(Component(it))
                        it.isCore() -> domain.add(Component(it))
                    }
                }

            return Module(
                info = packageInfo,
                useCases = useCases,
                domain = domain,
                infra = infra,
            )
        }
    }
}

private fun ClassInfo.isNotSyntheticCode() = !simpleName.endsWith("Kt")

private fun ClassInfo.isProductionCode() = !classpathElementURL.path.contains("/test/")
    && !classpathElementURL.path.contains("/test-classes/")

private fun ClassInfo.isTopLevelCode() = !name.contains("$")

private fun ClassInfo.isInfrastructure() = packageInfo.hasAnnotation(Architecture.Infra::class.java)

private fun ClassInfo.isCore() = packageInfo.hasAnnotation(Architecture.Core::class.java)

private fun ClassInfo.isUseCase() = isCore() && hasAnnotation(Architecture.UseCase::class.java)

private fun ClassInfo.isNotExcluded() = !hasAnnotation(Architecture.Excluded::class.java)

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