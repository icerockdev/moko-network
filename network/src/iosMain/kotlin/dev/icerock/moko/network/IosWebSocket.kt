/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.ktor.client.features.websocket.WebSocketException
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.ExperimentalWebSocketExtensionApi
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketExtension
import io.ktor.http.cio.websocket.readText
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionWebSocketCloseCode
import platform.Foundation.NSURLSessionWebSocketDelegateProtocol
import platform.Foundation.NSURLSessionWebSocketMessage
import platform.Foundation.NSURLSessionWebSocketTask
import platform.darwin.NSObject
import kotlin.coroutines.CoroutineContext

internal class IosWebSocket(
    socketEndpoint: NSURL,
    override val coroutineContext: CoroutineContext
) : DefaultWebSocketSession {
    internal val originResponse: CompletableDeferred<String?> = CompletableDeferred()

    private val webSocket: NSURLSessionWebSocketTask

    private val _incoming = Channel<Frame>()
    private val _outgoing = Channel<Frame>()
    private val _closeReason = CompletableDeferred<CloseReason?>()

    override val incoming: ReceiveChannel<Frame> = _incoming
    override val outgoing: SendChannel<Frame> = _outgoing
    override val closeReason: Deferred<CloseReason?> = _closeReason

    @ExperimentalWebSocketExtensionApi
    override val extensions: List<WebSocketExtension<*>>
        get() = emptyList()

    override var maxFrameSize: Long
        get() = throw WebSocketException("websocket doesn't support max frame size.")
        set(_) = throw WebSocketException("websocket doesn't support max frame size.")

    override suspend fun flush() = Unit

    @OptIn(ExperimentalWebSocketExtensionApi::class)
    @InternalAPI
    override fun start(negotiatedExtensions: List<WebSocketExtension<*>>) {
        require(negotiatedExtensions.isEmpty()) { "Extensions are not supported." }
    }

    init {
        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    originResponse.complete(didOpenWithProtocol)
                }

                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    val closeReason = CloseReason(
                        code = CloseReason.Codes.PROTOCOL_ERROR,
                        message = "$didCloseWithCode : ${reason.toString()}"
                    )
                    _closeReason.complete(closeReason)
                }
            },
            delegateQueue = NSOperationQueue.currentQueue()
        )
        println("urlSession was built: $urlSession")
        webSocket = urlSession.webSocketTaskWithURL(socketEndpoint)

        CoroutineScope(coroutineContext).launch {
            _outgoing.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = NSURLSessionWebSocketMessage(frame.readText())
                    webSocket.sendMessage(message) { nsError ->
                        if (nsError != null) throw Exception(nsError.description)
                    }
                }

            }
        }

        listenMessages()
    }

    fun start() {
        println("urlSession will resume")
        webSocket.resume()
        println("urlSession did resume")
    }

    private fun listenMessages() {
        webSocket.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    throw Exception(nsError.description)
                }
                message != null -> {
                    message.string?.let { _incoming.trySend(Frame.Text(it)) }
                }
            }
            listenMessages()
        }
    }

    override fun terminate() {
        coroutineContext.cancel()
    }
}