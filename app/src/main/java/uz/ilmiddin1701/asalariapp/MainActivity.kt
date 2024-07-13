package uz.ilmiddin1701.asalariapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import uz.ilmiddin1701.asalariapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyData.permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { check ->
            MyData.writePermissionGranted = check[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: MyData.writePermissionGranted
        }
        updateOrRequestPermission()

        binding.fabQr.setColorFilter(Color.WHITE)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_navigation_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.fabQr.setOnClickListener {
            checkPermissionCamera(this)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera()
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(this, "Skannerlash bekor qilindi", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)

                }
            }
        }

    private fun setResult(string: String) {
        binding.apply {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse(tvResult.text.toString())
//            startActivity(intent)
//
//            tvResult.setOnClickListener {
//                intent.data = Uri.parse(tvResult.text.toString())
//                startActivity(intent)
//            }
            Toast.makeText(this@MainActivity, "$string", Toast.LENGTH_SHORT).show()

        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "CAMERA permission required", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun updateOrRequestPermission(){
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        MyData.writePermissionGranted = hasWritePermission || minSdk29

        val permissionToRequest = mutableListOf<String>()
        if (!MyData.writePermissionGranted){
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()){
            MyData.permissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }
}