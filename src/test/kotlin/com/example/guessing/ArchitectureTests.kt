package com.example.guessing

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.modules.syntax.ModuleRuleDefinition.modules

@AnalyzeClasses(packagesOf = [App::class])
class ArchitectureTests {

    @ArchTest
    fun `modules have no cyclic dependencies`(importedClasses: JavaClasses) {
        modules()
            .definedByPackages("com.example.guessing.(*)..")
            .should()
            .beFreeOfCycles()
            .check(importedClasses)
    }
}