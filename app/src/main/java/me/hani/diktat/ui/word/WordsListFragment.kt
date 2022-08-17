package me.hani.diktat.ui.word

import android.app.AlertDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import me.hani.diktat.R
import me.hani.diktat.database.DiktatDatabase
import me.hani.diktat.database.Word
import me.hani.diktat.databinding.FragmentWordsListBinding
import me.hani.diktat.repository.DiktatRepository
import java.util.*


class WordsListFragment : Fragment(), OnWordItemClickListener, TextToSpeech.OnInitListener {

    private var catWordList = arrayListOf<Word>()
    private lateinit var wordViewModel: WordViewModel
    private lateinit var adapter: WordAdapter
    private val args: WordsListFragmentArgs by navArgs()

    private var catId = 0
    private var catName: String? = null

    private lateinit var tts: TextToSpeech


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentWordsListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_words_list, container, false)

        //to show the menu items
        setHasOptionsMenu(true)

        //initializing TextToSpeech
        tts = TextToSpeech(context, this)

        // get a reference to the application context
        val application = requireNotNull(this.activity).application
        //get DAO of database reference
        val dataSource = DiktatDatabase.getDatabaseInstance(application).diktatDao()
        //get instance of the repository
        val repository = DiktatRepository(dataSource)
        //get instance of the viewModelFactory
        val viewModelFactory = WordViewModelFactory(repository, application)
        //get instance of the viewModelFactory
        wordViewModel = ViewModelProvider(this, viewModelFactory).get(WordViewModel::class.java)

        //get arguments
        catId = args.catId
        catName = args.catName

        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.words_list_title, args.catName)

        adapter = WordAdapter(this)
        binding.rvWordsList.adapter = adapter

        //get diktat array list and convert it to array list
        //catDiktatList = args.catDiktatList!!.toCollection(ArrayList())
        wordViewModel.getCategoryWordsList(args.catId)
            .observe(viewLifecycleOwner, { wordsList ->
                if (args.argsWordsList == null) {
                    catWordList = wordsList.toCollection(ArrayList())
                    adapter.wordsList = catWordList
                } else {
                    catWordList = args.argsWordsList!!.toCollection(ArrayList())
                    adapter.wordsList = catWordList
                }
            })


        return binding.root
    }

    override fun onItemButtonClicked(position: Int, btn: View) {
        val wordObj = catWordList[position]
        val artikel = catWordList[position].artikel
        val word = catWordList[position].word
        val plural = catWordList[position].plural

        when (btn.id) {
            //speak btn
            R.id.btn_words_speak -> {
                // speak current word
                tts.speak("$artikel, $word, $plural", TextToSpeech.QUEUE_FLUSH, null, null)

            }
            //edit word
            R.id.btn_words_edit -> {
                findNavController()
                    .navigate(
                        WordsListFragmentDirections.actionWordsListFragmentToAddWordFragment(
                            catName,
                            catId,
                            wordObj
                        )
                    )
            }
            //delete btn
            R.id.btn_words_delete -> {

                val builder = AlertDialog.Builder(context)

                builder.setMessage(resources.getString(R.string.alert_msg_sure_delete))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.alert_positive_btn)) { _, _ ->
                        // delete cat
                        wordViewModel.deleteWord(catWordList[position])

                        Log.d("catWordList.size", catWordList.size.toString())
                        //check if there are words left in the list ..
                        if (catWordList.size > 1) {
                            adapter.notifyDataSetChanged()
                        } else { //if the list is empty then navigate to the main fragment
                            findNavController().navigate(R.id.action_wordsListFragment_to_catListFragment)
                        }


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
        inflater.inflate(R.menu.words_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            //if menu item clicked navigate to test fragment
            R.id.menu_words_test -> {
                if (args.argsWordsList == null) {
                    findNavController().navigate(
                        WordsListFragmentDirections.actionWordsListFragmentToTestFragment(
                            catName,
                            catId
                        )
                    )
                } else {
                    findNavController().navigate(
                        WordsListFragmentDirections.actionWordsListFragmentToTestFragment(
                            catName,
                            catId,
                            args.argsWordsList
                        )
                    )
                }

            }
            R.id.menu_words_add -> findNavController().navigate(
                WordsListFragmentDirections.actionWordsListFragmentToAddWordFragment(
                    catName,
                    catId
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            tts.language = Locale.GERMANY

        } else {
            Toast.makeText(context, "Error With to TextToSpeech", Toast.LENGTH_SHORT).show()
        }
    }


}