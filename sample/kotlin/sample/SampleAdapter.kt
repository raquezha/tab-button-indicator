package sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_sample.view.*

class SampleAdapter(
    private val context: Context,
    private val items: MutableList<String>)
    : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SampleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sample, parent, false))

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.itemView.tvSample.text = getItem(position)
    }

    private fun getItem(position: Int): String {
        return items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class SampleViewHolder(view: View) : RecyclerView.ViewHolder(view)

}