package hu.raven.puppet.utility.extention

/*
fun List<StepEfficiencyData>.sum(): StepEfficiencyData {
    return stream()
        .map {
            StepEfficiencyData(
                spentTime = it.spentTime,
                spentBudget = it.spentBudget,
                improvementCountPerRun = it.improvementCountPerRun,
                improvementPercentagePerBudget =
                it.improvementPercentagePerBudget * it.spentBudget
            )
        }
        .map {
            Pair(1, it)
        }.reduce { left, right ->
            Pair(
                left.first + right.first,
                StepEfficiencyData(
                    spentTime = left.second.spentTime + right.second.spentTime,
                    spentBudget = left.second.spentBudget + right.second.spentBudget,
                    improvementCountPerRun =
                    left.second.improvementCountPerRun + right.second.improvementCountPerRun,
                    improvementPercentagePerBudget =
                    left.second.improvementPercentagePerBudget + right.second.improvementPercentagePerBudget
                )
            )
        }
        .get()
        .let {
            StepEfficiencyData(
                spentTime = it.second.spentTime,
                spentBudget = it.second.spentBudget,
                improvementCountPerRun = it.second.improvementCountPerRun / it.first,
                improvementPercentagePerBudget =
                it.second.improvementPercentagePerBudget / it.second.spentBudget
            )
        }
}
 */