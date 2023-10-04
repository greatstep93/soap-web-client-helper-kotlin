package ru.greatstep.util

import org.w3c.dom.Document

class SoapHeadersUtil {

    companion object {

        fun getHeaderValueByName(soapDocument: Document?, headerName: String?) =
            soapDocument?.getElementsByTagName(headerName)?.item(0)?.textContent

        fun getHeadersMap(soapDocument: Document?, vararg headersNames: String) =
            headersNames.associateWith { getHeaderValueByName(soapDocument, it) }
    }
}