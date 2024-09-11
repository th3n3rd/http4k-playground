package com.example.architecture

import io.github.classgraph.ClassInfo

data class Component(val info: ClassInfo) {
    val name: String = info.name
    val packagePath = info.name.split(".").dropLast(1).joinToString(".")
    val simpleName = info.name.split(".").last()
    val shared = info.packageInfo.hasAnnotation(Architecture.Shared::class.java)
}