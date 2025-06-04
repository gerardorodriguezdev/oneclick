package buildLogic.convention.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class CreateWebpackConfigTask : DefaultTask() {

    @get: Input
    abstract val ignoredFiles: ListProperty<String>

    @get: OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun createFile() {
        val ignoredFiles = ignoredFiles.get()
        if (ignoredFiles.isEmpty()) return

        val ignoredFilesString = ignoredFiles.toIgnoredFilesString()
        val outputFile = outputFile.get().asFile
        outputFile.writeText(
            webpackConfigContent(ignoredFilesString = ignoredFilesString)
        )
    }

    private fun webpackConfigContent(ignoredFilesString: String): String =
        //language=javascript
        """
            if (config.devServer) {
                const directories = config.devServer.static

                config.devServer = {
                    ...config.devServer,
                    static: directories.map(item => ({
                        directory: item,
                        watch: {
                            ignored: [$ignoredFilesString],
                            usePolling: false
                        }
                    }))
                }
            }
        """.trimIndent()

    private fun List<String>.toIgnoredFilesString(): String =
        joinToString(separator = ",", transform = { "'$it'" })
}