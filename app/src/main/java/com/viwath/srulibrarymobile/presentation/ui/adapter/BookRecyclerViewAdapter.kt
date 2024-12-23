package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.data.dto.BookDto

class BookRecyclerViewAdapter(
    private var books: List<BookDto>
): RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder>(){

    inner class BookViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvBookQuan: TextView = view.findViewById(R.id.tvBookQuan)
        val tvLanguage: TextView = view.findViewById(R.id.tvLanguage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.tvTitle.text = book.bookTitle
        holder.tvBookQuan.text = "${book.bookQuan}"
        holder.tvLanguage.text = if (book.languageId === "kh") "Language.Khmer" else "Language.English"
        holder.itemView.setOnClickListener {

        }
    }

}