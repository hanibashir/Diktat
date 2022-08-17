package me.hani.diktat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import me.hani.diktat.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //full screen
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//            (window.insetsController?.hide(WindowInsets.Type.statusBars()))
//                    ?: window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //back arrow in action bar
        setupActionBarWithNavController(findNavController(R.id.host_fragment))

    }


    //activate up button
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.host_fragment)
        return super.onSupportNavigateUp() || navController.navigateUp()
    }
}
