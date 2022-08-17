package me.hani.diktat.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.hani.diktat.R
import me.hani.diktat.database.CategoryWithWords
import me.hani.diktat.database.Word
import kotlin.apply as also

class CatAdapter(private val context: Context?, private val listener: OnCatItemClickListener) :
    RecyclerView.Adapter<CatAdapter.CatViewHolder>() {

    //list of category with its words
    var catsListWithWords = listOf<CategoryWithWords>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var allCorrectList = listOf<Word>()
    var allWrongList = listOf<Word>()

    //on create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CatViewHolder.from(parent)


    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val item = catsListWithWords[position]
        //call CatViewHolder extension function
        holder.bind(item, listener, allCorrectList, allWrongList)
    }

    //view holder
    class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvCatName: TextView = itemView.findViewById(R.id.tv_cat_name)

        private val btnCatAllWords: Button = itemView.findViewById(R.id.btn_cat_all_words)
        private val btnCatCorrectWords: Button = itemView.findViewById(R.id.btn_cat_correct)
        private val btnCatWrongWords: Button = itemView.findViewById(R.id.btn_cat_wrong)

        private val btnCatAddWords: Button = itemView.findViewById(R.id.btn_cat_add_word)
        private val btnEditCat: Button = itemView.findViewById(R.id.btn_cat_edit)
        private val btnDeleteCat: Button = itemView.findViewById(R.id.btn_cat_delete)


        fun bind(
            item: CategoryWithWords,
            listener: OnCatItemClickListener,
            allCorrectList: List<Word>,
            allWrongList: List<Word>
        ) {
            //words in the current category
            val catWordsList = item.catWordsList
            //set cat name text view on the cat recycler view
            tvCatName.text = item.cat.name
            //variables for correct and wrong answered words
            var correctNum = 0
            var wrongNum = 0

            //loop over correct list of words
            for (word in allCorrectList) {
                if (word.cat_id == item.cat.id) {
                    correctNum++
                }
            }
            //loop over wrong list of words
            for (word in allWrongList) {
                if (word.cat_id == item.cat.id) {
                    wrongNum++
                }
            }
            // set all words button text on the cat recycler view
            ((correctNum + wrongNum).toString() + "-" + catWordsList.size).also {
                btnCatAllWords.text = this
            }
            // set values to buttons
            btnCatCorrectWords.text = correctNum.toString()
            btnCatWrongWords.text = wrongNum.toString()

            //on btn words count click listener
            btnCatAllWords.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }
            //on btn correct words click listener
            btnCatCorrectWords.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }
            //on btn correct words click listener
            btnCatWrongWords.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }
            //on btn add words click listener
            btnCatAddWords.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }

            //on btn edit cat click listener
            btnEditCat.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }
            //on btn delete cat click listener
            btnDeleteCat.setOnClickListener {
                listener.onItemButtonClicked(adapterPosition, it)
            }
        }

        companion object {
            fun from(parent: ViewGroup): CatViewHolder {
                val layout =
                    LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)

                return CatViewHolder(layout)
            }
        }
    }

    override fun getItemCount() = catsListWithWords.size
}


