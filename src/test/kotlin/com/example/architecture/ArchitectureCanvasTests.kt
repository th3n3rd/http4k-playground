package com.example.architecture

import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path

class ArchitectureCanvasTests {

    @Nested
    inner class CanvasGeneration {

        private val canvas = ArchitectureCanvas.of("com.example.guessing")

        @Test
        fun `identifies top-level modules`() {
            val canvas = ArchitectureCanvas.of("com.example.guessing")
            canvas.modules.map { it.shortName } shouldContainOnly listOf(
                "c.e.g.common",
                "c.e.g.gameplay",
                "c.e.g.leaderboard",
                "c.e.g.player",
            )
        }

        @Test
        fun `identifies infrastructure components for a module`() {
            canvas.modules.first().infra.all { it.name.contains(".infra.") } shouldBe true
        }

        @Test
        fun `identifies domain components for a module`() {
            val module = canvas.modules.first()
            module.domain.all { it.packagePath == module.name } shouldBe true
        }

        @Test
        fun `identifies use case components for a module`() {
            canvas.modules.filter { !it.shared }.forEach {
                it.useCases shouldNotContainAnyOf it.infra
                it.useCases shouldNotContainAnyOf it.domain
            }
        }

        @Test
        fun `identifies when a component is shared`() {
            val shared = canvas.modules.first { it.shared }
            shared.infra.all { it.shared } shouldBe true
            shared.useCases.all { it.shared } shouldBe true
            shared.domain.all { it.shared } shouldBe true
        }

        @Test
        fun `excludes marking interfaces`() {
            val shared = canvas.modules.first { it.shared }
            shared.domain.map { it.simpleName } shouldNotContainAnyOf listOf("DomainEvent")
            shared.useCases.map { it.simpleName } shouldNotContainAnyOf listOf("UseCase")
        }

        @Test
        fun `excludes test code`() {
            canvas.modules.filter { !it.shared }.forEach {
                it.infra.any { component -> component.name.endsWith("Tests") } shouldBe false
                it.useCases.any { component -> component.name.endsWith("Tests") } shouldBe false
                it.domain.any { component -> component.name.endsWith("Tests") } shouldBe false
            }
        }
    }

    @Nested
    inner class CanvasRendering {
        @Test
        fun `renders the canvas in markdown`() {
            val canvas = ArchitectureCanvas.of("com.example.guessing")
            canvas.render(
                Path.of("docs/architecture")
            )
        }
    }

}