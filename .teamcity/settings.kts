import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.provideAwsCredentials
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    vcsRoot(HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain)

    buildType(InstallDeps)
    buildType(Lint)
    buildType(Test)
    buildType(Build)
    buildType(DeployToAws)

    sequential {
        buildType(InstallDeps)
        parallel {
            buildType(Lint)
            buildType(Test)
        }
        buildType(Build)
        buildType(DeployToAws)
    }

    features {
        awsConnection {
            id = "AmazonWebServicesAws"
            name = "Amazon Web Services (AWS)"
            credentialsType = static {
                accessKeyId = "AKIAQC45HKX7ZQ6UHWIB"
                secretAccessKey = "credentialsJSON:b36259fa-883b-4bd6-99c4-81ce6ccfaca1"
            }
            allowInSubProjects = true
            allowInBuilds = true
        }
    }
}

object InstallDeps : BuildType({
    name = "InstallDeps"

    vcs {
        root(HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain)
    }

    steps {
        nodeJS {
            name = "Install deps"
            id = "nodejs_runner"
            shellScript = "npm ci"
            dockerImage = "node:18.19.1"
        }
    }

    features {
        perfmon {
        }
    }
})

object Lint : BuildType({
    name = "Lint"

    vcs {
        root(HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain)
    }

    steps {
        nodeJS {
            name = "Lint"
            id = "nodejs_runner_1"
            shellScript = """
                npm install eslint-teamcity --no-save
                npm run lint -- --format ./node_modules/eslint-teamcity/index.js
            """.trimIndent()
            dockerImage = "node:18.19.1"
        }
    }

    features {
        perfmon {
        }
    }
})

object Test : BuildType({
    name = "Test"

    vcs {
        root(HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain)
    }

    steps {
        nodeJS {
            name = "Test"
            id = "nodejs_runner_1"
            shellScript = "npm run test:unit"
            dockerImage = "node:18.19.1"
        }
    }

    features {
        perfmon {
        }
    }
})

object Build : BuildType({
    name = "Build"

    vcs {
        root(HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain)
    }

    artifactRules = "dist => dist.zip"

    steps {
        nodeJS {
            name = "Build"
            id = "nodejs_runner_2"
            shellScript = "npm run build"
            dockerImage = "node:18.19.1"
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }
})

object DeployToAws : BuildType({
    name = "Deploy to AWS"

    vcs {
        root(DslContext.settingsRoot)
        root(HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain)
    }

    steps {
        script {
            name = "Deploy_"
            id = "Deploy_1"
            scriptContent = """
                cd dist
                /root/.local/bin/aws s3 sync . s3://client-app-sample
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            branchFilter = "+:<default>"
        }
    }

    features {
        perfmon {
        }
        provideAwsCredentials {
            awsConnectionId = "AmazonWebServicesAws"
            sessionDuration = "61"
        }
    }

    dependencies {
        dependency(Build) {
            snapshot {
            }

            artifacts {
                buildRule = lastSuccessful()
                artifactRules = "dist.zip!**=>dist"
            }
        }
    }
})

object HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/mburmistrov/teamcity-s3-client-app-sample#refs/heads/main"
    url = "https://github.com/mburmistrov/teamcity-s3-client-app-sample"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "mburmistrov"
        password = "credentialsJSON:d294ecca-25ab-4916-9e71-c221dbb9facf"
    }
})
