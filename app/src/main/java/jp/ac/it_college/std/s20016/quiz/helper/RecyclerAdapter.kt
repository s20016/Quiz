package jp.ac.it_college.std.s20016.quiz.helper

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import jp.ac.it_college.std.s20016.quiz.R

class RecyclerAdapter(private val questionChoices: List<String>, private val questionAnswer: Int)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var checkedLimit = 0
    val userChoice = mutableListOf<Int>()

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(userChoice: MutableList<Int>)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemChoice.text = questionChoices[position]
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemCount(): Int {
        return questionChoices.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        var itemChoice: CheckBox = itemView.findViewById(R.id.tvChoice)

        init {
            itemChoice.setOnCheckedChangeListener { _, isChecked ->
                val itemSelected = adapterPosition
                if (isChecked && checkedLimit >= questionAnswer) {
                    itemChoice.isChecked = false
                } else {
                    if (isChecked) {
                        checkedLimit++
                        itemView.setBackgroundColor(Color.parseColor("#0E3858"))
                        if (itemSelected !in userChoice) userChoice.add(itemSelected)
                        listener.onItemClick(userChoice)

                    } else {
                        checkedLimit--
                        itemView.setBackgroundColor(Color.parseColor("#2C394B"))
                        if (itemSelected in userChoice) userChoice.remove(itemSelected)
                        listener.onItemClick(userChoice)
                    }
                }
            }
        }
    }
}
