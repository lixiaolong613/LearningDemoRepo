package com.example.madslearning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.madslearning.databinding.FlipperTextItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author lilong
 * @Date 2023/2/9
 * @Description
 */
class FlipperAdapter: RecyclerView.Adapter<FlipperAdapter.Holer>() {

    private val data = mutableListOf<String>()


    fun updateData(source: List<String>) {
        data.clear()
        data.addAll(source)
        if (source.size > 1) {
            data.add(source[0])
        }
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holer {
        return Holer(FlipperTextItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holer, position: Int) {
        holder.binding.tvContent.text = data[position]
        holder.binding.root.setBackgroundResource(if(position % 2 == 0)  R.color.purple_200 else R.color.teal_700)
    }

    inner class Holer(val binding: FlipperTextItemBinding): RecyclerView.ViewHolder(binding.root)
}