import okio.FileHandle
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.use

sealed interface InstallResult {
    object Success : InstallResult
    class Failed(val message: String) : InstallResult
}

expect fun resolveVencordDirs(): List<String>

fun copyInstallationFiles(
    discordResourcesPath: String,
    patcherPath: String
): InstallResult {
    val vencordPath = discordResourcesPath.toPath().resolve("app")

    if (FileSystem.SYSTEM.exists(vencordPath)) {
        return InstallResult.Failed("Vencord is already installed.")
    }

    try {
        FileSystem.SYSTEM.createDirectory(vencordPath, mustCreate = true)
    } catch (e: Exception) {
        return InstallResult.Failed("Failed to create the Vencord directory: ${e.message}")
    }

    if (!FileSystem.SYSTEM.exists(vencordPath)) {
        return InstallResult.Failed("Could not create the directory for Vencord.")
    }

    val indexJsPath = vencordPath.resolve("index.js")
    try {
        FileSystem.SYSTEM.openReadWrite(indexJsPath, mustCreate = true, mustExist = false).use {
            it.writeText(getIndexJsContent(patcherPath))
        }
    } catch (e: Exception) {
        return InstallResult.Failed("Failed to create index.js: ${e.message}")
    }

    val packageJsonPath = vencordPath.resolve("package.json")
    try {
        FileSystem.SYSTEM.openReadWrite(packageJsonPath, mustCreate = true, mustExist = false).use {
            it.writeText(getPackageJsonContent())
        }
    } catch (e: Exception) {
        return InstallResult.Failed("Failed to create package.json: ${e.message}")
    }

    return InstallResult.Success
}

fun removeInstallationFiles(discordResourcesPath: String): InstallResult {
    val vencordPath = discordResourcesPath.toPath().resolve("app")

    if (!FileSystem.SYSTEM.exists(vencordPath)) {
        throw RuntimeException("Could not find Vencord.")
    }

    try {
        FileSystem.SYSTEM.deleteRecursively(vencordPath, mustExist = true)
    } catch (e: Exception) {
        return InstallResult.Failed("Failed to delete the Vencord folder: ${e.message}")
    }

    return InstallResult.Success
}

private fun FileHandle.writeText(source: String) {
    val sourceArray = source.encodeToByteArray()
    write(
        fileOffset = 0L,
        array = sourceArray,
        arrayOffset = 0,
        byteCount = sourceArray.size
    )
}

private fun getIndexJsContent(patcherPath: String): String {
    return """
        require("${patcherPath.replace("\\", "/")}");
        require("../app.asar");
    """.trimIndent()
}

private fun getPackageJsonContent(): String {
    return """
        {
          "main": "index.js",
          "name": "discord"
        }
    """.trimIndent()
}