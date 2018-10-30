package com.chrsrck.quakemap.ui


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var networkViewModel : NetworkViewModel
    private lateinit var listViewModel : ListViewModel
    private lateinit var recyclerView : RecyclerView

    private val eqObserver = Observer<HashMap<String, Earthquake>> { hashMap ->
        listViewModel.updateQuakeList(hashMap)
        // TODO: notify recycler view of data change
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_earthquake_list, container, false)

        networkViewModel =
                ViewModelProviders.of((activity as MainActivity)).get(NetworkViewModel::class.java)
        networkViewModel.observeEarthquakes(this, eqObserver)

        listViewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        val binding : FragmentEarthquakeListBinding
                = FragmentEarthquakeListBinding.inflate(inflater)
        binding.viewModel = listViewModel
        binding.setLifecycleOwner(this)
        val view = binding.root
        recyclerView = view.findViewById(R.id.list)
        recyclerView?.setRecyclerListener( onRecyclerListener)

        with(recyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            adapter =
                    MyEarthquakeRecyclerViewAdapter(
                            listViewModel.getQuakeList(),
                            listener)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private val onRecyclerListener = RecyclerView.RecyclerListener { viewHolder ->
        val mapHolder = (viewHolder as MyEarthquakeRecyclerViewAdapter.ViewHolder)
        if (mapHolder != null && mapHolder.googleMap != null) {
            mapHolder?.googleMap?.clear()
            mapHolder?.googleMap?.mapType = GoogleMap.MAP_TYPE_NONE
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Earthquake?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                EarthquakeListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
