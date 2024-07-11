package uz.ilmiddin1701.asalariapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.ilmiddin1701.asalariapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {

        }
    }
}