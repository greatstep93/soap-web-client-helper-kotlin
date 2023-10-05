package ru.greatstep.helper.util

import jakarta.xml.bind.JAXBElement
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlElementRef
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class SoapConstraintValidator {

    companion object {
        fun isValid(obj: Any): Boolean = validate(obj).isBlank()

        fun validate(obj: Any): String {
            val errorMessages = mutableSetOf<String>()
            validate(obj, errorMessages)
            return errorMessages.joinToString(";")
        }

        private fun validate(obj: Any, errorMessages: MutableSet<String>) {
            obj::class.memberProperties.forEach { field ->
                field.annotations.filterIsInstance<XmlElement?>()[0]?.let {
                    checkError(it.required, field.name, obj, errorMessages)
                }
                field.annotations.filterIsInstance<XmlElementRef?>()[0]?.let {
                    checkError(it.required, field.name, obj, errorMessages)
                }
            }
        }

        private fun checkError(required: Boolean, fieldName: String, obj: Any, errorMessages: MutableSet<String>) {
            try {
                val value = getField(obj, fieldName)
                if (required && (value == null || value.toString().isBlank())) {
                    errorMessages.plus("Required field $fieldName missing")

                } else if (value != null && value::class.javaPrimitiveType != null) {
                    when (value) {
                        is List<*> -> value.forEach { it?.let { validate(it, errorMessages) } }
                        is JAXBElement<*> -> value.value?.let { validate(it, errorMessages) }
                    }
                }

            } catch (ex: Exception) {
                errorMessages.plus("Error validating $fieldName field")
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun getField(obj: Any, fieldName: String) = obj::class.memberProperties
            .first { it.name == fieldName }
            .also { it.isAccessible = true }
            .let { it as KProperty1<in Any, *> }
            .getter(obj)
    }
}