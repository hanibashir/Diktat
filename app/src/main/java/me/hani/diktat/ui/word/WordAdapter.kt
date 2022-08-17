package me.hani.diktat.ui.word

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.hani.diktat.R
import me.hani.diktat.database.Word
import java.util.*

class WordAdapter(private val listener: OnWordItemClickListener) :
    RecyclerView.Adapter<WordAdapter.DiktatViewHolder>() {

    var wordsList = arrayListOf<Word>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class DiktatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //text views
        private val artikel: TextView = itemView.findViewById(R.id.tv_words_artikel)
        private val word: TextView = itemView.findViewById(R.id.tv_words_word)
        private val plural: TextView = itemView.findViewById(R.id.tv_words_plural)

        //buttons
        private val speakBtn: Button = itemView.findViewById(R.id.btn_words_speak)
        private val editBtn: Button = itemView.findViewById(R.id.btn_words_edit)
        private val deleteBtn: Button = itemView.findViewById(R.id.btn_words_delete)

        fun bind(item: Word, listener: OnWordItemClickListener) {
            // set item views values
            artikel.text = item.artikel
            word.text = item.word
            plural.text = item.plural

            //on btn speak clicked
            speakBtn.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }

            // on delete btn clicked
            deleteBtn.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }
            // on delete btn clicked
            editBtn.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }

        }

        companion object {
            fun from(parent: ViewGroup): DiktatViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)

                return DiktatViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DiktatViewHolder.from(parent)

    override fun onBindViewHolder(holder: DiktatViewHolder, position: Int) {
        val item = wordsList[position]

        holder.bind(item, listener)

    }


    override fun getItemCount(): Int {
        return wordsList.size
    }
}

