package com.pmleczek.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ExpoBrownfieldPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("ExpoBrownfieldPlugin applied")
    }
}
