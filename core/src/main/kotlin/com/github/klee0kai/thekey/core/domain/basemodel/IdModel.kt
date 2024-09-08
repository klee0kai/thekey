package com.github.klee0kai.thekey.core.domain.basemodel

interface IdModel<IdType> : SameModel {

    val id: IdType

    override fun isSame(obj: SameModel): Boolean {
        return id == (obj as? IdModel<*>)?.id
    }

}