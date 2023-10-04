package ru.greatstep.util

import jakarta.xml.bind.JAXBException
import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.XmlType
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.CodecException
import org.springframework.core.codec.Encoder
import org.springframework.core.codec.EncodingException
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.PooledDataBuffer
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.ws.WebServiceMessageFactory
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.support.DefaultStrategiesHelper
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.greatstep.model.JaxbContextContainer
import ru.greatstep.model.SoapEnvelopeRequest
import java.nio.charset.StandardCharsets
import java.rmi.MarshalException
import kotlin.reflect.KClass

@Component
class Jaxb2SoapEncoder(val jaxbContext: JaxbContextContainer = JaxbContextContainer) : Encoder<Any> {
    override fun canEncode(elementType: ResolvableType, mimeType: MimeType?): Boolean {
        return elementType::class.annotations.filterIsInstance<XmlRootElement?>().isNotEmpty()
                || elementType::class.annotations.filterIsInstance<XmlType?>().isNotEmpty()
    }

    override fun getEncodableMimeTypes(): List<MimeType> {
        return listOf(MimeTypeUtils.TEXT_XML)
    }

    override fun encode(
        inputStream: Publisher<out Any>,
        bufferFactory: DataBufferFactory,
        elementType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?
    ): Flux<DataBuffer> {
        return Flux.from(inputStream)
            .take(1)
            .concatMap { encode(it, bufferFactory) }
            .doOnDiscard(PooledDataBuffer::class.java, PooledDataBuffer::release)
    }

    private fun encode(value: Any, bufferFactory: DataBufferFactory): Flux<DataBuffer> {

        return Mono.fromCallable {
            var release = true
            val buffer = bufferFactory.allocateBuffer(1024)
            try {
                value as SoapEnvelopeRequest
                val message = DefaultStrategiesHelper(WebServiceTemplate::class.java)
                    .getDefaultStrategy(WebServiceMessageFactory::class.java)
                    .createWebServiceMessage()
                initMarshaller(value.body::class).marshal(value.body,message.payloadResult)
                message.writeTo(buffer.asOutputStream())
                release = false
                return@fromCallable buffer
            } catch (ex: MarshalException) {
                throw EncodingException("Could not marshal ${value::class.java} to XML", ex)
            } catch (ex: JAXBException) {
                throw CodecException("Invalid JAXB configuration", ex)
            } finally {
                if (release) DataBufferUtils.release(buffer)
            }
        }.flux()


    }

    private fun initMarshaller(clazz: KClass<out Any>): Marshaller {
        val marshaller = this.jaxbContext.createMarshaller(clazz)
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name())
        return marshaller
    }

}