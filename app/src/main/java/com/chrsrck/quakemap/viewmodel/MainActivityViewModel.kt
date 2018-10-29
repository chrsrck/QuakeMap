package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val activeItem : Int

    init {
        activeItem = 0
    }

}