package uz.ilmiddin1701.asalariapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import uz.ilmiddin1701.asalariapp.databinding.ItemRvBinding
import uz.ilmiddin1701.asalariapp.models.Product

class ProductsAdapter(var rvAction: RvAction, var list: ArrayList<Product>) : Adapter<ProductsAdapter.Vh>() {

    inner class Vh(var itemRvBinding: ItemRvBinding) : ViewHolder(itemRvBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(product: Product, position: Int) {
            itemRvBinding.apply {
                Picasso.get().load(product.qrImgURL).into(qrImage)
                productName.text = "Nomi: ${product.name}"
                productPrice.text = "Narxi: ${product.price} so'm"
                productDate.text = product.date
                if (list.isNotEmpty() && position == list.size - 1) {
                    view.visibility = View.VISIBLE
                }
                root.setOnClickListener {
                    rvAction.itemClick(product, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    interface RvAction {
        fun itemClick(product: Product, position: Int)
    }
}