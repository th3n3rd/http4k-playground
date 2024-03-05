package com.example

import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
@EnabledIfSystemProperty(named = "threadSafetyTests", matches = "true", disabledReason = "Thread-safety tests not enabled")
annotation class ThreadSafetyTests
