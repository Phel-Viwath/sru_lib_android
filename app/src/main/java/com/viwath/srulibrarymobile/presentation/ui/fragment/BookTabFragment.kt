package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBookTabBinding
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.ui.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.ui.adapter.BookRecyclerViewAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.BookTabViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookTabFragment : Fragment() {

    private var _binding: FragmentBookTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var loading: Loading

    private val viewModel: BookTabViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookTabBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = Loading(requireActivity())

        binding.edtSearch.setOnFocusChangeListener{_, hasFocus ->
            val activity = requireActivity()
            if (hasFocus){
                if (activity is MainActivity) activity.hideBottomNav()
            }else {
                if (activity is MainActivity) activity.showBottomNav()
            }
        }

        // hide keyboard
        binding.rootLayout.setOnTouchListener { _, _ ->
            binding.edtSearch.clearFocus()
            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            false
        }
        // search book
        binding.layoutSearch.setEndIconOnClickListener {

        }

        /// call state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                val book = state.books
                when{
                    state.isLoading -> startLoading()
                    state.error.isNotBlank() -> {
                        stopLoading()
                        Toast.makeText(requireContext(), "Loaded ${state.error} books", Toast.LENGTH_SHORT).show()
                    }
                    book.isNotEmpty() -> {
                        stopLoading()
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.recyclerBook.layoutManager = LinearLayoutManager(requireContext())
                            val adapter = BookRecyclerViewAdapter(book)
                            binding.recyclerBook.adapter = adapter
                        }
                    }
                }
            }


        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventFlow.collect{ event ->
                when(event){
                    is ResultEvent.ShowSnackbar -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is ResultEvent.ShowError -> {
                        Toast.makeText(requireContext(), event.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startLoading() = CoroutineScope(Dispatchers.Main).launch{
        loading.loadingStart()
    }

    private fun stopLoading() = CoroutineScope(Dispatchers.Main).launch {
        loading.loadingDismiss()
    }

}