package hu.raven.puppet.utility.extention

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = get(index1)
    set(index1, get(index2))
    set(index2, tmp)
}
