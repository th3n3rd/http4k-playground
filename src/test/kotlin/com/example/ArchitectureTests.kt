package com.example

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.modules.syntax.ModuleRuleDefinition.modules

@AnalyzeClasses(packagesOf = [GuessTheSecretApp::class])
class ArchitectureTests {

    @ArchTest
    fun `modules have no cyclic dependencies`(importedClasses: JavaClasses) {
        modules()
            .definedByPackages("com.example.(*)..")
            .should()
            .beFreeOfCycles()
            .check(importedClasses)
    }
}