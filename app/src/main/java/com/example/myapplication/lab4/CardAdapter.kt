package com.example.myapplication.lab4

import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemCardBinding

/**
 * 实验五：RecyclerView 适配器（Adapter）
 * - 继承 RecyclerView.Adapter，负责创建卡牌视图并绑定 Model  state；
 * - 通过 onCardClick 回调把点击事件交给 Controller（Activity）处理；
 * - cardSizePx 由 Activity 根据横竖屏列数计算，保证牌面整齐。
 */
class CardAdapter(
    private val game: CardMatchingGame,
    private val spanCount: Int,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 同时受列数和行数约束，保证横竖屏下所有牌都能完整显示
        val density = parent.resources.displayMetrics.density
        val usableWidth = parent.width.takeIf { it > 0 }
            ?: parent.resources.displayMetrics.widthPixels
        val usableHeight = parent.height.takeIf { it > 0 }
            ?: parent.resources.displayMetrics.heightPixels
        val rows = (game.cardCount + spanCount - 1) / spanCount
        val gap = (4 * density).toInt()
        val size = (minOf(usableWidth / spanCount, usableHeight / rows) - gap)
            .coerceAtLeast((48 * density).toInt())
        binding.root.layoutParams = RecyclerView.LayoutParams(size, size)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = game.cardAtIndex(position)
        val button = holder.binding.btnCard
        when {
            card.isMatched -> {
                button.text = card.toString()
                button.setBackgroundColor(
                    ContextCompat.getColor(button.context, R.color.card_matched)
                )
                button.isEnabled = false
            }
            card.isChosen -> {
                button.text = card.toString()
                button.setBackgroundColor(
                    ContextCompat.getColor(button.context, R.color.card_chosen)
                )
                button.isEnabled = true
            }
            else -> {
                button.text = ""
                button.setBackgroundResource(R.drawable.bg_card_back)
                button.isEnabled = true
            }
        }
        holder.binding.root.contentDescription = holder.binding.root.context.getString(
            R.string.game_card_position, position + 1
        )
        button.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onCardClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = game.cardCount

    /** 控制器修改模型后调用，刷新全部牌面 */
    fun refreshAll() = notifyDataSetChanged()
}
