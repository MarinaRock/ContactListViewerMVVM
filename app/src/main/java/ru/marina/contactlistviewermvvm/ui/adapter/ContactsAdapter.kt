package ru.marina.contactlistviewermvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.marina.contactlistviewermvvm.R
import ru.marina.contactlistviewermvvm.databinding.RowContactBinding
import ru.marina.contactlistviewermvvm.domain.model.Contact

class ContactsAdapter : PagedListAdapter<Contact, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RowContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    inner class ViewHolder(private val binding: RowContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact?) {
            if (contact == null) {
                return
            }

            binding.contactAvatarImageView.load(contact.avatar) {
                error(R.drawable.ic_placeholder)
                crossfade(true)
                crossfade(500)
                transformations(CircleCropTransformation())
            }
            binding.contactNameTextView.text = contact.name
            binding.contactPhoneTextView.text = contact.phone
            binding.contactHeightTextView.text = contact.height.toString()

            binding.contactLayout.setOnClickListener {
                onItemClickListener?.onItemClick(contact)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.avatar == newItem.avatar
                    && oldItem.phone == newItem.phone
                    && oldItem.height == newItem.height
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(contact: Contact)
    }
}