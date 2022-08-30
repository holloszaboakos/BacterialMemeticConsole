package hu.raven.puppet.logic.statistics

class BacterialAlgorithmStatistics : AlgorithmStatistics {
    var fitnessCallCount: Long = 0
    var mutationStepCall: Long = 0
    var mutationCall: Long = 0
    var mutationCycleCall: Long = 0
    var mutationOperatorCall: Long = 0
    var mutationImprovementCountOnBest: Long = 0
    var mutationImprovementCountOnAll: Long = 0
    override var diversity: Double = Double.MAX_VALUE
}