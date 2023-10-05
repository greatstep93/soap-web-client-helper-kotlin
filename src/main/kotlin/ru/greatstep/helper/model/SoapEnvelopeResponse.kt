package ru.greatstep.helper.model

import jakarta.xml.bind.annotation.XmlRootElement
import org.w3c.dom.Document

@XmlRootElement
data class SoapEnvelopeResponse<T>(
    val body: T,
    val headers: Document
)
