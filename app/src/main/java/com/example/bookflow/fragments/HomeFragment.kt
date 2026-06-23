package com.example.bookflow.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.FragmentHomeBinding
import com.example.bookflow.models.Book
import com.example.bookflow.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private val tag = "HomeFragment"

    // ViewBinding : nullable car le view peut être détruit (lifecycle Fragment)
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel partagé via le scope du Fragment
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var bookAdapter: BookAdapter

    // Création de la vue du Fragment

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }


    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { book ->
                onBookClicked(book)
            }
        )

        binding.recyclerViewBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    //Observe les LiveData du ViewModel pour mettre à jour l'UI.

    private fun observeViewModel() {
        // Liste des livres
        viewModel.books.observe(viewLifecycleOwner) { books ->
            Log.d(tag, "Mise à jour UI : ${books.size} livres reçus")
            bookAdapter.submitList(books)
        }

        // État de chargement
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Erreur
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.textViewError.text = errorMessage
                binding.textViewError.visibility = View.VISIBLE
                binding.recyclerViewBooks.visibility = View.GONE
            } else {
                binding.textViewError.visibility = View.GONE
                binding.recyclerViewBooks.visibility = View.VISIBLE
            }
        }
    }


    private fun onBookClicked(book: Book) {
        Log.d(tag, "Livre cliqué : ${book.title}")
        // TODO : Ouvrir BookDetailActivity avec book.id
    }

    // nettoyage du binding pour éviter les fuites mémoire
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}