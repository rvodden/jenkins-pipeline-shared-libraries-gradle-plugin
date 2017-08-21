package com.mkobit.jenkins.pipelines

import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import testsupport.NotImplementedYet
import testsupport.resourceText
import testsupport.writeRelativeFile
import java.io.File

@Tag("integration")
internal class IntegrationTestSourceIntegrationTest {

  private lateinit var projectDir: File

  @BeforeEach
  internal fun setUp() {
    projectDir = createTempDir().apply { deleteOnExit() }
  }

  @Disabled("class not on compile classpath - Unable to load class org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever due to missing dependency org/jenkinsci/plugins/workflow/steps/scm/SCMStep")
  @Test
  internal fun `can compile integration test sources that use Jenkins libraries`() {
    projectDir.writeRelativeFile(fileName = "build.gradle") {
      groovyBuildScript()
    }

    projectDir.writeRelativeFile("test", "integration", "groovy", "com", "mkobit", fileName = "ImportClassesCompilationTest.groovy") {
      resourceText("com/mkobit/ImportClassesCompilationTest.groovy")
    }

    val buildResult: BuildResult = GradleRunner.create()
      .withPluginClasspath()
      .withArguments("compileIntegrationTestGroovy", "-s", "-i")
      .withProjectDir(projectDir)
      .build()

    val task = buildResult.task(":compileIntegrationTestGroovy")
    Assertions.assertThat(task?.outcome)
      .describedAs("integrationTestCompileGroovy task outcome")
      .withFailMessage("Build output: ${buildResult.output}")
      .isNotNull()
      .isEqualTo(TaskOutcome.SUCCESS)
  }

  @Disabled("test seems to hang forever")
  @Test
  internal fun `can use @JenkinsRule in integration tests`() {
    projectDir.writeRelativeFile(fileName = "build.gradle") {
      groovyBuildScript()
    }

    projectDir.writeRelativeFile("test", "integration", "groovy", "com", "mkobit", fileName = "JenkinsRuleUsageTest.groovy") {
      resourceText("com/mkobit/JenkinsRuleUsageTest.groovy")
    }

    val buildResult: BuildResult = GradleRunner.create()
      .withPluginClasspath()
      .withArguments("integrationTest", "-s", "-i", "--dry-run")
      .withProjectDir(projectDir)
      .build()

    val task = buildResult.task(":integrationTest")
    Assertions.assertThat(task?.outcome)
      .describedAs("integrationTest task outcome")
      .withFailMessage("Build output: ${buildResult.output}")
      .isNotNull()
      .isEqualTo(TaskOutcome.SUCCESS)
  }

  @Disabled("class not on compile classpath - unable to resolve class org.jenkinsci.plugins.workflow.job.WorkflowJob")
  @Test
  internal fun `WorkflowJob can be created and executed in integration tests`() {
    projectDir.writeRelativeFile(fileName = "build.gradle") {
      groovyBuildScript()
    }

    projectDir.writeRelativeFile("test", "integration", "groovy", "com", "mkobit", fileName = "WorkflowJobUsageTest.groovy") {
      resourceText("com/mkobit/WorkflowJobUsageTest.groovy")
    }

    val buildResult: BuildResult = GradleRunner.create()
      .withPluginClasspath()
      .withArguments("integrationTest", "-s", "-i")
      .withProjectDir(projectDir)
      .build()

    val task = buildResult.task(":integrationTest")
    Assertions.assertThat(task?.outcome)
      .describedAs("integrationTest task outcome")
      .withFailMessage("Build output: ${buildResult.output}")
      .isNotNull()
      .isEqualTo(TaskOutcome.SUCCESS)
  }

  @NotImplementedYet
  @Test
  internal fun `can set up pipeline library in an integration test`() {
    projectDir.writeRelativeFile(fileName = "build.gradle") {
      groovyBuildScript()
    }

    projectDir.writeRelativeFile("src", "com", "mkobit", fileName = "LibHelper.groovy") {
      """
package com.mkobit

class LibHelper {
  private script
  LibHelper(script) {
    this.script = script
  }

  void sayHelloTo(String name) {
    script.echo "LibHelper says hello to ${'$'}name!"
  }
}
"""
    }

    projectDir.writeRelativeFile("test", "integration", "groovy", "com", "mkobit", fileName = "JenkinsGlobalLibraryTest.groovy") {
      resourceText("com/mkobit/JenkinsGlobalLibraryTest.groovy")
    }

    val buildResult: BuildResult = GradleRunner.create()
      .withPluginClasspath()
      .withArguments("integrationTest", "-s", "-i")
      .withProjectDir(projectDir)
      .build()

    val task = buildResult.task(":integrationTest")
    Assertions.assertThat(task?.outcome)
      .describedAs("integrationTest task outcome")
      .withFailMessage("Build output: ${buildResult.output}")
      .isNotNull()
      .isEqualTo(TaskOutcome.SUCCESS)
  }


  @NotImplementedYet
  @Test
  internal fun `GlobalLibraries is available for configuration in integration tests`() {
  }

  @NotImplementedYet
  @Test
  internal fun `integration test output for Jenkins Test Harness is in the build directory`() {
  }

  // TODO: this should be tested but may or may not be needed. I have a feeling classloader errors
  // will happen in pipeline code if those classes are available.
  @NotImplementedYet
  @Test
  internal fun `cannot use classes from main source code in integration test`() {
  }

  @NotImplementedYet
  @Test
  internal fun `can use declared plugin dependencies in integration test`() {
  }
}