package ru.greatstep.helper.exception

/**
 * Ошибка парсинга SOAP запроса или ответа
 */
class SoapParseException(cause: Throwable, message: String? = cause.message) : RuntimeException(message, cause)