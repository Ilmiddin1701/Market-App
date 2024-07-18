package uz.ilmiddin1701.asalariapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uz.ilmiddin1701.asalariapp.R
import uz.ilmiddin1701.asalariapp.adapters.ScannerAdapter
import uz.ilmiddin1701.asalariapp.databinding.FragmentScannerBinding
import uz.ilmiddin1701.asalariapp.models.Product
import uz.ilmiddin1701.asalariapp.utils.MyData
import uz.ilmiddin1701.asalariapp.utils.MySharedPreferences

class ScannerFragment : Fragment() {
    private val binding by lazy { FragmentScannerBinding.inflate(layoutInflater) }

    //realtime database
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var realtimeReference: DatabaseReference

    private lateinit var list: ArrayList<Product>
    private lateinit var scannerAdapter: ScannerAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseDatabase = FirebaseDatabase.getInstance()
        realtimeReference = firebaseDatabase.getReference("Products")

        list = ArrayList()

        binding.apply {
            MySharedPreferences.init(requireContext())
            val anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.combination_anim)
            val anim2 = AnimationUtils.loadAnimation(requireContext(), R.anim.combination_anim_2)
            if (MySharedPreferences.sharedList.isNotEmpty()) {
                rv.visibility = View.VISIBLE
                card.visibility = View.VISIBLE
                empty.visibility = View.GONE
            } else {
                emptyImage.startAnimation(anim1)
                emptyText.startAnimation(anim2)
                rv.visibility = View.GONE
                card.visibility = View.GONE
                empty.visibility = View.VISIBLE
            }

            scannerAdapter = ScannerAdapter(requireContext(), list)
            rv.adapter = scannerAdapter

            loadProducts()

            MyData.isScanner.observe(viewLifecycleOwner) {
                if (it) {
                    loadProducts()
                }
            }
            btnCancel.setOnClickListener {
                val sharedList = MySharedPreferences.sharedList
                sharedList.clear()
                MySharedPreferences.sharedList = sharedList
                MyData.umumiyNarx.postValue(0)
                MyData.isScanner.postValue(false)
                findNavController().popBackStack()
            }
            btnNew.setOnClickListener {
                updateProductCount()
                val sharedList = MySharedPreferences.sharedList
                sharedList.clear()
                MySharedPreferences.sharedList = sharedList
                MyData.umumiyNarx.postValue(0)
                MyData.isScanner.postValue(false)
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    private fun loadProducts() {
        binding.apply {
            realtimeReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "FragmentLiveDataObserve")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    val children = snapshot.children
                    for (child in children) {
                        val product = child.getValue(Product::class.java)
                        if (product?.id != null && product.id in MySharedPreferences.sharedList) {
                            list.add(product)
                        }
                    }
                    MyData.umumiyNarx.observe(this@ScannerFragment) {
                        totalPrice.text = "Jami narx: $it so'm"
                    }
                    if (list.isNotEmpty()) {
                        rv.visibility = View.VISIBLE
                        card.visibility = View.VISIBLE
                        empty.visibility = View.GONE
                    }
                    scannerAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateProductCount() {
        for (product in list) {
            product.soni?.let { count ->
                realtimeReference.child(product.id!!).child("soni")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentCount = snapshot.getValue(Long::class.java) ?: 0
                            val newCount = currentCount - count
                            realtimeReference.child(product.id!!).child("soni").setValue(newCount)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}