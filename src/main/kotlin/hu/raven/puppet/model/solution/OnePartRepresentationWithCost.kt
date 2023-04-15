package hu.raven.puppet.model.solution

interface OnePartRepresentationWithCost<C : Comparable<C>, O : OnePartRepresentationWithCost<C, O>> :
    OnePartRepresentation, HasCost<C> {
    fun clone(): O
}