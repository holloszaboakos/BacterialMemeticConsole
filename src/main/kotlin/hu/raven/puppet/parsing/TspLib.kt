package hu.raven.puppet.parsing

import com.mlaskows.tsplib.datamodel.tsp.Tsp
import com.mlaskows.tsplib.datamodel.types.EdgeWeightType
import kotlin.math.sqrt


private fun euclidianDistance(n1: Tsp.Node, n2: Tsp.Node): Double {
    return sqrt((n2.y - n1.y).let { it * it } + (n2.x - n1.x).let { it * it })
}

data class TspLibData(
    val nodes: List<Tsp.Node>,
    val weightMatrix: Array<IntArray>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TspLibData

        if (nodes != other.nodes) return false
        if (!weightMatrix.contentDeepEquals(other.weightMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nodes.hashCode()
        result = 31 * result + weightMatrix.contentDeepHashCode()
        return result
    }
}

fun loadTspLibData(libTspSpec: Tsp): TspLibData {
    if (libTspSpec.edgeWeightType === EdgeWeightType.EUC_2D) {
        val nodes = libTspSpec.nodes.get()
        val weightMatrix = Array(nodes.size) { i ->
            IntArray(nodes.size) { j ->
                Math.round(euclidianDistance(nodes.get(i), nodes.get(j))).toInt()
            }
        }

        return TspLibData(nodes, weightMatrix)
    } else {
        throw UnsupportedOperationException("Only EUC_2D edge weight types are supported currently.")
    }
}
