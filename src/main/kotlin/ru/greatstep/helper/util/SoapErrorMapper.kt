package ru.greatstep.helper.util

import jakarta.xml.soap.MessageFactory
import jakarta.xml.soap.SOAPException
import org.springframework.stereotype.Component
import ru.greatstep.helper.exception.SoapParseException
import java.io.IOException

@Component
class SoapErrorMapper(val messageFactory: MessageFactory) {

    fun toFaultString(stringXml: String, errorMessage: String): String {
        return try {
            messageFactory
                .createMessage(null, stringXml.byteInputStream())
                .soapBody
                .fault
                .faultString
        } catch (ex: Exception) {
            when (ex) {
                is IOException, is SOAPException -> throw SoapParseException(ex, errorMessage)
                else -> throw ex
            }
        }
    }
}