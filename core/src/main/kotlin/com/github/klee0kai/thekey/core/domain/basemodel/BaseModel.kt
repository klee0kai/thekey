package com.github.klee0kai.thekey.core.domain.basemodel

interface BaseModel<idType> :
    IdModel<idType>,
    LoadedModel,
    FilterModel,
    SortableModel
