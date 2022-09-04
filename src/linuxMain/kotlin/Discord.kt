import okio.FileSystem
import okio.Path.Companion.toPath

actual fun resolveDiscordDirs(): List<String> {
    return listOf("/usr/share", "/usr/lib64", "/opt").map {
        FileSystem.SYSTEM.list(it.toPath()).filter {
            it.name.startsWith("discord") || it.name.startsWith("Discord")
        }.map {
            FileSystem.SYSTEM.canonicalize(it.resolve("resources")).toString()
        }
    }.flatten()
}

actual fun helperSuffixForPath(path: String): String {
    return when {
        path.contains("DiscordCanary") || path.contains("discord-canary") -> "(Canary)"
        path.contains("DiscordPtb") || path.contains("discord-ptb") -> "(PTB)"
        path.contains("DiscordDevelopment") || path.contains("discord-development") -> "(Dev)"
        path.contains("Discord") || path.contains("discord") -> "(Stable)"
        else -> "(Unknown)"
    }
}