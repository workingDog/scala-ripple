package com.kodekutters.ripple.core

import java.net.URI
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshake


/**
 * the websocket client connecting to the ripple server.
 * Basically a wrapper around the java WebSocketClient
 *
 * @param serverUri the server URI address
 * @param protocolDraft
 * @param httpHeaders
 * @param connectTimeout
 */
class WSClient(serverUri: URI,
               protocolDraft: Draft,
               httpHeaders: Map[String, String],
               connectTimeout: Int)
//  extends WebSocketClient(serverUri, protocolDraft, httpHeaders, connectTimeout) {
  extends WebSocketClient(serverUri, protocolDraft) {

  override def onMessage(jsonMessage: String) = {}
  override def onOpen(handshakeData: ServerHandshake) = {}
  override def onClose(code: Int, reason: String, remote: Boolean) = {}
  override def onError(ex: Exception) = {}
}
