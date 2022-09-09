package hu.raven.puppet.logic.step.common.logging

import hu.raven.puppet.logic.step.common.logging.CSVLineBuilderUtility.appendDuration
import hu.raven.puppet.logic.step.common.logging.CSVLineBuilderUtility.appendField
import hu.raven.puppet.logic.step.common.logging.CSVLineBuilderUtility.appendString
import hu.raven.puppet.logic.step.common.logging.CSVLineBuilderUtility.buildCsvLine
import hu.raven.puppet.model.logging.*
import java.io.File

class CSVLogger {
    var targetFile: File? = null
        set(value) {
            field = value
            targetFile?.writeText("${produceHeader()}\n")
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

            appendString("operatorCallCountOfMutation")
            appendString("spentBudgetOfMutation")
            appendString("improvementCountTotalOfMutation")
            appendString("improvementCountPerCallOfMutation")
            appendString("improvementAmountTotalOfMutation")
            appendString("improvementAmountPerBudgetSpentOfMutation")

            appendString("operatorCallCountOfMutationOnBest")
            appendString("spentBudgetOfMutationOnBest")
            appendString("improvementCountTotalOfMutationOnBest")
            appendString("improvementCountPerCallOfMutationOnBest")
            appendString("improvementAmountTotalOfMutationOnBest")
            appendString("improvementAmountPerBudgetSpentOfMutationOnBest")


            appendString("operatorCallCountOfGeneTransfer")
            appendString("spentBudgetOfGeneTransfer")
            appendString("improvementCountTotalOfGeneTransfer")
            appendString("improvementCountPerCallOfGeneTransfer")
            appendString("improvementAmountTotalOfGeneTransfer")
            appendString("improvementAmountPerBudgetSpentOfGeneTransfer")


            appendString("operatorCallCountOfBoost")
            appendString("spentBudgetOfBoost")
            appendString("improvementCountTotalOfBoost")
            appendString("improvementCountPerCallOfBoost")
            appendString("improvementAmountTotalOfBoost")
            appendString("improvementAmountPerBudgetSpentOfBoost")

            appendString("operatorCallCountOfBoostOnBest")
            appendString("spentBudgetOfBoostOnBest")
            appendString("improvementCountTotalOfBoostOnBest")
            appendString("improvementCountPerCallOfBoostOnBest")
            appendString("improvementAmountTotalOfBoostOnBest")
            appendString("improvementAmountPerBudgetSpentOfBoostOnBest")
        }
    }

    operator fun invoke(message: BacterialMemeticAlgorithmLogLine) {
        val line = toCsvLine(message)
        targetFile?.appendText("$line\n")
    }

    private fun toCsvLine(message: BacterialMemeticAlgorithmLogLine): String {
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

    private fun StringBuilder.appendPopulationData(populationData: PopulationData) {
        appendSpecimenData(populationData.best)
        appendSpecimenData(populationData.second)
        appendSpecimenData(populationData.third)
        appendSpecimenData(populationData.worst)
        appendSpecimenData(populationData.median)

        appendField(populationData.diversity)
        appendField(populationData.geneBalance)
    }

    private fun StringBuilder.appendSpecimenData(specimenData: SpecimenData?) {
        appendField(specimenData?.id)
        appendField(specimenData?.cost)
    }

    private fun StringBuilder.appendStepEfficiencyData(stepEfficiencyData: StepEfficiencyData) {
        appendField(stepEfficiencyData.spentTime)
        appendField(stepEfficiencyData.spentBudget)

        appendField(stepEfficiencyData.spentTime)
        appendField(stepEfficiencyData.improvementPercentagePerBudget)
    }


}