package me.hani.diktat.ui.test

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hani.diktat.R
import me.hani.diktat.database.DiktatDatabase
import me.hani.diktat.database.Word
import me.hani.diktat.databinding.FragmentTestBinding
import me.hani.diktat.repository.DiktatRepository
import java.util.*
import kotlin.collections.ArrayList

class TestFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var binding: FragmentTestBinding
    private lateinit var testViewModel: TestViewModel
    private val args: TestFragmentArgs by navArgs()
    private var wordsList = arrayListOf<Word>()
    private lateinit var currentWord: String
    private var currentArtikel: String? = ""
    private var currentPlural: String? = ""
    private var wordIndex = 0
    private var wordNum = 0
    private var wordsInListCount = 0


    //correct answered words array list
    private var correctAnsweredWords = arrayListOf<Word>()

    //wrong answered words array list
    private var wrongAnsweredWords = arrayListOf<Word>()

    private var catId = 0
    private var catName: String? = null

    private lateinit var tts: TextToSpeech

    //on create view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false)

        //initializing TextToSpeech
        tts = TextToSpeech(context, this)

        //get args values
        catId = args.catId
        catName = args.catName

        // get a reference to the application context
        val application = requireNotNull(this.activity).application
        //get DAO of database reference
        val dataSource = DiktatDatabase.getDatabaseInstance(application).diktatDao()
        //get instance of the repository
        val repository = DiktatRepository(dataSource)
        //get instance of the viewModelFactory
        val viewModelFactory = TestViewModelFactory(repository, application)
        //get instance of the viewModelFactory
        testViewModel = ViewModelProvider(this, viewModelFactory).get(TestViewModel::class.java)

        //get cat list of words for the test
        testViewModel.getCategoryWordsList(catId)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                wordsList = if (args.argsWordsList == null) {
                    it.toCollection(ArrayList())
                } else {
                    args.argsWordsList!!.toCollection(ArrayList())
                }
                //shuffle the words
                wordsList.shuffle()
                // get list size
                wordsInListCount = wordsList.size
                //change action bar title
                (activity as AppCompatActivity).supportActionBar?.title =
                    getString(R.string.diktat_test_title, ++wordNum, wordsInListCount)
            })
        //speak the the first word in the list
        // and start the test
        onVolumeUpClicked()
        //check the inputs
        onCheckBtnClicked()

        return binding.root
    }

    //onclick speak the word
    private fun onVolumeUpClicked() {
        binding.btnVolumeUp.setOnClickListener {
            //only after the user clicked this view then enable this views
            binding.etTestArtikel.isEnabled = true
            binding.etTestWord.isEnabled = true
            binding.etTestPlural.isEnabled = true
            binding.btnCheckTest.isEnabled = true
            //get word from the list
            currentWord     = wordsList[wordIndex].word
            currentArtikel  = wordsList[wordIndex].artikel
            currentPlural   = wordsList[wordIndex].plural
            // speak only current word and let the user guess the artikel and plural
            tts.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    //on check button clicked
    private fun onCheckBtnClicked() {

        binding.btnCheckTest.setOnClickListener {
            // hide the keyboard in fragment
            hideKeyboard(it)

            // get value from edit text fields
            val artikelValue = binding.etTestArtikel.text.toString().trim()
            val wordValue = binding.etTestWord.text.toString().trim()
            val pluralValue = binding.etTestPlural.text.toString().trim()

            //empty words list and empty edit text values will match. so check if not empty
            // using isNotBlank() To ensure that the user has not only entered spaces
            if (artikelValue.isNotBlank() && wordValue.isNotBlank() && pluralValue.isNotBlank()) {
                //compare to the saved data in database
                if (currentWord == wordValue && currentArtikel == artikelValue && currentPlural == pluralValue) {
                    //add current word object correct words array
                    correctAnsweredWords.add(wordsList[wordIndex])
                    // 1 -> to tell the method it's from correct answer call
                    showAnimationAndHideOtherViews(1)
                    //load next word in the list
                    loadNextWord()
                } else {
                    //... else it is wrong answer
                    //add current word object wrong words array
                    wrongAnsweredWords.add(wordsList[wordIndex])
                    // 0 -> to tell the method it's from wrong answer call
                    showAnimationAndHideOtherViews(0)
                    //load next word in the list
                    loadNextWord()
                }

            } else {
                Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //show the animation view and hide other views
    private fun showAnimationAndHideOtherViews(answer: Int) {

        binding.etTestArtikel.visibility = View.GONE
        binding.etTestWord.visibility = View.GONE
        binding.etTestPlural.visibility = View.GONE
        binding.btnVolumeUp.isEnabled = false
        binding.btnCheckTest.isEnabled = false

        if (answer == 1) {
            //show lottie animation CORRECT view and play animation
            binding.laTestCorrect.visibility = View.VISIBLE
            binding.laTestCorrect.playAnimation()
        } else {
            //show lottie animation WRONG view and play animation
            binding.laTestWrong.visibility = View.VISIBLE
            binding.laTestWrong.playAnimation()
        }


        lifecycleScope.launch {
            delay(2000)

            //after two seconds hide animation
            binding.laTestCorrect.visibility = View.GONE
            binding.laTestWrong.visibility = View.GONE
            //and set other views to visible
            binding.etTestArtikel.visibility = View.VISIBLE
            binding.etTestWord.visibility = View.VISIBLE
            binding.etTestPlural.visibility = View.VISIBLE
            binding.btnVolumeUp.isEnabled = true
            //disable this views again
            binding.etTestArtikel.isEnabled = false
            binding.etTestWord.isEnabled = false
            binding.etTestPlural.isEnabled = false
            binding.btnCheckTest.isEnabled = false
        }

        //clear edit text fields
        binding.etTestArtikel.text.clear()
        binding.etTestWord.text.clear()
        binding.etTestPlural.text.clear()
    }

    //the user answered the word then ...
    private fun loadNextWord() {
        //...remove it from the list
        wordsList.removeAt(0)
        //because we don't know how many items in the list, so check if there is words left in it
        if (wordsList.isNotEmpty() && wordsList.size != 0) {
            //increase wordCount var and change title on action bar
            (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.diktat_test_title, ++wordNum, wordsInListCount)
        } else {
            lifecycleScope.launch {
                delay(2000)
                //the words list is empty, wait 2 seconds for the
                // animation view to complete then go to result fragment
                findNavController().navigate(
                    TestFragmentDirections.actionTestFragmentToResultFragment(
                        wordsInListCount,
                        wrongAnsweredWords.toTypedArray(),
                        correctAnsweredWords.toTypedArray(),
                        catName,
                        catId
                    )
                )
            }
        }
    }

    //TextToSpeech init
    override fun onInit(p0: Int) {

        if (p0 == TextToSpeech.SUCCESS) {

            tts.language = Locale.GERMANY

        } else {
            Toast.makeText(context, "Error With TextToSpeech", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        //stop text to speech engine but not shutting it down
        tts.stop()
        super.onPause()
    }

    //if fragment destroyed shut textToSpeak engine down
    override fun onDestroyView() {
        tts.shutdown()
        super.onDestroyView()
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}