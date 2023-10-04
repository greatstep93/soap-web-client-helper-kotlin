package ru.greatstep

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.TEXT_XML
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import ru.greatstep.model.SoapEnvelopeResponse
import ru.greatstep.util.Jaxb2SoapDecoder
import ru.greatstep.util.Jaxb2SoapEncoder
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@Component
class SoapWebClientHelper(val encoder: Jaxb2SoapEncoder, val decoder: Jaxb2SoapDecoder) {

    fun getSoapBuilder(): Builder {
        val connector = ReactorClientHttpConnector(
            HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected {
                    it.addHandlerFirst(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                }.wiretap(true)
        )
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { it.customCodecs().register(encoder) }
            .codecs { it.customCodecs().register(decoder) }
            .build()

        return WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .clientConnector(connector)
    }

    fun getSoapBuilder(timeout: Int): Builder {
        val connector = ReactorClientHttpConnector(
            HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected {
                    it.addHandlerFirst(ReadTimeoutHandler(timeout.toLong(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(WriteTimeoutHandler(timeout.toLong(), TimeUnit.MILLISECONDS))
                }.wiretap(true)
        )
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { it.customCodecs().register(encoder) }
            .codecs { it.customCodecs().register(decoder) }
            .build()

        return WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .clientConnector(connector)
    }

    fun <T> postSoapRequest(
        url: String,
        headersConsumer: Consumer<HttpHeaders>,
        request: Any,
        requestClass: Class<*>,
        responseClass: Class<T>,
        errorHandler: (ClientResponse) -> Mono<out Throwable>
    ): Mono<T> {
        return getSoapBuilder().build()
            .post()
            .uri(url)
            .headers(headersConsumer)
            .contentType(TEXT_XML)
            .body(Mono.just(request), requestClass)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler)
            .bodyToMono(responseClass)
    }

    fun <T> postSoapRequest(
        url: String,
        headersConsumer: Consumer<HttpHeaders>,
        request: Any,
        requestClass: Class<*>,
        responseClass: Class<T>,
        errorHandler: (ClientResponse) -> Mono<out Throwable>,
        timeoutMillis: Int
    ): Mono<T> {
        return getSoapBuilder(timeoutMillis).build()
            .post()
            .uri(url)
            .headers(headersConsumer)
            .contentType(TEXT_XML)
            .body(Mono.just(request), requestClass)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler)
            .bodyToMono(responseClass)
    }


    fun <T> postSoapRequest(
        url: String,
        headersConsumer: Consumer<HttpHeaders>,
        request: Any,
        requestClass: Class<*>,
        responseClass: ParameterizedTypeReference<SoapEnvelopeResponse<T>>,
        errorHandler: (ClientResponse) -> Mono<out Throwable>,
        timeoutMillis: Int
    ): Mono<SoapEnvelopeResponse<T>> {
        return getSoapBuilder(timeoutMillis).build()
            .post()
            .uri(url)
            .headers(headersConsumer)
            .contentType(TEXT_XML)
            .body(Mono.just(request), requestClass)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler)
            .bodyToMono(responseClass)
    }

    fun <T> postSoapRequestCustomContentType(
        url: String,
        headersConsumer: Consumer<HttpHeaders>,
        request: Any,
        requestClass: Class<*>,
        responseClass: Class<T>,
        errorHandler: (ClientResponse) -> Mono<out Throwable>,
        timeoutMillis: Int
    ): Mono<T> {
        return getSoapBuilder(timeoutMillis).build()
            .post()
            .uri(url)
            .headers(headersConsumer)
            .body(Mono.just(request), requestClass)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler)
            .bodyToMono(responseClass)
    }

    fun <T> postSoapRequestCustomContentType(
        url: String,
        headersConsumer: Consumer<HttpHeaders>,
        request: Any,
        requestClass: Class<*>,
        responseClass: ParameterizedTypeReference<SoapEnvelopeResponse<T>>,
        errorHandler: (ClientResponse) -> Mono<out Throwable>,
        timeoutMillis: Int
    ): Mono<SoapEnvelopeResponse<T>> {
        return getSoapBuilder(timeoutMillis).build()
            .post()
            .uri(url)
            .headers(headersConsumer)
            .body(Mono.just(request), requestClass)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler)
            .bodyToMono(responseClass)
    }

}