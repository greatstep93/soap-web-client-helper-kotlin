package ru.greatstep.helper.util

import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class DateUtil {
    companion object {
        const val ISO_DATE_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ssXXX"

        fun toGregorianXmlDate(dateTime: OffsetDateTime?): XMLGregorianCalendar =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(
                GregorianCalendar.from(dateTime?.toZonedDateTime() ?: ZonedDateTime.now())
            )

        fun toOffsetDateTime(gregorianCalendar: XMLGregorianCalendar?): OffsetDateTime? {
            return gregorianCalendar?.toGregorianCalendar()
                ?.toZonedDateTime()
                ?.toOffsetDateTime()
        }

        fun dateToString(gregorianCalendar: XMLGregorianCalendar?): String {
            return gregorianCalendar?.toGregorianCalendar()
                ?.toZonedDateTime()
                ?.toOffsetDateTime()
                ?.format(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT))
                ?: ""
        }
    }
}