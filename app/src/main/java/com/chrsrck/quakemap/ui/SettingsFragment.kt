package com.chrsrck.quakemap.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.SettingsFragmentBinding
import com.chrsrck.quakemap.viewmodel.SettingsViewModel


class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val binding : SettingsFragmentBinding = SettingsFragmentBinding.inflate(inflater);
        binding.viewmodel = viewModel
        binding.setLifecycleOwner(this)
        val view = binding.root

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        val darkModeObserver = Observer<Boolean> {
            val actCompat = activity as AppCompatActivity
            if (it != null && it) {
                actCompat.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                actCompat.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        viewModel.isDarkMode.observe(this, darkModeObserver)
    }

}
