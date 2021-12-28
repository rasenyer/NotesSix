package com.rasenyer.notessix.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ShareCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.drjacky.imagepicker.ImagePicker
import com.rasenyer.notessix.R
import com.rasenyer.notessix.databinding.FragmentUpdateBinding
import com.rasenyer.notessix.datasource.local.model.Note
import com.rasenyer.notessix.ui.base.BaseFragment
import com.rasenyer.notessix.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.layout_content.view.*
import java.util.*

@AndroidEntryPoint
class UpdateFragment : BaseFragment<FragmentUpdateBinding, NoteViewModel>() {

    private val updateFragmentArgs: UpdateFragmentArgs by navArgs()
    override val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var currentNote: Note
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentNote = updateFragmentArgs.currentNote!!

        mLayoutContent.mMaterialCardViewDelete.setOnClickListener {
            imageUri = null
            mLayoutContent.mRelativeLayout.visibility = View.GONE
        }

        setViews()

    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUpdateBinding.inflate(inflater, container, false)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mAdd -> ImagePicker.with(FragmentComponentManager.findActivity(view?.context) as Activity).crop().createIntentFromDialog { launcherImage.launch(it) }
            R.id.mUpdate -> update()
            R.id.mShare -> share()
            R.id.mDelete -> delete()
        }

        return super.onOptionsItemSelected(item)

    }

    private fun setViews() = with(binding) {

        imageUri = Uri.parse(currentNote.image)

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = currentNote.date.toLong()
        val dateFormat = DateFormat.format("EEEE, d MMMM yyyy, HH:mm:ss", calendar).toString()

        mLayoutContent.mEditTextTitle.setText(currentNote.title)
        mLayoutContent.mEditTextDescription.setText(currentNote.description)
        mLayoutContent.mTextViewDate.text = dateFormat

        if (currentNote.image.isNotEmpty()) {

            mLayoutContent.mRelativeLayout.visibility = View.VISIBLE
            mLayoutContent.mImageView.setImageURI(imageUri)

            mLayoutContent.mMaterialCardViewDelete.setOnClickListener {
                imageUri = null
                mLayoutContent.mRelativeLayout.visibility = View.GONE
            }

        } else { mLayoutContent.mRelativeLayout.visibility = View.GONE }

    }

    private fun update() = with(binding) {

        val title = mLayoutContent.mEditTextTitle.text.toString().trim()
        val description = mLayoutContent.mEditTextDescription.text.toString().trim()

        if (title.isNotEmpty() && description.isNotEmpty()) {

            if (imageUri == null) {

                val note = Note(
                    id = currentNote.id,
                    title = title,
                    description = description,
                    date = System.currentTimeMillis().toString(),
                    image = ""
                )
                noteViewModel.update(note)
                toast(getString(R.string.note_updated_successfully))
                findNavController().navigate(R.id.action_UpdateFragment_to_HomeFragment)

            } else {

                val note = Note(
                    id = currentNote.id,
                    title = title,
                    description = description,
                    date = System.currentTimeMillis().toString(),
                    image = imageUri.toString()
                )
                noteViewModel.update(note)
                toast(getString(R.string.note_updated_successfully))
                findNavController().navigate(R.id.action_UpdateFragment_to_HomeFragment)

            }

        } else { toast(getString(R.string.please_fill_in_the_fields)) }


    }

    @SuppressLint("StringFormatMatches")
    private fun share() = with(binding) {

        val shareNote = getString(
            R.string.share_note,
            mLayoutContent.mEditTextTitle.text.toString(),
            mLayoutContent.mEditTextDescription.text.toString(),
            mLayoutContent.mTextViewDate.text.toString()
        )

        val intent = ShareCompat.IntentBuilder(requireActivity()).setType("text/plain").setText(shareNote).intent
        startActivity(Intent.createChooser(intent, null))

    }

    private fun delete() {

        android.app.AlertDialog.Builder(activity).apply {
            setTitle(R.string.delete_note)
            setMessage(R.string.are_you_sure_you_want_to_permanently_delete_this_note)
            setCancelable(false)
            setPositiveButton(R.string.delete) { _, _ ->
                noteViewModel.delete(currentNote)
                view?.findNavController()?.navigate(R.id.action_UpdateFragment_to_HomeFragment)
            }
            setNegativeButton(R.string.cancel, null)
        }.create().show()

    }

    private val launcherImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data!!
            mLayoutContent.mRelativeLayout.visibility = View.VISIBLE
            mLayoutContent.mImageView.setImageURI(imageUri)
        }

    }

}