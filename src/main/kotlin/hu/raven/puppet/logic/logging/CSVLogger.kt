package hu.raven.puppet.logic.logging

import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.appendDuration
import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.appendField
import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.appendString
import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.buildCsvLine
import hu.raven.puppet.model.logging.*
import hu.raven.puppet.model.physics.PhysicsUnit
import java.io.File

class CSVLogger : AlgorithmLogger() {

    fun printHeader() {
        val targetFile = File("$outputFolderPath\\$targetFileName.csv")
        val header = produceHeader()
        targetFile.writeText(header)
    }

    private fun produceHeader(): String {
        return buildCsvLine {
            appendString("generation")
            appendString("timeTotal")
            appendString("timeOfIteration")
            appendString("fitnessCallCountSoFar")
            appendString("fitnessCallCountOfIteration")

            appendString("idOfBest")
            appendString("costOfBest")

            appendString("idOfSecond")
            appendString("costOfSecond")

            appendString("idOfThird")
            appendString("costOfThird")

            appendString("idOfWorst")
            appendString("costOfWorst")

            appendString("idOfMedian")
            appendString("costOfMedian")

            appendString("diversity")
            appendString("geneBalance")

            appendString("spentTimeOfMutation")
            appendString("spentBudgetOfMutation")
            appendString("improvementCountPerRunOfMutation")
            appendString("improvementPercentagePerBudgetOfMutation")

            appendString("spentTimeOfMutationOnBest")
            appendString("spentBudgetOfMutationOnBest")
            appendString("improvementCountPerRunOfMutationOnBest")
            appendString("improvementPercentagePerBudgetOfMutationOnBest")

            appendString("spentTimeOfGeneTransfer")
            appendString("spentBudgetOfGeneTransfer")
            appendString("improvementCountPerRunOfGeneTransfer")
            appendString("improvementPercentagePerBudgetOfGeneTransfer")


            appendString("spentTimeOfBoost")
            appendString("spentBudgetOfBoost")
            appendString("improvementCountPerRunOfBoost")
            appendString("improvementPercentagePerBudgetOfBoost")

            appendString("spentTimeOfBoostOnBest")
            appendString("spentBudgetOfBoostOnBest")
            appendString("improvementCountPerRunOfBoostOnBest")
            appendString("improvementPercentagePerBudgetOfBoostOnBest")
        }
    }

    operator fun <C : PhysicsUnit<C>> invoke(message: BacterialMemeticAlgorithmLogLine<C>) {
        val targetFile = File("$outputFolderPath\\$targetFileName.csv")
        val line = toCsvLine(message)
        targetFile.appendText(line)
    }

    private fun toCsvLine(message: BacterialMemeticAlgorithmLogLine<*>): String {
        return buildCsvLine {
            appendProgressData(message.progressData)
            appendPopulationData(message.populationData)

            appendStepEfficiencyData(message.mutationImprovement)
            appendStepEfficiencyData(message.mutationOnBestImprovement)
            appendStepEfficiencyData(message.geneTransferImprovement)
            appendStepEfficiencyData(message.boostImprovement)
            appendStepEfficiencyData(message.boostOnBestImprovement)
        }
    }

    private fun StringBuilder.appendProgressData(progressData: ProgressData) {
        appendField(progressData.generation)
        appendDuration(progressData.spentTimeTotal)
        appendDuration(progressData.spentTimeOfGeneration)
        appendField(progressData.spentBudgetTotal)
        appendField(progressData.spentBudgetOfGeneration)
    }

    private fun StringBuilder.appendPopulationData(populationData: PopulationData<*>) {
        appendSpecimenData(populationData.best)
        appendSpecimenData(populationData.second)
        appendSpecimenData(populationData.third)
        appendSpecimenData(populationData.worst)
        appendSpecimenData(populationData.median)

        appendField(populationData.diversity)
        appendField(populationData.geneBalance)
    }

    private fun StringBuilder.appendSpecimenData(specimenData: SpecimenData<*>?) {
        appendField(specimenData?.id)
        appendField(specimenData?.cost)
    }

    private fun StringBuilder.appendStepEfficiencyData(stepEfficiencyData: StepEfficiencyData) {
        appendField(stepEfficiencyData.spentTime)
        appendField(stepEfficiencyData.spentBudget)

        appendField(stepEfficiencyData.improvementCountPerRun)
        appendField(stepEfficiencyData.improvementPercentagePerBudget)
    }


}