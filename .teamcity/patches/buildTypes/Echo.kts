package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Echo'
in the root project, and delete the patch script.
*/
create(DslContext.projectId, BuildType({
    id("Echo")
    name = "Echo"

    vcs {
        root(RelativeId("HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain"))
    }

    steps {
        script {
            id = "simpleRunner"
            scriptContent = """
                last_commit_tag = git describe --tags %build.vcs.number%
                echo "123"
                echo  "$last_commit_tag"
                echo  "${'$'}$last_commit_tag"
                echo "321"
                if [ "${'$'}last_commit_tag" == "tezzzme" ]; then
                  echo "has tezzzme tag"
                else
                  echo "dont have tezzzme tag"
                fi
            """.trimIndent()
        }
    }
}))

