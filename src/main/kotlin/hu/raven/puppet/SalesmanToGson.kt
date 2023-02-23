package hu.raven.puppet

import com.google.gson.Gson
import hu.raven.puppet.model.task.DSalesman

//TODO processing old data structture to new one
fun main() {
    val gson = Gson()
    val salesman = DSalesman(    )
    println(gson.toJson(salesman))
}