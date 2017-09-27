@file:JvmName("JsonMaker")

package com.teamwizardry.librarianlib.features.kotlin

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import net.minecraft.util.ResourceLocation
import java.io.StringWriter

/**
 * @author WireSegal
 * Created at 7:34 PM on 9/28/16.
 * and elad fixed the shit out of it to make it cool
 */


object JSON {

    fun array(vararg args: Any?): JsonArray {
        val arr = JsonArray()
        args.forEach { arr.add(convertJSON(it)) }
        return arr
    }

    fun obj(vararg args: Pair<String, *>): JsonObject {
        val obj = JsonObject()
        args.forEach { obj.add(it.first, convertJSON(it.second)) }
        return obj
    }
}

fun array(vararg args: Any?): JsonArray {
    val arr = JsonArray()
    args.forEach { arr.add(convertJSON(it)) }
    return arr
}

fun obj(vararg args: Pair<String, *>): JsonObject {
    val obj = JsonObject()
    args.forEach { obj.add(it.first, convertJSON(it.second)) }
    return obj
}

class Json {
    val inputs = mutableListOf<Any>()
    operator fun Any.unaryPlus(): Json {
        inputs.add(this)
        return this@Json
    }

    fun convert(): JsonElement {
        return when {
            inputs.size == 1 -> convertJSON(inputs[0])
            inputs.isEmpty() -> JsonNull.INSTANCE
            inputs.all { it is Pair<*, *> } -> obj(*inputs.map { it as Pair<String, *> }.toTypedArray())
            else -> array(*inputs.toTypedArray())
        }
    }
}

fun convertJSON(value: Any?): JsonElement = when (value) {
    null -> JsonNull.INSTANCE
    is Char -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is String -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is Array<*> -> array(*value)
    is Collection<*> -> array(*value.toTypedArray())
    is Map<*, *> -> obj(*value.toList().map { it.first.toString() to it.second }.toTypedArray())
    is JsonElement -> value
    is ResourceLocation -> JsonPrimitive(value.toString())
    else -> throw IllegalArgumentException("Unrecognized type: " + value)
}

// Not inline because hot reloading fails on inline obfuscated classes under some circumstances
fun json(lambda: Json.() -> JsonElement) = Json().lambda()

fun JsonElement.serialize(): String {
    val stringWriter = StringWriter()
    val jsonWriter = JsonWriter(stringWriter)
    jsonWriter.serializeNulls = true
    jsonWriter.setIndent("\t")
    Streams.write(this, jsonWriter)
    return stringWriter.toString() + "\n"
}
