package uz.ilmiddin1701.asalariapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseDatabase = FirebaseDatabase.getInstance()
        realtimeReference = firebaseDatabase.getReference("Products")

        binding.apply {
            btnNew.setOnClickListener {
                Toast.makeText(context, "${MyData.umumiyNarx}", Toast.LENGTH_SHORT).show()
            }
            MySharedPreferences.init(requireContext())
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
                    MyData.umumiyNarx.observe(viewLifecycleOwner) {
                        totalPrice.text = "Jami narx: $it so'm"
                    }
                    scannerAdapter = ScannerAdapter(requireContext(), list)
                    rv.adapter = scannerAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
        return binding.root
    }
}