package com.example.todolist

import java.io.Serializable
import java.util.*

open class Task(val id: Int = 0 ,val title: String = "", val content: String = "", val date: Date = Date(), val uid: String = ""):Serializable {

}