package com.abduvosit.devoloper.lugat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abduvosit.devoloper.lugat.adapter.WordAdapter
import com.abduvosit.devoloper.lugat.databinding.FragmentYodlanmaganBinding
import com.abduvosit.devoloper.lugat.shared.SettingsManager
import com.abduvosit.devoloper.lugat.sql.DatabaseHelper

class YodlanmaganFragment : Fragment() {

    private var _binding: FragmentYodlanmaganBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: WordAdapter
    private lateinit var settings: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYodlanmaganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings = SettingsManager(requireContext())
        dbHelper = DatabaseHelper(requireContext())

        setupRecyclerView()
        setupSwipeToDelete()
        loadWords()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val longLang = settings.getSetting("wordLongLang", "EN")
        adapter = WordAdapter(emptyList(), dbHelper, false, longLang)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showDeleteConfirmationDialog(viewHolder)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }

    private fun showDeleteConfirmationDialog(viewHolder: RecyclerView.ViewHolder) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("O'chirishni tasdiqlang")
            .setMessage("Siz ushbu elementni o'chirmoqchimisiz?")
            .setPositiveButton("Ha") { _, _ ->
                adapter.deleteItem(viewHolder.adapterPosition)
            }
            .setNegativeButton("Yo'q") { dialog, _ ->
                adapter.notifyItemChanged(viewHolder.adapterPosition)
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun loadWords() {
        val wordList = dbHelper.getNotMemorizedWords()
        adapter.updateList(wordList)
        binding.emptyText.visibility = if (wordList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadWords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
