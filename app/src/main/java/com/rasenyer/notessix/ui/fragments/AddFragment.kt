package com.rasenyer.notessix.ui.fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.rasenyer.notessix.R
import com.rasenyer.notessix.databinding.FragmentAddBinding
import com.rasenyer.notessix.datasource.local.model.Note
import com.rasenyer.notessix.ui.base.BaseFragment
import com.rasenyer.notessix.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.layout_content.view.*

@AndroidEntryPoint
class AddFragment : BaseFragment<FragmentAddBinding, NoteViewModel>() {

    override val noteViewModel: NoteViewModel by activityViewModels()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLayoutContent.mMaterialCardViewDelete.setOnClickListener {
            imageUri = null
            mLayoutContent.mRelativeLayout.visibility = View.GONE
        }

    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddBinding.inflate(inflater, container, false)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mAdd -> ImagePicker.with(FragmentComponentManager.findActivity(view?.context) as Activity).crop().createIntentFromDialog { launcherImage.launch(it) }
            R.id.mInsert -> insertNote()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun insertNote() = with(binding) {

        val title = mLayoutContent.mEditTextTitle.text.toString().trim()
        val description = mLayoutContent.mEditTextDescription.text.toString().trim()

        if (title.isNotEmpty() && description.isNotEmpty()) {

            if (imageUri == null) {

                val note = Note(
                    id = 0,
                    title = title,
                    description = description,
                    date = System.currentTimeMillis().toString(),
                    image = ""
                )
                noteViewModel.insert(note)
                toast(getString(R.string.note_added_successfully))
                findNavController().navigate(R.id.action_AddFragment_to_HomeFragment)

            } else {

                val note = Note(
                    id = 0,
                    title = title,
                    description = description,
                    date = System.currentTimeMillis().toString(),
                    image = imageUri.toString()
                )
                noteViewModel.insert(note)
                toast(getString(R.string.note_added_successfully))
                findNavController().navigate(R.id.action_AddFragment_to_HomeFragment)

            }

        } else { toast(getString(R.string.please_fill_in_the_fields)) }

    }

    private val launcherImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data!!
            mLayoutContent.mRelativeLayout.visibility = View.VISIBLE
            mLayoutContent.mImageView.setImageURI(imageUri)
        }

    }

}