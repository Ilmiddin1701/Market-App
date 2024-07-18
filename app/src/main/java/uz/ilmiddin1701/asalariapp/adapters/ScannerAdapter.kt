package uz.ilmiddin1701.asalariapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import uz.ilmiddin1701.asalariapp.databinding.ItemRvBinding
import uz.ilmiddin1701.asalariapp.models.Product
import uz.ilmiddin1701.asalariapp.utils.MyData
import uz.ilmiddin1701.asalariapp.utils.MySharedPreferences

class ScannerAdapter(var context: Context, var list: ArrayList<Product>) : Adapter<ScannerAdapter.Vh>() {

    var umumiyNarx: Long =  0

    inner class Vh(var itemRvBinding: ItemRvBinding) : ViewHolder(itemRvBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(product: Product) {
            itemRvBinding.apply {
                Picasso.get().load(product.qrImgURL).into(qrImage)
                productName.text = "Nomi: ${product.name}"
                productPrice.text = "Narxi: ${product.price} so'm"
                productDate.text = product.date
                MySharedPreferences.init(context)
                var count: Long = 0
                for (i in MySharedPreferences.sharedList) {
                    if (product.id == i) {
                        count++
                    }
                    productSoni.text = "Soni: $count ta"
                }
                product.soni = count
                umumiyNarx += product.price!! * count
                MyData.umumiyNarx.postValue(umumiyNarx)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }
}