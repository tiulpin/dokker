import io.github.tiulpin.*
import java.io.File
import kotlin.test.*

class DokkerTest {
    private val dockerfile = """
            FROM ubuntu:latest
            ARG platform
            LABEL maintainer="viktor@tiulp.in"
            USER root
            ENV PYTHON_VERSION="3.9.6"
            COPY .python_version /tmp
            RUN apt-get update && \
                DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends  \
                    build-essential \
                    python3 \
                    python3-pip && \
                apt-get autoremove -y && apt-get clean && rm -r /var/cache/apt /var/lib/apt/ && \
                echo ${envBraces("PYTHON_VERSION")}
            EXPOSE 8080/tcp
            WORKDIR /
            ENTRYPOINT ["python3"]
            # We did it!
            
        """.trimIndent()

    private val dockerIgnore = """
            .venv
            
        """.trimIndent()

    private val aptPackages = listOf(
        "build-essential",
        "python3",
        "python3-pip"
    )
    private val environment = mapOf(
        "PYTHON_VERSION" to "3.9.6"
    )
    private val ignores = listOf(
        ".venv"
    )
    private val resources = listOf(
        Resource(".python_version", "3.9.6")
    )
    private val project = dokker(resources = resources, ignores = ignores) {
        from("ubuntu")
        arg("platform")
        label(
            mapOf(
                "maintainer" to "viktor@tiulp.in"
            )
        )
        user("root")
        env(environment)
        kopy(listOf(".python_version"), "/tmp")
        run(
            listOf(
                aptInstall(aptPackages),
                "echo ${envBraces("PYTHON_VERSION")}"
            )
        )
        expose(8080)
        workdir("/")
        entrypoint("python3")
        comment("We did it!")
    }

    @Test
    fun testDockerfile() {
        assertEquals(dockerfile, project.generateDockerfile())
    }

    @Test
    fun testDockerIgnore() {
        assertEquals(dockerIgnore, project.generateDockerIgnore())
    }

    @Test
    fun testProjectSave() {
        project.save("../example")
        assert(
            File("../example/Dockerfile").exists() &&
                    File("../example/.dockerignore").exists() &&
                    File("../example/.python_version").exists()
        )
    }
}
