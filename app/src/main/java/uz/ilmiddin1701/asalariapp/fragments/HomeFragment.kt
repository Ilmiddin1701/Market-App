package uz.ilmiddin1701.asalariapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import uz.ilmiddin1701.asalariapp.utils.MyData
import uz.ilmiddin1701.asalariapp.R
import uz.ilmiddin1701.asalariapp.adapters.ProductsAdapter
import uz.ilmiddin1701.asalariapp.databinding.DialogItemBinding
import uz.ilmiddin1701.asalariapp.databinding.FragmentHomeBinding
import uz.ilmiddin1701.asalariapp.models.Product
import uz.ilmiddin1701.asalariapp.utils.MySharedPreferences
import uz.ilmiddin1701.asalariapp.utils.sdk26AdnUp
import java.io.IOException

class HomeFragment : Fragment(), ProductsAdapter.RvAction {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    //realtime database
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var realtimeReference: DatabaseReference

    private lateinit var list: ArrayList<Product>
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseDatabase = FirebaseDatabase.getInstance()
        realtimeReference = firebaseDatabase.getReference("Products")

        val anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.combination_anim)
        val anim2 = AnimationUtils.loadAnimation(requireContext(), R.anim.combination_anim_2)

        MySharedPreferences.init(requireContext())
        list = ArrayList()

        binding.apply {
            productsAdapter = ProductsAdapter(this@HomeFragment, list)
            rv.adapter = productsAdapter

            realtimeReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    val children = snapshot.children
                    for (child in children) {
                        val product = child.getValue(Product::class.java)
                        list.add(product!!)
                    }
                    if (list.isNotEmpty()) {
                        empty.visibility = View.GONE
                        rv.visibility = View.VISIBLE
                    } else {
                        emptyImage.startAnimation(anim1)
                        emptyText.startAnimation(anim2)
                        rv.visibility = View.GONE
                        empty.visibility = View.VISIBLE

                        val sharedList = MySharedPreferences.sharedList
                        sharedList.clear()
                        MySharedPreferences.sharedList = sharedList
                    }
                    productsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
        return binding.root
    }

    private fun saveToExternalStorage(displayName: String, bmp: Bitmap): Boolean {
        val imageCollection = sdk26AdnUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image.jpeg")
        }
        return try {
            requireActivity().contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                requireActivity().contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream!!)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun itemClick(product: Product, position: Int) {
        val dialog = AlertDialog.Builder(context, R.style.NewDialog).create()
        val dialogView = DialogItemBinding.inflate(layoutInflater)
        dialogView.apply {
            tvName.text = "Nomi: ${product.name}"
            tvPrice.text = "Narxi: ${product.price} so'm"
            tvCount.text = "Soni: ${product.soni}"
            tvDate.text = "Olib kelingan sana: ${product.date}"
            Picasso.get().load(product.qrImgURL).into(qrImage)
            btnDownload.setOnClickListener {
                if (MyData.writePermissionGranted) {
                    saveToExternalStorage(product.name!!, qrImage.drawToBitmap())
                    Toast.makeText(context, "QR-kod yuklandi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Yuklash uchun ruxsat berilmagan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            btnCancel.setOnClickListener { dialog.cancel() }
        }
        dialog.setView(dialogView.root)
        dialog.show()
    }

    override fun itemLongClick(product: Product, position: Int, view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.popup_menu)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_delete -> {
                    realtimeReference.child(product.id!!).removeValue()
                    val sharedList = MySharedPreferences.sharedList
                    sharedList.remove(product.id)
                    MySharedPreferences.sharedList = sharedList
                    Toast.makeText(context, "Mahsulot o'chirildi", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        menu.show()
    }
}