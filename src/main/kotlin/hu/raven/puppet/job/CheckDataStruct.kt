package hu.raven.puppet.job

import java.io.File

fun main() {
    val line = File("D:\\Research\\Results\\extractions\\costTimeSeries.json").useLines { it.first() }
    println(line)
}