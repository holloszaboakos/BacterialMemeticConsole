package hu.raven.puppet.modules.dataset

import org.koin.dsl.module

val defaultDataSetModule = module {
    /*
    single(named(INPUT_FOLDER)) { "input" }
    single(named(GRAPH_FILE)) { "hungarianExample/graph.json" }
    single(named(EDGES_BETWEEN_FILE)) { "hungarianExample/between.json" }
    single(named(EDGES_FROM_CENTER_FILE)) { "hungarianExample/fromCenter.json" }
    single(named(EDGES_TO_CENTER_FILE)) { "hungarianExample/toCenter.json" }
    single(named(OBJECTIVES_FILE)) { "hungarianExample/objectives.json" }
    single(named(SALESMAN_FILE)) { "hungarianExample/salesman.json" }
    single<TaskLoader> { DefaultTaskLoader() }
    factory<CalculateCost<*, *>> {
        CalculateCostOfACVRPWithMultipleCapacity<OnePartRepresentation<Euro>>(
            get(), get(), get(), get()
        )
    }

     */
}