package com.tamin.taminhamrah.data.tools

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class
RuntimeTypeAdapterFactory<T> private constructor(
    private val baseType: Class<*>,
    private val typeFieldName: String,
    private val maintainType: Boolean
) : TypeAdapterFactory {

    private val labelToSubtype = mutableMapOf<String, Class<*>>()
    private val subtypeToLabel = mutableMapOf<Class<*>, String>()

    fun registerSubtype(type: Class<out T>, label: String = type.simpleName): RuntimeTypeAdapterFactory<T> {
        if (labelToSubtype.containsKey(label) || subtypeToLabel.containsKey(type)) {
            throw IllegalArgumentException("Types and labels must be unique.")
        }
        labelToSubtype[label] = type
        subtypeToLabel[type] = label
        return this
    }

    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType != baseType) return null

        val labelToDelegate = mutableMapOf<String, TypeAdapter<*>>()
        val subtypeToDelegate = mutableMapOf<Class<*>, TypeAdapter<*>>()

        for ((label, subtype) in labelToSubtype) {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(subtype))
            labelToDelegate[label] = delegate
            subtypeToDelegate[subtype] = delegate
        }

        return object : TypeAdapter<R>() {
            override fun write(out: JsonWriter, value: R) {
                if (value == null) {
                    out.nullValue()
                    return
                }

                val srcType = value::class.java
                val label = subtypeToLabel[srcType]
                    ?: throw JsonParseException("Cannot serialize $srcType; did you forget to register it?")

                val delegate = subtypeToDelegate[srcType] as TypeAdapter<R>
                val jsonObject = delegate.toJsonTree(value).asJsonObject

                if (!maintainType) {
                    val clone = JsonObject()
                    clone.add(typeFieldName, JsonPrimitive(label))
                    for ((k, v) in jsonObject.entrySet()) {
                        clone.add(k, v)
                    }
                    Streams.write(clone, out)
                } else {
                    jsonObject.add(typeFieldName, JsonPrimitive(label))
                    Streams.write(jsonObject, out)
                }
            }

            override fun read(input: JsonReader): R {
                val jsonElement = Streams.parse(input)
                val labelElement = jsonElement.asJsonObject.get(typeFieldName)
                    ?: throw JsonParseException("Missing type field '$typeFieldName' in $jsonElement")

                val label = labelElement.asString
                val delegate = labelToDelegate[label] as TypeAdapter<R>?
                    ?: throw JsonParseException("Unknown type label $label")

                return delegate.fromJsonTree(jsonElement)
            }
        }
    }

    companion object {
        fun <T> of(baseType: Class<T>, typeFieldName: String, maintainType: Boolean = false): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, maintainType)
        }
    }
}
