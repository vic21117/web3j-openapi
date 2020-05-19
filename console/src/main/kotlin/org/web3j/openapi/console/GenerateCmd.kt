/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.openapi.console

import mu.KLogging
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(
    name = "generate",
    description = ["Generates a web3j-openapi project"]
)
class GenerateCmd : OpenApiCli(), Callable<Int>{

    @Option(
        names = ["--jar"],
        description = ["set for jar generation."],
        defaultValue = "false"
    )
    var jar: Boolean = false

    @Option(
        names = ["--swagger-ui"],
        description = ["set for generating the swagger-ui."],
        defaultValue = "false"
    )
    var swaggerUi: Boolean = false

    override fun call(): Int {
        generate(outputDirectory)

        val projectFolder =
            Path.of(
                outputDirectory,
                projectName
            ).toFile()
                .apply {
                    if (!exists()) throw FileNotFoundException(absolutePath)
                }

        if (swaggerUi) {
            runGradleTask(projectFolder, "resolve", "Generating OpenApi specs")
            runGradleTask(projectFolder, "generateSwaggerUI", "Generating SwaggerUI")
            runGradleTask(projectFolder, "moveSwaggerUiToResources", "Setting up the SwaggerUI")
        }
        if (jar) runGradleTask(projectFolder, "shadowJar", "Generating the FatJar to ${projectFolder.parentFile.canonicalPath}")
        if (jar || swaggerUi) runGradleTask(projectFolder, "clean", "Cleaning up")

        println("Done.")
        return 0
    }

    private fun runGradleTask(projectFolder: File, task: String, description: String) {
        println(description)
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(projectFolder)
            .connect()
            .apply {
                newBuild()
                    .forTasks(task)
                    .run(object : ResultHandler<Void> {
                        override fun onFailure(failure: GradleConnectionException) {
                            logger.debug(failure.message)
                            throw GradleConnectionException(failure.message)
                        }

                        override fun onComplete(result: Void?) {
                        }
                    })
                close()
            }
    }

    companion object : KLogging()
}
