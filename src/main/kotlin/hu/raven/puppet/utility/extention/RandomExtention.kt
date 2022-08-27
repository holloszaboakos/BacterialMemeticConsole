package hu.raven.puppet.utility.extention

import kotlin.random.Random
import kotlin.random.nextInt

fun Random.nextSegmentStartPosition(rangeMax:Int, segmentSize:Int) =
    nextInt(0 until (rangeMax - segmentSize))