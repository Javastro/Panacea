Architecture
============


.. plantuml::

    @startuml
    component [OCI Registry] as OCI
    component [Application Metadata Store] as AMS
    component [Execution Context Store] as ECS
    component [Job Queue] as JQ
    component [Data Processing Node] as DPN

    component Executor {
        component [Data fetcher] as DF
        component [Container Runtime] as CR
        component [Execution Manager] as EM
        component [Result Handler] as RH
        DF --> EM : Fetches input data
        EM --> CR : Mounts input data and executes container
        CR --> EM : Returns execution status and metadata
        EM --> RH : Passes execution results and metadata
    }


    Executor --> OCI : Pulls container images
    Executor --> AMS : Stores execution metadata
    Executor --> ECS : Stores execution context and state
    DPN --> Executor : Executes tasks on processing nodes
    @enduml
