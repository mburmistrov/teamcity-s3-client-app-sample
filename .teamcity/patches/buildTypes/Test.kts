package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.NodeJSBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Test'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Test")) {
    expectSteps {
        nodeJS {
            name = "Test"
            id = "nodejs_runner_1"
            shellScript = "npm run test:unit"
            dockerImage = "node:18.19.1"
        }
    }
    steps {
        update<NodeJSBuildStep>(0) {
            clearConditions()
            shellScript = """
                if [ -n "%teamcity.build.vcs.branch.TeamcityS3ClientAppSample_HttpsGithubComMburmistrovTeamcityS3clientAppSampleRefsHeadsMain%.tezzzme%" ]; then
                  echo "42"
                  npm run test:unit
                else
                    echo "Skipping build as the required tag is not present."
                fi
            """.trimIndent()
        }
    }
}