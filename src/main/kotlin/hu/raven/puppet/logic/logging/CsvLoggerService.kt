package hu.raven.puppet.logic.logging

import hu.raven.puppet.logic.logging.CsvLineBuilder.Companion.buildCsvLine
import java.nio.file.Path

class CsvLoggerService<T : Any>(
    override val outputPath: Path,
    private val headers: Array<CsvHeader<T>>,
) : AlgorithmLoggerService<T> {

    override fun initFile() = outputPath.toFile().run {
        if (!exists()) {
            createNewFile()
        }
        val header = produceHeader()
        writeText(header)
    }

    override fun log(data: T) {
        val line = buildCsvLine {
            headers.forEach {
                it.fieldExtractor(data)
            }
        }
        outputPath.toFile().appendText(line)
    }

    // TODO
    //            appendString("generation")
    //            appendString("timeTotal")
    //            appendString("timeOfIteration")
    //            appendString("fitnessCallCountSoFar")
    //            appendString("fitnessCallCountOfIteration")
    //              ..
    //            appendString("idOfBest")
    //            appendString("costOfBest")
    //              ..
    //            appendString("diversity")
    //            appendString("geneBalance")
    //              ..
    //            appendString("spentTimeOfMutation")
    //            appendString("spentBudgetOfMutation")
    //            appendString("improvementCountPerRunOfMutation")
    //            appendString("improvementPercentagePerBudgetOfMutation")
    private fun produceHeader(): String {
        return buildCsvLine {
            headers.forEach {
                appendString(it.displayText)
            }
        }
    }


}