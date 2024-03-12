package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.NodeJSBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Lint'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Lint")) {
    check(name == "Lint") {
        "Unexpected name: '$name'"
    }
    name = "i18n Staging"

    expectSteps {
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
    steps {
        update<NodeJSBuildStep>(0) {
            clearConditions()

            conditions {
                doesNotContain("teamcity.build.branch", "skip-lint")
            }
            shellScript = """
                ls
                # Check if the file exists
                if [ -e "./linted-branches/%teamcity.build.branch%" ]; then
                  npm install eslint-teamcity --no-save
                  npm run lint -- --format ./node_modules/eslint-teamcity/index.js
                else
                  echo "skip lint"
                fi
            """.trimIndent()
        }
    }
}
