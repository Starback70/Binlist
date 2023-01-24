package ru.izotov.binlist.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val liveBinData = MutableLiveData<BinModel>()
    val binDataList = MutableLiveData<List<BinModel>>()
}