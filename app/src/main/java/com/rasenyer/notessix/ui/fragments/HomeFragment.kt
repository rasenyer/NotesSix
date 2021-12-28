package com.rasenyer.notessix.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rasenyer.notessix.R
import com.rasenyer.notessix.ui.adapter.NoteAdapter
import com.rasenyer.notessix.databinding.FragmentHomeBinding
import com.rasenyer.notessix.datasource.local.model.Note
import com.rasenyer.notessix.utils.hide
import com.rasenyer.notessix.utils.show
import com.rasenyer.notessix.ui.base.BaseFragment
import com.rasenyer.notessix.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, NoteViewModel>() {

    private lateinit var noteAdapter: NoteAdapter
    override val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDeleteAll.setOnClickListener { deleteAll() }
        mAdd.setOnClickListener { findNavController().navigate(R.id.action_HomeFragment_to_AddFragment) }

        setUpRecyclerView()

    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)

        lifecycleScope.launchWhenStarted {
            val isChecked = noteViewModel.getUIMode.first()
            val uiMode = menu.findItem(R.id.mLight)
            uiMode.isChecked = isChecked
            setUIMode(uiMode, isChecked)
        }

        val search = menu.findItem(R.id.mSearch)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_notes)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) { searchByTitle(query) }
                return true

            }

            override fun onQueryTextChange(query: String?): Boolean {

                if (query != null){ searchByTitle(query) }
                return true

            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.mLight -> {
                item.isChecked = !item.isChecked
                setUIMode(item, item.isChecked)
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

    private fun setUpRecyclerView() {

        noteAdapter = NoteAdapter()

        mRecyclerView.apply {

            setHasFixedSize(true)
            adapter = noteAdapter

            activity.let {

                noteViewModel.getAll().observe(viewLifecycleOwner, { note ->
                    noteAdapter.differ.submitList(note)
                    updateUI(note)

                })

            }

        }

    }

    private fun updateUI(noteList: List<Note>) {

        if (noteList.isNotEmpty()) {
            mLayoutEmpty.hide()
            mRecyclerView.visibility = View.VISIBLE
        } else {
            mLayoutEmpty.show()
            mRecyclerView.visibility = View.GONE
        }

    }

    private fun setUIMode(item: MenuItem, isChecked: Boolean) {

        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            noteViewModel.saveUIModeToDataStore(true)
            item.setIcon(R.drawable.ic_night)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            noteViewModel.saveUIModeToDataStore(false)
            item.setIcon(R.drawable.ic_day)
        }

    }

    private fun searchByTitle(query: String?){

        val searchQuery = "%$query%"

        noteViewModel.searchByTitle(searchQuery).observe(this, { listNotes ->
            noteAdapter.differ.submitList(listNotes)
        })

    }

    private fun deleteAll(){

        AlertDialog.Builder(activity).apply {
            setTitle(R.string.delete_all_notes)
            setMessage(R.string.are_you_sure_you_want_to_delete_all_notes)
            setCancelable(false)
            setPositiveButton(R.string.delete) { _, _ -> noteViewModel.deleteAll() }
            setNegativeButton(R.string.cancel, null)
        }.create().show()

    }

}