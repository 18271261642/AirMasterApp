package com.app.airmaster.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.airmaster.bean.DbManager
import com.app.airmaster.bean.NoteBookBean

/**
 * Created by Admin
 *Date 2023/1/10
 */
class NoteBookViewModel : ViewModel() {

    //查询所有的记事本数据
    var allNoteBookData = MutableLiveData<List<NoteBookBean>>()


    //查询所有的记事本数据
    fun getAllDbData(){
        val list = DbManager.getInstance().queryAllNoteBook()
        allNoteBookData.postValue(list)
    }
}