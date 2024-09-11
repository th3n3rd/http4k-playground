package com.example.guessing

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.modules.syntax.ModuleRuleDefinition.modules
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path

@AnalyzeClasses(packagesOf = [GuessTheSecretApp::class])
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