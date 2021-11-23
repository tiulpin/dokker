@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.tiulpin

import java.io.File

data class Resource(val name: String, val content: String)

/**
 * DSL for building a [Dokker] instance.
 *
 * @param name the base directory for the project
 * @param tag the name of the project
 * @param registry the version of the project
 * @param dockerFilename the name of the dockerfile
 * @param ignores files to add to dockerignore
 * @param resources additional files to add to the project
 * @param dokkerCommands commands apply to Dokker instance
 */
fun dokker(
    name: String = "dokker",
    tag: String = "latest",
    registry: String = "docker.io",
    dockerFilename: String = "Dockerfile",
    ignores: List<String> = listOf(),
    resources: List<Resource> = listOf(),
    dokkerCommands: Dokker.() -> Unit = {}
): Dokker = Dokker(
    name = name,
    tag = tag,
    registry = registry,
    dockerFilename = dockerFilename,
    ignores = ignores,
    resources = resources
).apply(dokkerCommands)

/**
 * Actual Dokker instance that allows templating Docker projects.
 *
 * @param name the base directory for the project
 * @param tag the name of the project
 * @param registry the version of the project
 * @param dockerFilename the name of the dockerfile
 * @param ignores files to add to dockerignore
 * @param resources additional files to add to the project
 * @param imageName automatically generated image name
 * @param layers all Docker image layers
 */
data class Dokker(
    val name: String = "dokker",
    val tag: String = "latest",
    val registry: String = "docker.io",
    val dockerFilename: String = "Dockerfile",
    val ignores: List<String> = listOf(),
    val resources: List<Resource> = listOf(),
    val imageName: String = "$registry/$name:$tag",
    val layers: MutableList<Layer> = mutableListOf(
        DockerComment("The Dockerfile is auto-generated with Dokker.")
    )
) {
    /**
     * Add a USER layer to the Dockerfile.
     *
     * @param user user to run as
     */
    fun user(user: String) {
        layers.add(DockerUser(user))
    }

    /**
     * Add am EXPOSE layer to the Dockerfile.
     *
     * @param port port to expose
     * @param protocol protocol to use
     */
    fun expose(port: Int, protocol: String = "tcp") {
        layers.add(DockerExpose(port, protocol))
    }

    /**
     * Add a LABEL layer to the Dockerfile.
     *
     * @param variables map of variables to label
     */
    fun label(variables: Map<String, String>) {
        add(DockerLabel(variables))
    }

    /**
     * Add a comment "layer" to the Dockerfile.
     *
     * @param comment comment to add
     */
    fun comment(comment: String) {
        add(DockerComment(comment))
    }

    /**
     * Add a FROM layer to the Dockerfile.
     *
     * @param image image name to base from
     * @param tag image tag to base from
     * @param alias alias to use for the layer
     */
    fun from(image: String, tag: String = "latest", alias: String? = null) {
        add(DockerFrom(image, tag, alias))
    }

    /**
     * Add a RUN layer to the Dockerfile.
     *
     * @param commands commands to run
     */
    fun run(commands: List<String>) {
        add(DockerRun(commands))
    }

    /**
     * Add a ENV layer to the Dockerfile.
     *
     * @param variables map of variables to set
     */
    fun env(variables: Map<String, String>) {
        add(DockerEnv(variables))
    }

    /**
     * Add a ARG layer to the Dockerfile.
     *
     * @param value ARG value
     */
    fun arg(value: String) {
        add(DockerArg(value))
    }

    /**
     * Add a COPY layer to the Dockerfile.
     * Called [kopy] to avoid conflict with the Kotlin copy() function.
     *
     * @param what what to copy
     * @param to where to copy to
     * @param from from which layer to copy from
     */
    fun kopy(what: String, to: String, from: String? = null) {
        add(DockerCopy(what, to, from))
    }

    /**
     * Add a COPY layer to the Dockerfile.
     * Called [kopy] to avoid conflict with the Kotlin copy() function.
     *
     * @param what what to copy
     * @param to where to copy to
     * @param from from which layer to copy from
     */
    fun kopy(what: List<String>, to: String, from: String? = null) {
        kopy(what.joinToString(" "), to, from)
    }

    /**
     * Add a WORKDIR layer to the Dockerfile.
     *
     * @param dir directory to set
     */
    fun workdir(dir: String) {
        add(DockerWorkdir(dir))
    }

    /**
     * Add a layer to the Dockerfile.
     */
    fun entrypoint(command: String) {
        add(DockerEntrypoint(command))
    }

    /**
     * Add a layer to the Dockerfile.
     *
     * @param layer the layer to add
     */
    fun add(layer: Layer) {
        layers.add(layer)
    }

    /**
     * Generate the Dockerfile.
     *
     */
    fun generateDockerfile(): String {
        return layers.joinToString(separator = "\n") + "\n"
    }

    /**
     * Generate the .dockerignore.
     */
    fun generateDockerIgnore(): String {
        return ignores.joinToString(separator = "\n") + "\n"
    }

    /**
     * Save the project.
     *
     * @param projectDir the directory to save the project to
     */
    fun save(projectDir: String) {
        val project = File(projectDir).apply {
            mkdirs()
        }

        val dockerFile = File(project, dockerFilename)
        dockerFile.writeText(generateDockerfile())

        val dockerIgnore = generateDockerIgnore()
        if (dockerIgnore.isNotEmpty()) {
            File(project, ".dockerignore").apply {
                writeText(dockerIgnore)
            }
        }

        resources.forEach {
            File(project, it.name).apply {
                writeText(it.content)
            }
        }
    }
}

/**
 * Wrap the given environment variable in quotes.
 *
 * @param key variable name
 * @param value variable value
 * @return the wrapped string
 */
fun wrapEnvVar(key: String, value: String): String {
    return "$key=\"$value\""
}

/**
 * Wrap the given string in braces.
 *
 * @param value the value to wrap
 * @return the wrapped string
 */
fun envBraces(value: String): String {
    return "\${$value}"
}

/**
 * Wrap the given string in quotes.
 *
 * @param value the value to wrap
 * @return the wrapped string
 */
fun envQuotes(value: String): String {
    return "\"\$$value\""
}

/**
 * Generate apt-install commands.
 *
 * @param packages the packages to install
 * @param update whether to run the apt-update or not
 * @param clear whether to run the apt clean commands or not
 * @return the apt-install commands
 */
fun aptInstall(packages: List<String>, update: Boolean = true, clear: Boolean = true): String {
    val installPackages = packages.joinToString(separator = " ")
    var command = "apt-get install -y $installPackages"
    if (update) {
        command = "apt-get update$COMMAND_SEP$command"
    }
    if (clear) {
        command = "$command${COMMAND_SEP}apt-get autoremove -y && apt-get clean && rm -r /var/cache/apt /var/lib/apt/"
    }
    return "DEBIAN_FRONTEND=noninteractive $command"
}
