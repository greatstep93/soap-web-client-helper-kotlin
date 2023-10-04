package ru.greatstep.util

import jakarta.xml.bind.JAXBException
import jakarta.xml.bind.UnmarshalException
import org.springframework.core.ResolvableType
import org.springframework.core.codec.CodecException
import org.springframework.core.codec.DecodingException
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.WebServiceMessageFactory
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.support.DefaultStrategiesHelper
import reactor.core.Exceptions
import ru.greatstep.model.JaxbContextContainer
import ru.greatstep.model.SoapEnvelopeResponse
import javax.xml.stream.XMLStreamException
import javax.xml.transform.dom.DOMSource
import kotlin.reflect.KClass

@Component
class Jaxb2SoapDecoder(val jaxbContext: JaxbContextContainer = JaxbContextContainer) : Jaxb2XmlDecoder() {

    companion object {
        private const val RESPONSE_WITH_HEADERS = "ru.greatstep.model.SoapEnvelopeResponse"
    }

    override fun decode(
        dataBuffer: DataBuffer,
        targetType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?
    ): Any {
        try {
            val message = DefaultStrategiesHelper(WebServiceTemplate::class.java)
                .getDefaultStrategy(WebServiceMessageFactory::class.java)
                .createWebServiceMessage(dataBuffer.asInputStream())
            return if (targetType.rawClass?.typeName == RESPONSE_WITH_HEADERS) {
                unmarshalWithHeaders(message, targetType)
            } else {
                unmarshal(message, targetType::class)
            }

        } catch (ex: Exception) {
            when (ex) {
                is XMLStreamException -> throw Exceptions.propagate(ex.cause ?: ex)
                else -> throw Exceptions.propagate(ex)
            }
        } finally {
            DataBufferUtils.release(dataBuffer)
        }

    }

    private fun unmarshalWithHeaders(message: WebServiceMessage, targetType: ResolvableType) =
        SoapEnvelopeResponse(
            unmarshal(message, targetType.generics[0].toClass().kotlin),
            (message.payloadSource as DOMSource).node.ownerDocument
        )


    private fun unmarshal(message: WebServiceMessage, outputClass: KClass<out Any>): Any {
        try {
            return getUnmarshaller(outputClass).unmarshal(message.payloadSource, outputClass.java).value
        } catch (ex: UnmarshalException) {
            throw DecodingException("Could not unmarshal XML to $outputClass", ex)
        } catch (ex: JAXBException) {
            throw CodecException("Invalid JAXB configuration", ex)
        }
    }

    private fun getUnmarshaller(outputClass: KClass<out Any>) =
        unmarshallerProcessor.apply(this.jaxbContext.createUnmarshaller(outputClass))
}