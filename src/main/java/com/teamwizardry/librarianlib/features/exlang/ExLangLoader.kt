package com.teamwizardry.librarianlib.features.exlang

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.exlang.parser.ExLangFile
import com.teamwizardry.librarianlib.features.exlang.parser.ExLangValue
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.LanguageMap
import org.apache.commons.io.IOUtils
import java.io.*
import java.nio.charset.Charset
import java.nio.file.Paths

val SimpleReloadableResourceManager.domainResourceManagers_mh by MethodHandleHelper
        .delegateForReadOnly<SimpleReloadableResourceManager, Map<String, FallbackResourceManager>>(
                "domainResourceManagers", "field_110548_a"
        )
val FallbackResourceManager.resourcePacks_mh by MethodHandleHelper
        .delegateForReadOnly<FallbackResourceManager, List<IResourcePack>>(
                "resourcePacks", "field_110540_a"
        )

val LanguageMap_instance_mh by MethodHandleHelper
        .delegateForStaticReadOnly<LanguageMap, LanguageMap>(
                "instance", "field_74817_a"
        )
val LanguageMap.languageList_mh by MethodHandleHelper
        .delegateForReadOnly<LanguageMap, MutableMap<String, String>>(
                "languageList", "field_74816_c"
        )

object ExLangLoader {
    val DOCS_PREFIX = "// Written in the LibrarianLib ExLang format. More information at: https://wiki.teamwizardry.com/wiki/ExLang"
    val invalidHeaders = mutableListOf<ResourceLocation>()
    val correctedHeaders = mutableListOf<ResourceLocation>()

    init {
        LibrarianLib.PROXY.addReloadHandler(ClientRunnable {
            reload(Minecraft.getMinecraft().resourceManager)
        })
    }

    fun reload(resourceManager: IResourceManager) {
        var fallbackResourceManagers: Map<String, FallbackResourceManager> = emptyMap()
        var fallbackResourceManager: FallbackResourceManager? = null
        when(resourceManager) {
            is SimpleReloadableResourceManager -> {
                fallbackResourceManagers = resourceManager.domainResourceManagers_mh
            }
            is FallbackResourceManager -> {
                fallbackResourceManager = resourceManager
            }
            else -> {
                throw UnsupportedResourceManagerException(resourceManager.javaClass)
            }
        }
        val languages = mutableSetOf("en_us",
                Minecraft.getMinecraft().languageManager.currentLanguage.languageCode).toList()

        val domains = mutableMapOf<String, ExLangDomain>()
        resourceManager.resourceDomains.forEach { domain ->
            val manager = fallbackResourceManagers[domain] ?: fallbackResourceManager ?: return@forEach
            domains[domain] = ExLangDomain(domain, manager, languages)
        }

        val duplicates = mutableMapOf<String, Map<String, ExLangValue>>()
        val mcDuplicates = mutableMapOf<String, Map<String, ExLangValue>>()
        domains.forEach { (domain, exlang) ->
            duplicates[domain] = exlang.entries.filter { (key, _) ->
                domains.any { it.value.entries.containsKey(key) }
            }
            mcDuplicates[domain] = exlang.entries.filter { (key, _) ->
                I18n.hasKey(key)
            }
        }

        val entries = mutableMapOf<String, String>()
        domains.forEach { (domain, exlang) ->
            entries.putAll(exlang.entries.filter { (key, _) ->
                !(duplicates[domain]?.containsKey(key) == true || mcDuplicates[domain]?.containsKey(key) == true)
            }.mapValues { it.value.text })
        }

        LanguageMap_instance_mh.languageList_mh.putAll(entries)

        correctedHeaders.map {
            LibrarianLog.info("Added 'Written in...' header to $it")
        }
        if(invalidHeaders.isNotEmpty()) {
            val list = mutableListOf<String>()

            list.add("The following files don't have the correct header and could not be automatically corrected")
            list.addAll(invalidHeaders.map { "$it" })
            list.add("This error will only occur in a development environment" +
                    " and thus should never happen to a resource pack author or user")
            list.add("")
            list.add("All ExLang files are required to have the following header (minus the backticks):")
            list.add("`$DOCS_PREFIX`")
            list.add("This is for the sake of translators, since they likely have not have seen ExLang files before" +
                    " and will need an explanation of how they work before they can begin translating.")

            LibrarianLog.bigDie("ERROR: ExLang files missing headers", list)
        }
    }

    fun checkHeader(location: ResourceLocation, stream: InputStream): Reader {
        try {
            val resourceText = IOUtils.toString(stream, Charsets.UTF_8)

            if (LibrarianLib.DEV_ENVIRONMENT) {
                var generate = false

                val file = Paths.get(JsonGenerationUtils.getAssetPath(location.resourceDomain), location.resourcePath).toFile()
                if (file.exists()) {
                    try {
                        val localText = file.readText()
                        if (localText == resourceText) generate = true
                    } catch (e: IOException) {
                        LibrarianLog.error("Error reading local ExLang file", e)
                    }
                }

                if (!resourceText.startsWith(DOCS_PREFIX) && location !in correctedHeaders) {
                    if (generate) {
                        try {
                            file.writeText(DOCS_PREFIX + "\n" + resourceText)
                            correctedHeaders.add(location)
                        } catch (e: IOException) {
                            LibrarianLog.error("Error writing corrected ExLang file", e)
                        }
                    } else {
                        invalidHeaders.add(location)
                    }
                }
            }

            if (resourceText.isEmpty()) { // the lexer barfs on empty input
                return StringReader(" ")
            }
            return StringReader(resourceText)
        } catch(e: IOException) {
            LibrarianLog.error("Error loading ExLang file $location", e)
            return StringReader(" ")
        } finally {
            IOUtils.closeQuietly(stream)
        }
    }
}

// Note: In all of these classes, items later in a list override those earlier in the list

class ExLangDomain(val domain: String, manager: FallbackResourceManager, val languages: List<String>) {
    val entries = mutableMapOf<String, ExLangValue>()

    init {
        val packs = manager.resourcePacks_mh

        packs.forEach { pack ->
            languages.forEach { lang ->
                entries.putAll(loadLang(pack, lang))
            }
        }
    }

    fun loadLang(pack: IResourcePack, language: String): Map<String, ExLangValue> {
        val resource = ResourceLocation(domain, "lang/$language.exlang")
        val includeRoot = "lang/$language/"
        if(!pack.resourceExists(resource)) return emptyMap()

        val file = ExLangFile(pack, resource, emptyList())
        file.prefix = includeRoot
        file.parse()
        file.compute()
        val lists = file.collect()
        warnDuplicates(pack, language, lists.duplicated)
        return lists.entries
    }

    fun warnDuplicates(pack: IResourcePack, language: String, duplicated: Map<String, List<ExLangValue>>) {
        if(duplicated.isEmpty()) return

        LibrarianLog.warn("Duplicate lang entries when loading $language.exlang for $domain in pack ${pack.packName}")
        duplicated.forEach { (id, occurrences) ->
            var prefix = id
            var first = true
            occurrences.forEach {
                LibrarianLog.warn("$prefix | " + it.location.locationString)
                if(first) {
                    first = false
                    prefix = " " * prefix.length
                }
            }
        }
    }
}

class UnsupportedResourceManagerException(klass: Class<*>) : RuntimeException(
        "LibrarianLib .exlang system does not recognize the IResourceManager `${klass.canonicalName}`"
)
