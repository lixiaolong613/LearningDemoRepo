package com.example.madslearning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madslearning.databinding.FruitItemBinding

/**
 * @Author lilong
 * @Date 2023/1/3
 * @Description
 */
class FruitRecycleAdapter: RecyclerView.Adapter<FruitRecycleAdapter.FruitHolder>() {

    override fun getItemCount(): Int {
        return 20
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitHolder {
        val binding = FruitItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FruitHolder(binding)
    }

    override fun onBindViewHolder(holder: FruitHolder, position: Int) {
        holder.bind(position)
    }


    inner class FruitHolder(val binding: FruitItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvName.text = "fruit_$position"
        }
    }
}