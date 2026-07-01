package com.example.bookflow.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bookflow.activities.LoginActivity
import com.example.bookflow.auth.AuthPreferences
import com.example.bookflow.databinding.FragmentProfileBinding
import com.example.bookflow.repositories.LibraryRepository
import com.example.bookflow.utils.LanguageManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var libraryRepository: LibraryRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryRepository = LibraryRepository(requireContext())

        displayUserInfo()
        observeLibraryCount()
        setupButtons()
    }


     //Affiche le nom, email et initiale de l'user connecté.

    private fun displayUserInfo() {
        val context = requireContext()
        val name = AuthPreferences.displayName(context).ifBlank { "User" }

        binding.profileName.text = name
        binding.profileEmail.text = "Book lover 📚"

        // Initiale : première lettre du nom en majuscule
        binding.avatarInitial.text = name.first().uppercase()
    }


     // Observe le nombre de livres sauvegardés dans la BDD Room.

    private fun observeLibraryCount() {
        libraryRepository.getAllSavedBooks().observe(viewLifecycleOwner) { books ->
            val count = books?.size ?: 0
            binding.statBooksCount.text = count.toString()
        }
    }

    private fun setupButtons() {
        // Bouton changer langue
        binding.languageButton.setOnClickListener {
            LanguageManager.showLanguageDialog(requireActivity() as AppCompatActivity)
        }

        // Bouton déconnexion (avec confirmation)
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    /**
     * Affiche une popup de confirmation avant la déconnexion.
     */
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log out?")
            .setMessage("You will need to enter your credentials again to access the app.")
            .setPositiveButton("Confirm") { _, _ -> performLogout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        // Marquer comme déconnecté
        AuthPreferences.logout(requireContext())

        // Retour à LoginActivity + vider la pile
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}