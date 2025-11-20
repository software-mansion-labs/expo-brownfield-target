package com.pmleczek.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ExpoBrownfieldPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("ExpoBrownfieldPublishPlugin applied to project: ${project.name}")
  }
}
