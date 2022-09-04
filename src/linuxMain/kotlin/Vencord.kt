import okio.FileSystem
import okio.Path.Companion.toPath

actual fun resolveVencordDirs(): List<String> {
    return listOf("/usr/share", "/usr/lib64", "/opt").map {
        FileSystem.SYSTEM.list(it.toPath()).filter {
            it.name.startsWith("discord") || it.name.startsWith("Discord")
        }.mapNotNull {
            val resources = it.resolve("resources")
            if (FileSystem.SYSTEM.exists(resources.resolve("app")))
                FileSystem.SYSTEM.canonicalize(resources).toString()
            else null
        }
    }.flatten()
}