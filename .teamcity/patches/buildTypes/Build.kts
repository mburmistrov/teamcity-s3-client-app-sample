package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    dependencies {
        remove(RelativeId("Test")) {
            snapshot {
            }
        }

        add(RelativeId("InstallDeps")) {
            snapshot {
            }
        }

    }
}
