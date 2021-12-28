package com.rasenyer.notessix.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.rasenyer.notessix.R
import com.rasenyer.notessix.databinding.ActivityMainBinding
import com.rasenyer.notessix.repository.NoteRepository
import com.rasenyer.notessix.datasource.local.database.NoteDatabase
import com.rasenyer.notessix.utils.hide
import com.rasenyer.notessix.utils.show
import com.rasenyer.notessix.utils.viewModelFactory
import com.rasenyer.notessix.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val noteRepository by lazy { NoteRepository(NoteDatabase(this)) }
    private val noteViewModel: NoteViewModel by viewModels { viewModelFactory { NoteViewModel(this.application, noteRepository) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteViewModel

        initViews(binding)
        observeNavElements(binding, navHostFragment.navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        navHostFragment.navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    private fun initViews(binding: ActivityMainBinding) {

        setSupportActionBar(binding.mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.mNavHostFragment) as NavHostFragment? ?: return

        with(navHostFragment.navController) {
            appBarConfiguration = AppBarConfiguration(graph)
            setupActionBarWithNavController(this, appBarConfiguration)
        }

    }

    private fun observeNavElements(binding: ActivityMainBinding, navController: NavController) {

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                R.id.HomeFragment -> {
                    supportActionBar!!.setDisplayShowTitleEnabled(false)
                    binding.mTextViewTitle.show()
                }
                R.id.AddFragment -> {
                    supportActionBar!!.setDisplayShowTitleEnabled(true)
                    binding.mTextViewTitle.hide()
                }

                R.id.UpdateFragment -> {
                    supportActionBar!!.setDisplayShowTitleEnabled(true)
                    binding.mTextViewTitle.hide()
                }

                else -> { supportActionBar!!.setDisplayShowTitleEnabled(true) }

            }

        }

    }

}