package hu.raven.puppet.logic

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory
import hu.raven.puppet.model.inner.setup.AAlgorithm4VRPSetup
import hu.raven.puppet.model.mtsp.DEdge
import hu.raven.puppet.model.mtsp.DGraph
import hu.raven.puppet.model.mtsp.DObjective
import hu.raven.puppet.model.mtsp.DSalesman
import org.koin.java.KoinJavaComponent.inject
import kotlin.properties.Delegates

abstract class AAlgorithm4VRP<S : ISpecimenRepresentation> {
    val subSolutionFactory: SSpecimenRepresentationFactory<S> by inject(SSpecimenRepresentationFactory::class.java)

    lateinit var costGraph: DGraph

    lateinit var salesmen: Array<DSalesman>

    enum class State {
        CREATED,
        INITIALIZED,
        RESUMED
    }

    /** A futási időre vonatkozó információk. */
    private val timeOf = object {
        var running = 0L
        var resume = 0L
    }


    var state: State by Delegates.observable(State.CREATED)
    { _, oldValue, newValue ->
        when (newValue) {
            State.CREATED ->
                when (oldValue) {
                    State.CREATED -> {
                    }
                    State.INITIALIZED -> {
                        timeOf.running = 0
                        timeOf.resume = 0
                    }
                    State.RESUMED -> throw Exception("Illegal state change! from:$oldValue to:$newValue")
                }
            State.INITIALIZED ->
                when (oldValue) {
                    State.CREATED -> {
                    }
                    State.INITIALIZED -> {
                    }
                    State.RESUMED -> {
                        timeOf.running += System.currentTimeMillis() - timeOf.resume
                    }
                }
            State.RESUMED ->
                when (oldValue) {
                    State.CREATED -> throw Exception("Illegal state change! from:$oldValue to:$newValue")
                    State.INITIALIZED -> timeOf.resume = System.currentTimeMillis()
                    State.RESUMED -> {
                    }
                }
        }
    }
    val runTimeInSeconds: Double
        get() =
            if (state != State.RESUMED)
                timeOf.running / 1000.0
            else
                (System.currentTimeMillis() - timeOf.resume + timeOf.running) / 1000.0

    protected abstract val setup: AAlgorithm4VRPSetup

    fun initialize() = setup.initialize(this)

    fun calculateCostOf(specimen: S) = setup.cost(this, specimen)
    fun costOfEdge(edge: DEdge, salesman: DSalesman) = setup.costOfEdge(edge, salesman)
    fun costOfObjective(objective: DObjective, salesman: DSalesman) = setup.costOfObjective(objective, salesman)
}