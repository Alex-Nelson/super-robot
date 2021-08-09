package com.example.ibuyapp.display

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ViewFlipper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ibuyapp.R
import com.example.ibuyapp.data.AppDatabase
import com.example.ibuyapp.databinding.FragmentDisplayBinding
import com.example.ibuyapp.util.DisplayViewState


/**
 * A [Fragment] to display any lists the user has made in the app. If there are no lists, it needs
 * to display the empty state layout until a list has been created.
 */
class DisplayFragment : Fragment() {

    private lateinit var binding: FragmentDisplayBinding
    private lateinit var listAdapter: DisplayListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewFlipper: ViewFlipper

    private object Flipper {
        const val EMPTY = 0 // No lists have been created
        const val CONTENT = 1 // At least one list has been created
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display, container,
            false)
        viewFlipper = binding.viewFlipper

        recyclerView = binding.contentState.displayList

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).appDao

        val viewModelFactory = DisplayViewModelFactory(dataSource)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(DisplayViewModel::class.java)

        listAdapter = DisplayListAdapter()

        // Observe the view state to render the correct layout
        viewModel.viewState.observe(viewLifecycleOwner, {state ->
            render(state)
        })

        recyclerView.adapter = listAdapter

        // Set an onClickListener for the FAB
        binding.emptyState.createNewListFAB.setOnClickListener { viewModel.onFabClicked() }
        binding.contentState.createNewListFAB.setOnClickListener { viewModel.onFabClicked() }

        // Navigate to the next screen to create a new list when the FAB is clicked
        viewModel.navigateToManageList.observe(viewLifecycleOwner, {
            if (it == true){
                this.findNavController()
                    .navigate(DisplayFragmentDirections
                    .actionDisplayFragmentToManageListFragment(0L))

                viewModel.doneNavigatingToManageList()
            }
        })

    }

    /**
     * Checks which layout to display (either the empty state or content state) based on if there is
     * at least one list stored in the Room database.
     * */
    private fun render(viewState: DisplayViewState){
        // Determine which layout to display
        if(viewState.isEmpty){
            viewFlipper.displayedChild = Flipper.EMPTY
        }else{
            viewFlipper.displayedChild = Flipper.CONTENT
        }

        listAdapter.submitList(viewState.lists)
    }

}