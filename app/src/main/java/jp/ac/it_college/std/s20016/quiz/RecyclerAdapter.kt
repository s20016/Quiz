package jp.ac.it_college.std.s20016.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val questionChoices: List<String>, private val questionAnswer: Int)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemChoice.text = questionChoices[position]
    }

    override fun getItemCount(): Int {
        return questionChoices.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var itemChoice: TextView = itemView.findViewById(R.id.tvChoice)

        init {
            itemChoice.setOnClickListener {
                val itemPosition: Int = adapterPosition
                Toast.makeText(itemView.context, "You clicked $itemPosition", Toast.LENGTH_LONG).show()
            }
        }
    }

}