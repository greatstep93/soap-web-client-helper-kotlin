package ru.greatstep.helper.model

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.Unmarshaller
import kotlin.reflect.KClass

/**
 * Утилитный класс контейнер для JaxbContext
 */
object JaxbContextContainer {
    val jaxbContext = mutableMapOf<KClass<out Any>, JAXBContext>()
    val jaxbContext2 = mutableMapOf<Class<*>, JAXBContext>()

    fun createMarshaller(clazz: KClass<out Any>): Marshaller = getJaxbContext(clazz).createMarshaller()

    fun createUnmarshaller(outputClass: Class<*>): Unmarshaller = getJaxbContext(outputClass).createUnmarshaller()

    private fun getJaxbContext(clazz: KClass<out Any>): JAXBContext = this.jaxbContext[clazz] ?: putJaxbContext(clazz)
    private fun getJaxbContext(outputClass: Class<*>): JAXBContext = this.jaxbContext2[outputClass] ?: putJaxbContext(outputClass)
    private fun putJaxbContext(clazz: KClass<out Any>): JAXBContext {
        val jaxbContext = JAXBContext.newInstance(clazz.java)
        this.jaxbContext.putIfAbsent(clazz, jaxbContext)
        return jaxbContext
    }
    private fun putJaxbContext(outputClass: Class<*>): JAXBContext {
        val jaxbContext = JAXBContext.newInstance(outputClass)
        this.jaxbContext2.putIfAbsent(outputClass, jaxbContext)
        return jaxbContext
    }
}