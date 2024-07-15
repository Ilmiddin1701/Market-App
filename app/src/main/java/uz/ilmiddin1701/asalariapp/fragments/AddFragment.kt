package uz.ilmiddin1701.asalariapp.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import uz.ilmiddin1701.asalariapp.databinding.FragmentAddBinding
import uz.ilmiddin1701.asalariapp.models.Product
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddFragment : Fragment() {
    private val binding by lazy { FragmentAddBinding.inflate(layoutInflater) }

    private var imgURL = ""
    private lateinit var btm: Bitmap

    //cloud storage
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    //realtime database
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var realtimeReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseDatabase = FirebaseDatabase.getInstance()
        realtimeReference = firebaseDatabase.getReference("Products")

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReference("QrCodes")

        binding.apply {
            tvDay.setOnClickListener { showCalendar() }
            btnAdd.setOnClickListener {
                if (edtName.text.isNotBlank() && edtPrice.text.isNotBlank() && tvDay.text.isNotBlank()) {
                    progressBar.visibility = View.VISIBLE
                    btnAdd.isEnabled = false
                    val id = UUID.randomUUID().toString()
                    generateQRCode(id)
                    qrImage.setImageBitmap(btm)
                    card.visibility = View.VISIBLE
                    val imageUri = getImageUri(requireContext(), btm)
                    val task = storageReference.child(id).putFile(imageUri)
                    task.addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            val downloadURL = it.metadata?.reference?.downloadUrl
                            downloadURL?.addOnSuccessListener { imageURL ->
                                imgURL = imageURL.toString()
                                val product1 = Product(id, edtName.text.toString(), edtPrice.text.toString().toLong(), edtSoni.text.toString().toLong(), tvDay.text.toString(), imgURL)
                                val key = realtimeReference.push().key!!
                                realtimeReference.child(key).setValue(product1)
                            }
                            progressBar.visibility = View.GONE
                            btnAdd.isEnabled = true
                            findNavController().popBackStack()
                        }
                    }
                    task.addOnFailureListener {
                        Toast.makeText(context, "Xatolik ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Ma'lumotlar to'liq kiritilmagan", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun generateQRCode(text: String): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            btm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    btm.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            btm
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showCalendar() {
        binding.apply {
            val datePicker = DatePickerDialog(requireContext())
            datePicker.setOnCancelListener {
                tvDay.text = ""
            }
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                tvDay.text = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
            }
            datePicker.show()
        }
    }
}