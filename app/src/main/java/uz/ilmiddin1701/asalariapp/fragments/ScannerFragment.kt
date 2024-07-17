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
            realtimeReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list = ArrayList()
                    val children = snapshot.children
                    for (child in children) {
                        val product = child.getValue(Product::class.java)
                        if (product!!.id in MySharedPreferences.sharedList) {
                            list.add(product)
                        }
                    }
                    scannerAdapter = ScannerAdapter(requireContext(), list)
                    rv.adapter = scannerAdapter

                    MyData.umumiyNarx.observe(viewLifecycleOwner) {
                        totalPrice.text = "Jami narx: $it so'm"
                    }

                    MyData.isScanner.observe(viewLifecycleOwner) {
                        if (it && MySharedPreferences.sharedList.isNotEmpty()) {
                            rv.visibility = View.VISIBLE
                            card.visibility = View.VISIBLE
                            empty.visibility = View.GONE
                            realtimeReference.addValueEventListener(object : ValueEventListener {
                                @SuppressLint("SetTextI18n")
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    list = ArrayList()
                                    val children1 = snapshot.children
                                    for (child in children1) {
                                        val product = child.getValue(Product::class.java)
                                        if (product!!.id in MySharedPreferences.sharedList) {
                                            list.add(product)
                                        }
                                    }
                                    scannerAdapter = ScannerAdapter(requireContext(), list)
                                    rv.adapter = scannerAdapter

                                    MyData.umumiyNarx.observe(viewLifecycleOwner) {
                                        totalPrice.text = "Jami narx: $it so'm"
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })

            btnCancel.setOnClickListener {
                val sharedList = MySharedPreferences.sharedList
                sharedList.clear()
                MySharedPreferences.sharedList = sharedList
                MyData.umumiyNarx.postValue(0)
                MyData.isScanner.postValue(false)
                findNavController().popBackStack()
            }

            btnNew.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }
}