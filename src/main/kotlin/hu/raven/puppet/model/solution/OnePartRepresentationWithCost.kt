package hu.raven.puppet.model.solution

interface OnePartRepresentationWithCost<C : Comparable<C>> :
    OnePartRepresentation, HasCost<C> {
    fun cloneRepresentationAndCost(): OnePartRepresentationWithCost<C>
}