package me.hani.diktat.ui.category

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
import me.hani.diktat.database.Category
import me.hani.diktat.database.DiktatDatabase
import me.hani.diktat.databinding.FragmentAddCatBinding
import me.hani.diktat.repository.DiktatRepository

class AddCatFragment : Fragment() {

    private lateinit var binding: FragmentAddCatBinding
    private lateinit var catViewModel: CatViewModel
    private lateinit var args: AddCatFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_cat, container, false)

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

        args = AddCatFragmentArgs.fromBundle(requireArguments())

        if (args.catId > 0) {
            // fill edit text field with cat name
            binding.etTestWord.setText(args.catName)
            // change btn text to update
            binding.btnSaveCat.text = resources.getString(R.string.update)
        }

        binding.btnSaveCat.setOnClickListener {

            addEditCat()

            // hide the keyboard in fragment
            hideKeyboard(it)
        }

        return binding.root
    }


    private fun addEditCat() {

        val etCat = binding.etTestWord.text.toString()

        // create cat object
        //args.catId default value is 0
        val catObj = Category(args.catId, etCat)

        if (etCat.isNotBlank()) {
            if (args.catId <= 0) { // if true then add new category

                //pass the data to database
                catViewModel.addCat(catObj)

                Toast.makeText(
                    context,
                    getString(R.string.success_adding_category),
                    Toast.LENGTH_SHORT
                ).show()
                //navigate back to the diktat recycler view list
                findNavController().navigate(R.id.action_addCatFragment_to_catListFragment)
            } else { // if args.catId > 0 then update the category


                catViewModel.updateCat(catObj)

                Toast.makeText(
                    context,
                    getString(R.string.success_updating_category),
                    Toast.LENGTH_SHORT
                ).show()
                //navigate back to the diktat recycler view list
                findNavController().navigate(R.id.action_addCatFragment_to_catListFragment)
            }


        } else {
            Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
        }

    }

    //hide keyboard
    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}