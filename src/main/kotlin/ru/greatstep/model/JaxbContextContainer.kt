package ru.greatstep.model

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.Unmarshaller
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Утилитный класс контейнер для JaxbContext
 */
object JaxbContextContainer {
    val jaxbContext = ConcurrentHashMap<KClass<out Any>, JAXBContext>(64)

    fun createMarshaller(clazz: KClass<out Any>): Marshaller = getJaxbContext(clazz).createMarshaller()

    fun createUnmarshaller(clazz: KClass<out Any>): Unmarshaller = getJaxbContext(clazz).createUnmarshaller()

    private fun getJaxbContext(clazz: KClass<out Any>): JAXBContext = this.jaxbContext[clazz] ?: putJaxbContext(clazz)

    private fun putJaxbContext(clazz: KClass<out Any>): JAXBContext {
        val jaxbContext = JAXBContext.newInstance(clazz.java)
        this.jaxbContext.putIfAbsent(clazz, jaxbContext)
        return jaxbContext
    }
}