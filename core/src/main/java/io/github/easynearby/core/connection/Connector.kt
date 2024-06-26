package io.github.easynearby.core.connection

interface Connector {
    /**
     * Establishes a connection with a remote endpoint.
     * @param endpoint remote endpoint
     * @param localDeviceName name of the local device that tries to connect
     * @param remoteDeviceName name of the remote device
     * @param isIncomingConnection true if connection is incoming, false if the initiator is the current device
     * @param authValidator that is used to determine whether to accept or reject the connection
     */
    suspend fun connect(
        endpoint: String,
        localDeviceName: String,
        remoteDeviceName: String,
        isIncomingConnection: Boolean,
        authValidator: suspend (String) -> Boolean
    ): Result<Connection>

    /**
     * Disconnects from a remote endpoint
     */
    suspend fun disconnect(endpoint: String)

    suspend fun rejectConnection(endpoint: String)

    /**
     * Sends a [payload] to a remote endpoint
     */
    suspend fun sendPayload(endpoint: String, payload: ByteArray): Result<Unit>
}