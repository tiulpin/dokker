job("dokker: Publish library") {
    startOn {
        gitPush {
            branchFilter {
                +"refs/heads/main"
            }
        }
    }
    job("Build and publish") {
        container(displayName = "Run publish script", image = "gradle") {
            kotlinScript { api ->
                api.gradle("build")
                api.gradle("publish")
            }
        }
    }
}