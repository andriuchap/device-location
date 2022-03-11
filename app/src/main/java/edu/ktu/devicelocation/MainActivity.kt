package edu.ktu.devicelocation

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Permission request code to differentiate from other requests.
    private val LOCATION_PERMISSION_REQUEST_CODE = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initiates the application's code.
        attemptDiscoverLocation();
    }

    private fun attemptDiscoverLocation() {
        when {
            // Check if permission is granted.
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Continue with using the device's location.
                discoverLocation()
            }
            // Permission is not granted, should we show rationale?
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                // Show rationale
                showRationale();
            }
            else -> {
                // Permission not granted, make a request.
                makeLocationPermissionRequest()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if this is the request code we're expecting.
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If the result array is empty, that means the user canceled the request.
            // If it's not empty, we have to check if our request was granted.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If the request was granted, we continue with our execution.
                discoverLocation();
            } else {
                // If the request was denied or canceled, we explain why our application won't work.
                showDenyMessage();
            }
        }
    }

    private fun discoverLocation() {
        // Get the location manager service.
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Retrieve the location, longitude and latitude.
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val long = location?.longitude
        val lat = location?.latitude
        // Show longitude and latitude on the screen.
        findViewById<TextView>(R.id.message_text).text =
            String.format(resources.getString(R.string.location), long, lat)
    }

    private fun makeLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showRationale() {
        AlertDialog.Builder(this).setMessage(R.string.location_rationale)
            // The positive button dismisses the dialog.
            .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
                // Once the dialog is dismissed a request for the permission is called.
            }.setOnDismissListener {
                makeLocationPermissionRequest()
            }.show()
    }

    private fun showDenyMessage() {
        AlertDialog.Builder(this).setMessage(R.string.location_denied_message)
            .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.show()
        // Set the text of the message informing the user
        // that the device location can't be determined.
        findViewById<TextView>(R.id.message_text).text = resources.getString(R.string.no_location)
    }

}