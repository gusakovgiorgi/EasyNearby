package io.github.easynearby.core.discovery

import io.github.easynearby.core.EasyNearby
import io.github.easynearby.core.PermissionsChecker
import io.github.easynearby.core.advertising.DeviceInfo
import io.github.easynearby.core.connection.ConnectionCandidateEvent
import io.github.easynearby.core.exceptions.PermissionsNotGrantedException
import io.github.easynearby.core.loggging.extensions.logD
import io.github.easynearby.core.loggging.extensions.logW
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A thread safe Manager that can start and stop discovery.
 * If permissions not granted returns [PermissionsNotGrantedException] result.
 * If discovery already started returns [IllegalStateException]
 */
class DiscoveryManager(
    private val permissionsChecker: PermissionsChecker,
    private val discover: Discover
) {
    private val TAG =
        EasyNearby::class.java.simpleName + " - " + DiscoveryManager::class.java.simpleName

    private val isDiscovering = AtomicBoolean(false)

    /**
     * Starts discovery for remote endpoints with the specified [deviceInfo].
     * If discovery already started returns [IllegalStateException]
     * If permissions not granted returns [PermissionsNotGrantedException]
     * Otherwise returns [Flow] of [ConnectionCandidateEvent]
     */
    suspend fun startDiscovery(deviceInfo: DeviceInfo): Result<Flow<ConnectionCandidateEvent>> {
        if (isDiscovering.getAndSet(true)) {
            return Result.failure(IllegalStateException("Already Discovering"))
        }

        if (permissionsChecker.hasAllPermissions().not()) {
            return Result.failure(PermissionsNotGrantedException(permissionsChecker.getMissingPermissions()))
        }

        logD(TAG, "Preparing discovery for $deviceInfo")

        return discover.startDiscovery(deviceInfo).also {
            isDiscovering.set(it.isSuccess)
        }
    }

    /**
     * Stops discovery
     */
    fun stopDiscovery() {
        if (isDiscovering.getAndSet(false)) {
            logD(TAG, "Stopping discovery")
            discover.stopDiscovery()
        } else {
            logW(TAG, "Not Discovering")
        }
    }
}