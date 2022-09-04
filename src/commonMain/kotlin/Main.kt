private enum class Mode {
    Install,
    Uninstall
}

private sealed interface UserInputDiscordPath {
    class Resolved(val path: String) : UserInputDiscordPath
    object Other : UserInputDiscordPath
}

fun main() {
    val modeSelector = optionSelector("Please select the action:") {
        addOption("Install" to Mode.Install)
        addOption("Uninstall" to Mode.Uninstall)
    }

    when (modeSelector.requestSelect()) {
        Mode.Install -> {
            val resourcesDir = requestResourcesDir(resolveDiscordDirs())

            print("Please input the patcher path: ")
            val patcher = readln()

            when(val result = copyInstallationFiles(
                discordResourcesPath = resourcesDir,
                patcherPath = patcher
            )) {
                is InstallResult.Success -> {
                    println("Successfully installed Vencord.")
                }
                is InstallResult.Failed -> {
                    println("Installation failed: ${result.message}")
                }
            }
        }
        Mode.Uninstall -> {
            val resourcesDir = requestResourcesDir(resolveVencordDirs())

            when (val result = removeInstallationFiles(resourcesDir)) {
                is InstallResult.Success -> {
                    println("Successfully uninstalled Vencord.")
                }
                is InstallResult.Failed -> {
                    println("Uninstallation failed: ${result.message}")
                }
            }
        }
    }
}

private fun requestResourcesDir(dirs: List<String>): String {
    val selector = optionSelector("Please select the Discord path:") {
        dirs.forEach { path ->
            addOption(path + " " + helperSuffixForPath(path) to UserInputDiscordPath.Resolved(path))
        }
        addOption("Other (requires manual input)" to UserInputDiscordPath.Other)
    }

    return when (val result = selector.requestSelect()) {
        is UserInputDiscordPath.Resolved -> {
            result.path
        }
        is UserInputDiscordPath.Other -> {
            print("Please specify the path (the path must point to the resources folder of Discord): ")
            readln()
        }
    }
}