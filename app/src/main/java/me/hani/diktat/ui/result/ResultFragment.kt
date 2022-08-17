package me.hani.diktat.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hani.diktat.R
import me.hani.diktat.database.DiktatDatabase
import me.hani.diktat.database.Word
import me.hani.diktat.databinding.FragmentResultBinding
import me.hani.diktat.repository.DiktatRepository


class ResultFragment : Fragment(), View.OnClickListener {

    private var catId = 0
    private var catName: String? = null
    private lateinit var binding: FragmentResultBinding
    private lateinit var resultViewModel: ResultViewModel
    private val args: ResultFragmentArgs by navArgs()
    private var correctAnsweredWords = arrayListOf<Word>()
    private var wrongAnsweredWords = arrayListOf<Word>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)

        // get a reference to the application context
        val application = requireNotNull(this.activity).application
        //get DAO of database reference
        val dataSource = DiktatDatabase.getDatabaseInstance(application).diktatDao()
        //get instance of the repository
        val repository = DiktatRepository(dataSource)
        //get instance of the viewModelFactory
        val viewModelFactory = ResultViewModelFactory(repository, application)
        //get instance of the viewModelFactory
        resultViewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)

        //values to pass to ResultFragment then to wordsListFragment
        catId = args.catId
        catName = args.catName
        // correct answered words array
        correctAnsweredWords = args.correctWordsArray!!.toCollection(ArrayList())
        // correct answered words array
        wrongAnsweredWords = args.wrongWordsArray!!.toCollection(ArrayList())

        //set views text values
        setViewsValues()

        lifecycleScope.launch(Dispatchers.IO) {
            //update is_correct column in the database
            updateIsCorrectColumn()

        }

        //on buttons clicked
        binding.btnResultAll.setOnClickListener(this)
        binding.btnResultCorrect.setOnClickListener(this)
        binding.btnResultWrong.setOnClickListener(this)

        binding.btnResultAgain.setOnClickListener(this)
        binding.btnResultExit.setOnClickListener(this)

        return binding.root
    }


    private fun setViewsValues() {
        //set the score text view
        (correctAnsweredWords.size.toString() + " / " + args.wordsNum).also {
            binding.tvResultScore.text = it
        }
        binding.btnResultAll.text = args.wordsNum.toString()
        binding.btnResultCorrect.text = correctAnsweredWords.size.toString()
        binding.btnResultWrong.text = wrongAnsweredWords.size.toString()
    }

    private fun updateIsCorrectColumn() {
        correctAnsweredWords.forEachIndexed { _, word ->
            resultViewModel.updateCorrectWrong(1, word.id)
        }
        wrongAnsweredWords.forEachIndexed { _, word ->
            resultViewModel.updateCorrectWrong(0, word.id)
        }
    }

//    private fun saveToSharedPreference(correctNum: Int, wrongNum: Int) {
//        val sp = activity?.getSharedPreferences(catName, Context.MODE_PRIVATE) ?: return
//        with(sp.edit()) {
//            putInt("correct", correctNum)
//            putInt("wrong", wrongNum)
//            apply()
//        }
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_result_all -> {
                findNavController().navigate(
                    ResultFragmentDirections.actionResultFragmentToWordsListFragment(catName, catId)
                )
            }
            R.id.btn_result_correct -> {
                findNavController().navigate(
                    ResultFragmentDirections.actionResultFragmentToWordsListFragment(
                        catName,
                        catId,
                        correctAnsweredWords.toTypedArray()
                    )
                )
            }
            R.id.btn_result_wrong -> {
                findNavController().navigate(
                    ResultFragmentDirections.actionResultFragmentToWordsListFragment(
                        catName,
                        catId,
                        wrongAnsweredWords.toTypedArray()
                    )
                )
            }
            R.id.btn_result_again -> {
                findNavController().navigate(
                    ResultFragmentDirections.actionResultFragmentToWordsListFragment(catName, catId)
                )
            }

            R.id.btn_result_exit -> {
                findNavController().navigate(R.id.action_resultFragment_to_catListFragment)
            }
        }
    }
}