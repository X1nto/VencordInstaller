import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv

actual fun resolveDiscordDirs(): List<String> {
    val localAppData = getenv("localappdata")?.toKString()
        ?: return emptyList()

    return FileSystem.SYSTEM.list(localAppData.toPath()).filter {
        it.name.startsWith("Discord")
    }.map {
        val appFolder = FileSystem.SYSTEM.list(it).firstOrNull {
            it.name.startsWith("app-")
        } ?: return emptyList()
        FileSystem.SYSTEM.canonicalize(it.resolve(appFolder).resolve("resources")).toString()
    }
}

actual fun helperSuffixForPath(path: String): String {
    return when {
        path.contains("DiscordCanary") -> "(Canary)"
        path.contains("DiscordPtb") -> "(PTB)"
        path.contains("DiscordDevelopment") -> "(Dev)"
        path.contains("Discord") -> "(Stable)"
        else -> "(Unknown)"
    }
}