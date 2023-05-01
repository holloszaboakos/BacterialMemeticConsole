package hu.raven.puppet.model.solution

interface OnePartRepresentationWithCost :
    OnePartRepresentation, HasCost {
    fun cloneRepresentationAndCost(): OnePartRepresentationWithCost
}