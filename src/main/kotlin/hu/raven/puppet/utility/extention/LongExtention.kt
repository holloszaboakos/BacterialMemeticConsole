package hu.raven.puppet.utility.extention

fun Long.log2(): Int {
    return 63 - java.lang.Long.numberOfLeadingZeros(this)
}

inline fun Long.timesTwoToThePowerOf(power: Int) = shl(power)

inline fun Long.divByTwoToThePowerOf(power: Int) = shr(power)
