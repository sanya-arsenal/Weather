package com.example.weather.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface LocationProvider {
    suspend fun getUserLocation():GetUserLocationResult
}

sealed class GetUserLocationResult{
    data class Success(val latitude: String, val longitude: String): GetUserLocationResult()
    sealed class Failed: GetUserLocationResult(){
        object LocationManagerNotAvailable: Failed()
        object LocationProviderIsDisabled: Failed()
        object OtherFailure: Failed()
    }
}

class FusedLocationProvider(private val context: Context): LocationProvider{

    private var cancellationTokenSource = CancellationTokenSource()

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override suspend fun getUserLocation(): GetUserLocationResult {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            ?: return GetUserLocationResult.Failed.LocationManagerNotAvailable

        if (!isLocationEnabled(locationManager)){
            return GetUserLocationResult.Failed.LocationProviderIsDisabled
        }
        return getCurrentLocation()
    }

    private suspend fun getCurrentLocation():GetUserLocationResult{
        return suspendCancellableCoroutine { continuation ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
                val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)

                currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                    if (task.isSuccessful && task.result != null) {
                        val result: Location = task.result
                        return@addOnCompleteListener continuation.resume(
                            GetUserLocationResult.Success(
                                result.latitude.toString(),
                                result.longitude.toString()
                            )
                        )
                    } else {
                        return@addOnCompleteListener continuation.resume(
                            GetUserLocationResult.Failed.OtherFailure
                        )
                    }
                }.addOnFailureListener {
                    return@addOnFailureListener continuation.resume(
                        GetUserLocationResult.Failed.OtherFailure
                    )
                }
            }
        }
    }

    private fun isLocationEnabled(lm: LocationManager): Boolean{
        var gpsEnabled = false
        var networkEnabled = false

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e:Exception){}

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e:Exception){}
        return gpsEnabled || networkEnabled
    }
}