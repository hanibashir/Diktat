package me.hani.diktat.ui.category

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hani.diktat.R
import me.hani.diktat.database.CategoryWithWords
import me.hani.diktat.database.DiktatDatabase
import me.hani.diktat.database.Word
import me.hani.diktat.databinding.FragmentCatListBinding
import me.hani.diktat.repository.DiktatRepository


class CatListFragment : Fragment(), OnCatItemClickListener {

    private lateinit var binding: FragmentCatListBinding
    private lateinit var catViewModel: CatViewModel
    private lateinit var adapter: CatAdapter
    private var catListWithWords = listOf<CategoryWithWords>()
    private var databaseCorrectWordsList = listOf<Word>()
    private var databaseWrongWordsList = listOf<Word>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cat_list, container, false)
        //to show the menu items
        setHasOptionsMenu(true)
        // get a reference to the application context
        val application = requireNotNull(this.activity).application
        //get DAO of database reference
        val dataSource = DiktatDatabase.getDatabaseInstance(application).diktatDao()
        //get instance of the repository
        val repository = DiktatRepository(dataSource)
        //get instance of the viewModelFactory
        val viewModelFactory = CatViewModelFactory(repository)
        //get instance of the viewModelFactory
        catViewModel = ViewModelProvider(this, viewModelFactory).get(CatViewModel::class.java)

        adapter = CatAdapter(context, this)
        binding.rvDiktatCat.adapter = adapter

        //send category list to recycler adapter
        catViewModel.getCatsListWithWordsList.observe(viewLifecycleOwner, {
            adapter.catsListWithWords = it
            this.catListWithWords = it
        })

        // get all correct answered words
        catViewModel.getWordsListWithIsCorrect(1).observe(viewLifecycleOwner, {
            databaseCorrectWordsList = it
            adapter.allCorrectList = it
        })

        // get all wrong answered words
        catViewModel.getWordsListWithIsCorrect(0).observe(viewLifecycleOwner, {
            databaseWrongWordsList = it
            adapter.allWrongList = it
        })

        return binding.root
    }

    override fun onItemButtonClicked(position: Int, btn: View) {

        val catName = catListWithWords[position].cat.name
        val catId = catListWithWords[position].cat.id
        val catWordsList = catListWithWords[position].catWordsList
        val correctWordsList = mutableListOf<Word>()
        val wrongWordsList = mutableListOf<Word>()

        when (btn.id) {
            //on btn show all words clicked
            R.id.btn_cat_all_words -> {
                if (catWordsList.isNotEmpty()) { //if the current category has diktat list
                    //then navigate to the test fragment and pass to it as arguments
                    findNavController().navigate(
                        CatListFragmentDirections.actionCatListFragmentToWordsListFragment(
                            catName,
                            catId
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.no_words),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            //correct answered words btn
            R.id.btn_cat_correct -> {

                // trying to loop over the list to get only current category
                // words not all correct words in database
                for (word in databaseCorrectWordsList) {
                    if (word.cat_id == catId) {
                        correctWordsList.add(word)
                    }
                }

                //if the correct words list not empty
                if (correctWordsList.isNotEmpty()) {
                    //then navigate to the words list fragment and pass this list to it as arguments
                    findNavController().navigate(
                        CatListFragmentDirections.actionCatListFragmentToWordsListFragment(
                            catName,
                            catId,
                            correctWordsList.toTypedArray()
                        )
                    )

                } else {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.no_words),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            //wrong answered words btn
            R.id.btn_cat_wrong -> {
                // trying to loop over the list to get only current category
                // words not all wrong words in database
                for (word in databaseWrongWordsList) {
                    if (word.cat_id == catId) {
                        wrongWordsList.add(word)
                    }
                }

                //if the correct words list not empty
                if (wrongWordsList.isNotEmpty()) {
                    //then navigate to the words list fragment and pass this list to it as arguments
                    findNavController().navigate(
                        CatListFragmentDirections.actionCatListFragmentToWordsListFragment(
                            catName,
                            catId,
                            wrongWordsList.toTypedArray()
                        )
                    )

                } else {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.no_words),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            //on button add words clicked navigate to add new
            // fragment and attach cat name and id as arguments
            R.id.btn_cat_add_word -> {

                findNavController()
                    .navigate(
                        CatListFragmentDirections.actionCatListFragmentToAddWordFragment(
                            catName,
                            catId
                        )
                    )
            }
            //on btn edit clicked
            R.id.btn_cat_edit -> {
                findNavController().navigate(
                    CatListFragmentDirections.actionCatListFragmentToAddCatFragment(catId, catName)
                )
            }
            //delete category button
            R.id.btn_cat_delete -> {

                val builder = AlertDialog.Builder(context)

                builder.setMessage(resources.getString(R.string.alert_msg_sure_delete))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.alert_positive_btn)) { _, _ ->
                        // delete category
                        catViewModel.deleteCat(catListWithWords[position].cat)
                        //also delete the category words
                        catViewModel.deleteWordWithCat(catId)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton(resources.getString(R.string.alert_negative_btn)) { dialog, _ ->

                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_cat -> findNavController().navigate(R.id.action_catListFragment_to_addCatFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        adapter.allCorrectList = databaseCorrectWordsList
        adapter.allWrongList = databaseWrongWordsList
    }
}