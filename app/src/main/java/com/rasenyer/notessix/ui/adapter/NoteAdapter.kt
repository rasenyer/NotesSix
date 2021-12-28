package com.rasenyer.notessix.ui.adapter

import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rasenyer.notessix.databinding.ItemNoteBinding
import com.rasenyer.notessix.datasource.local.model.Note
import com.rasenyer.notessix.ui.fragments.HomeFragmentDirections
import java.util.*

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val noteModel = differ.currentList[position]
        myViewHolder.bind(noteModel)
    }

    override fun getItemCount(): Int { return differ.currentList.size }

    inner class MyViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {

            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = note.date.toLong()
            val dateFormat = DateFormat.format("EEEE, d MMMM yyyy", calendar).toString()

            binding.apply {

                binding.note = note
                binding.executePendingBindings()

                if (note.image.isNotEmpty()){
                    binding.mImageView.visibility = View.VISIBLE
                    binding.mImageView.setImageURI(Uri.parse(note.image))
                } else {
                    binding.mImageView.visibility = View.GONE
                }

                mTextViewTitle.text = note.title
                mTextViewDescription.text = note.description
                mTextViewDate.text = dateFormat

                root.setOnClickListener { v ->
                    val direction = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(note)
                    v.findNavController().navigate(direction)
                }

            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id && oldItem.description == newItem.description && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

}


