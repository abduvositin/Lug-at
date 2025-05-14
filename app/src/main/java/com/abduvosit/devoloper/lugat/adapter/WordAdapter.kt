package com.abduvosit.devoloper.lugat.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abduvosit.devoloper.lugat.R
import com.abduvosit.devoloper.lugat.ViewActivity
import com.abduvosit.devoloper.lugat.model.Word
import com.abduvosit.devoloper.lugat.sql.DatabaseHelper
import java.util.Locale

class WordAdapter(
    private var wordList: List<Word>,
    private val dbHelper: DatabaseHelper,
    private val isShowingMemorized: Boolean,
    private val longLang: String
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textEnglish: TextView = itemView.findViewById(R.id.textEnglish)
        val textUzbek: TextView = itemView.findViewById(R.id.textUzbek)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = wordList[position]
        bindWordData(holder, word)

        holder.itemView.setOnLongClickListener {
            toggleMemorizedStatus(word)
            true
        }

        holder.itemView.setOnClickListener {
            openWordDetails(it, word)
        }
    }

    override fun getItemCount(): Int = wordList.size

    private fun bindWordData(holder: WordViewHolder, word: Word) {
        when (longLang) {
            "UZ" -> {
                holder.textEnglish.text = word.english.lowercase(Locale.ROOT)
                holder.textUzbek.text = word.uzbek.lowercase(Locale.ROOT)
            }
            "EN" -> {
                holder.textEnglish.text = word.uzbek.lowercase(Locale.ROOT)
                holder.textUzbek.text = word.english.lowercase(Locale.ROOT)
            }
        }
    }

    private fun toggleMemorizedStatus(word: Word) {
        dbHelper.updateMemorized(word.id, !word.memorized)
        updateWordList()
    }

    private fun updateWordList() {
        val updatedList = if (isShowingMemorized) {
            dbHelper.getMemorizedWords()
        } else {
            dbHelper.getNotMemorizedWords()
        }
        updateList(updatedList)
    }

    private fun openWordDetails(view: View, word: Word) {
        val intent = Intent(view.context, ViewActivity::class.java).apply {
            putExtra("word", word)
        }
        view.context.startActivity(intent)
    }

    fun deleteItem(position: Int) {
        val word = wordList[position]
        dbHelper.deleteWord(word.id)
        wordList = wordList.toMutableList().apply { removeAt(position) }
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Word>) {
        wordList = newList
        notifyDataSetChanged()
    }
}
