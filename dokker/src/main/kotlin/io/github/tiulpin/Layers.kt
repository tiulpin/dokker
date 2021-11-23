package io.github.tiulpin

const val FROM = "FROM"
const val RUN = "RUN"
const val COPY = "COPY"
const val ENTRYPOINT = "ENTRYPOINT"
const val ARG = "ARG"
const val LABEL = "LABEL"
const val EXPOSE = "EXPOSE"
const val ENV = "ENV"
const val USER = "USER"
const val WORKDIR = "WORKDIR"

const val COMMAND_SEP = " && \\\n    "
const val COPY_FROM = "--from"

interface Layer {
    override fun toString(): String
}

data class DockerExpose(val port: Int, val protocol: String = "tcp") : Layer {
    override fun toString(): String {
        return "$EXPOSE $port/$protocol"
    }
}

data class DockerUser(val user: String) : Layer {
    override fun toString(): String {
        return "$USER $user"
    }
}

data class DockerEnv(val variables: Map<String, String>) : Layer {
    override fun toString(): String {
        return "$ENV ${variables.toList().joinToString(" ") { wrapEnvVar(it.first, it.second) }}"
    }
}

data class DockerArg(val key: String) : Layer {
    override fun toString(): String {
        return "$ARG $key"
    }
}

data class DockerWorkdir(val dir: String): Layer {
    override fun toString(): String {
        return "$WORKDIR $dir"
    }
}

data class DockerEntrypoint(val command: String) : Layer {
    override fun toString(): String {
        return "$ENTRYPOINT [\"$command\"]"
    }
}

data class DockerCopy(val what: String, val to: String, val from: String?) : Layer {
    override fun toString(): String {
        return when (from) {
            null -> "$COPY $what $to"
            else -> "$COPY $COPY_FROM=$from $what $to"
        }
    }
}

data class DockerRun(val commands: List<String>) : Layer {
    override fun toString(): String {
        return "$RUN ${commands.joinToString(COMMAND_SEP)}"
    }
}

data class DockerLabel(val variables: Map<String, String>) : Layer {
    override fun toString(): String {
        return "$LABEL ${variables.toList().joinToString(" ") { wrapEnvVar(it.first, it.second) }}"
    }
}

data class DockerFrom(val from: String, val tag: String = "latest", val alias: String? = null) : Layer {
    override fun toString(): String {
        return when (alias) {
            null -> "$FROM $from:$tag"
            else -> "$FROM $from:$tag AS $alias"
        }
    }
}

data class DockerComment(val comment: String): Layer {
    override fun toString(): String{
        return "# $comment"
    }
}
