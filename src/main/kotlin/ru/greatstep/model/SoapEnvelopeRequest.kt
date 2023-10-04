package ru.greatstep.model

import jakarta.xml.bind.annotation.XmlRootElement

/**
 * Класс обертка для сущностей передаваемых в SOAP - запросах.
 */
@XmlRootElement
data class SoapEnvelopeRequest(var body: Any)
