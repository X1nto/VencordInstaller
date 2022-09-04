import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.getenv

actual fun resolveVencordDirs(): List<String> {
    val localAppData = getenv("localappdata")?.toKString()
        ?: return emptyList()

    return FileSystem.SYSTEM.list(localAppData.toPath()).filter {
        it.name.startsWith("Discord")
    }.mapNotNull {
        val appFolder = FileSystem.SYSTEM.list(it).firstOrNull {
            it.name.startsWith("app-")
        } ?: return emptyList()

        val resources = appFolder.resolve("resources")
        if (FileSystem.SYSTEM.exists(resources.resolve("app")))
            FileSystem.SYSTEM.canonicalize(resources).toString()
        else null
    }
}