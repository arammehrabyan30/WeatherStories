package com.aram.mehrabyan.weatherstories.data.repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Implementation of LocationRepository using Google Play Services
 */
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    override suspend fun getCurrentLocation(): Result<Location> {
        if (!hasLocationPermission()) {
            return Result.failure(
                SecurityException("Location permission not granted")
            )
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                val cancellationTokenSource = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(Result.success(location))
                    } else {
                        continuation.resume(
                            Result.failure(Exception("Location is null"))
                        )
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            } catch (e: SecurityException) {
                continuation.resume(Result.failure(e))
            }
        }
    }

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}