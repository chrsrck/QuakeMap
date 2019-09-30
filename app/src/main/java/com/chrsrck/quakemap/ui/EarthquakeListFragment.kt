package com.chrsrck.quakemap.ui


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.FragmentEarthquakeListBinding
import com.chrsrck.quakemap.model.Earthquake
import com.chrsrck.quakemap.viewmodel.ListViewModel
import com.chrsrck.quakemap.viewmodel.NetworkViewModel
import com.google.android.gms.maps.GoogleMap

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [EarthquakeListFragment.OnListFragmentInteractionListener] interface.
 */
class EarthquakeListFragment : Fragment() {

    private lateinit var networkViewModel : NetworkViewModel
    private lateinit var listViewModel : ListViewModel
    private lateinit var recyclerView : androidx.recyclerview.widget.RecyclerView

    private val eqObserver = Observer<HashMap<String, Earthquake>> { hashMap ->
        listViewModel.updateQuakeList(hashMap)
        recyclerView.adapter?.notifyDataSetChanged()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        networkViewModel =
                ViewModelProviders.of((activity as MainActivity)).get(NetworkViewModel::class.java)
        networkViewModel.eqLiveData.observe(this, eqObserver)

        listViewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        val binding : FragmentEarthquakeListBinding
                = FragmentEarthquakeListBinding.inflate(inflater)
        binding.viewModel = listViewModel
        binding.setLifecycleOwner(this)
        val view = binding.root
        recyclerView = view.findViewById(R.id.list)


        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = MyEarthquakeRecyclerViewAdapter(listViewModel.getQuakeList())
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() {

        }
    }
}
