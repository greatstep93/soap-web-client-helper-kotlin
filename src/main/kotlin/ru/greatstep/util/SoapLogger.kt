package ru.greatstep.util

import jakarta.xml.bind.JAXB
import org.crm.kibana.KibanaLogger
import java.io.StringWriter

class SoapLogger {
    companion object {
        fun <T> logXmlString(logger: KibanaLogger, textLog: String, soapLog: T?, xRequestId: String) {
            soapLog?.let {
                val sw = StringWriter()
                JAXB.marshal(it, sw)
                val log = sw.toString()
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                logger.info(textLog, log, xRequestId)
            }
        }
    }
}