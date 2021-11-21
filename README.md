# 🐳 Dokker

The Kotlin DSL to generate your project Dockerfiles with Kotlin.

## Example

The library is created with one purpose: allow easy generation of multiple Dockerfiles with Kotlin.
Check out the example below:
```kotlin
val aptPackages = listOf(
    "build-essential",
    "python3",
    "python3-pip"
)
val environment = mapOf(
    "PYTHON_VERSION" to "3.9.6"
)
val ignores = listOf(
    ".venv"
)
val resources = listOf(
    "examples/.python_version"
)
val project = dokker(resources = resources, ignores = ignores) {
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
project.save("python-example")
```
With the above code, you can generate a project [like this](./example). 

## Contribution

Feel free to extend the Dokker with anything you want : create an issue or send a pull request!
