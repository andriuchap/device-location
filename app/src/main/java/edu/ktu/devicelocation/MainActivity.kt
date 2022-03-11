package edu.ktu.devicelocation

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // The ActivityResultLauncher to handle our permission result.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted ->
            if (isGranted) {
                // Permission granted, execute the location code.
                discoverLocation()
            } else {
                // Permission denied, explain why the app won't work
                showDenyMessage()
            }
        }

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
                // Show rationale.
                showRationale();
            }
            else -> {
                // Permission not granted, make a request.
                makeLocationPermissionRequest()
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
        // Launch the contract
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
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