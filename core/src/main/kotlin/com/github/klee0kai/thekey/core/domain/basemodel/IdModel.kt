package com.github.klee0kai.thekey.core.domain.basemodel

import com.github.klee0kai.hummus.collections.removeDoubles

interface IdModel<IdType> : SameModel {

    val id: IdType

    override fun isSame(obj: SameModel): Boolean {
        return id == (obj as? IdModel<*>)?.id
    }

}

fun <T : IdModel<*>> Iterable<T>.removeDoublesById(): List<T> {
    return removeDoubles { t, t2 -> t.id == t2.id }
}