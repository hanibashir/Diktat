package me.hani.diktat.ui.word

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import me.hani.diktat.R
import me.hani.diktat.database.DiktatDatabase
import me.hani.diktat.database.Word
import me.hani.diktat.databinding.FragmentAddWordBinding
import me.hani.diktat.repository.DiktatRepository


class AddWordFragment : Fragment() {

    lateinit var binding: FragmentAddWordBinding
    private lateinit var args: AddWordFragmentArgs
    private lateinit var wordViewModel: WordViewModel
    private var argsWordObj: Word? = null
    private var catId = 0
    private var catName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_word, container, false)

        // get a reference to the application context
        val application = requireNotNull(this.activity).application
        //get DAO of database reference
        val dataSource = DiktatDatabase.getDatabaseInstance(application).diktatDao()
        //get instance of the repository
        val repository = DiktatRepository(dataSource)
        //get instance of the viewModelFactory
        val viewModelFactory = WordViewModelFactory(repository, application)
        //get instance of the viewModel
        wordViewModel = ViewModelProvider(this, viewModelFactory).get(WordViewModel::class.java)

        //get arguments
        args = AddWordFragmentArgs.fromBundle(requireArguments())
        catId = args.catId
        catName = args.catName
        argsWordObj = args.wordObj

        if (argsWordObj != null) {
            //if word not null then it is update case
            // fill edit text field with values
            binding.etAddWordArtikel.setText(argsWordObj?.artikel)
            binding.etAddWordWord.setText(argsWordObj?.word)
            binding.etAddWordPlural.setText(argsWordObj?.plural)
            // change btn text to update
            binding.btnAddWordSave.text = resources.getString(R.string.update)
        }

        binding.btnAddWordSave.setOnClickListener {

            //send data to database
            addEditWord()

            // hide the keyboard in fragment
            hideKeyboard(it)
        }

        return binding.root
    }


    private fun addEditWord() {
        val artikel = binding.etAddWordArtikel.text.toString()
        val word = binding.etAddWordWord.text.toString()
        val plural = binding.etAddWordPlural.text.toString()

        // create word object
        val wordObj: Word

        if (artikel.isNotBlank() || word.isNotBlank() || plural.isNotBlank()) {
            if (argsWordObj == null) { // add new word
                // create word object
                wordObj = Word(0, artikel, word, plural, catId)
                //pass the word object to database
                wordViewModel.addWord(wordObj)

                Toast.makeText(
                    context,
                    getString(R.string.success_adding_word),
                    Toast.LENGTH_SHORT
                ).show()

                //navigate to words list
                goToWordsListFragment()
            } else {
                wordObj = Word(argsWordObj!!.id, artikel, word, plural, catId)
                // update word
                wordViewModel.updateWord(wordObj)

                Toast.makeText(
                    context,
                    getString(R.string.success_updating_word),
                    Toast.LENGTH_SHORT
                ).show()

                //navigate to words list
                goToWordsListFragment()
            }

        } else {
            Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
        }

    }

    private fun goToWordsListFragment() {
        //navigate to category list of words fragment
        findNavController().navigate(
            AddWordFragmentDirections.actionAddWordFragmentToWordsListFragment(
                catName,
                catId
            )
        )
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}