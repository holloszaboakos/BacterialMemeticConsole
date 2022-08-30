package hu.raven.puppet.logic.modules

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCostOfVRPSolutionWithoutCapacity
import hu.raven.puppet.logic.common.steps.calculatecostofedge.CalculateCostOfEdge
import hu.raven.puppet.logic.common.steps.calculatecostofobjective.CalculateCostOfObjective
import hu.raven.puppet.logic.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.evolutionary.common.boost.BoostOnBestLazy
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.evolutionary.common.boostoperator.Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit
import hu.raven.puppet.logic.evolutionary.common.diversity.Diversity
import hu.raven.puppet.logic.evolutionary.common.diversity.DiversityByInnerDistanceAndSequence
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.logic.evolutionary.common.initializePopulation.InitializePopulationByModuloStepper
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.factory.OnePartRepresentationFactory
import hu.raven.puppet.logic.specimen.factory.SSpecimenRepresentationFactory
import org.koin.dsl.module
import java.io.File

val commonModule = module {
    single {
        DoubleLogger(File(""))
    }

    factory<Diversity<*>> {
        DiversityByInnerDistanceAndSequence<DOnePartRepresentation>(
            algorithm = get()
        )
    }

    factory<SSpecimenRepresentationFactory<*>> {
        OnePartRepresentationFactory()
    }

    factory<InitializePopulation<*>> {
        InitializePopulationByModuloStepper<DOnePartRepresentation>(
            algorithm = get()
        )
    }

    factory {
        OrderPopulationByCost<DOnePartRepresentation>(
            algorithm = get()
        )
    }
    factory<CalculateCost<*>> {
        CalculateCostOfVRPSolutionWithoutCapacity<DOnePartRepresentation>(
            algorithm = get()
        )
    }
    factory { CalculateCostOfEdge() }
    factory { CalculateCostOfObjective() }

    factory<Boost<*>> {
        BoostOnBestLazy<DOnePartRepresentation>(
            algorithm = get()
        )
    }
    factory<BoostOperator<*>> {
        Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<DOnePartRepresentation>(
            algorithm = get(),
            stepLimit = 2000
        )
    }


}