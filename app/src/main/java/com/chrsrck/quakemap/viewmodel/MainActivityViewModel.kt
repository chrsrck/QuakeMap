package com.chrsrck.quakemap.viewmodel

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val activeItem : Int

    init {
        activeItem = 0
    }

}