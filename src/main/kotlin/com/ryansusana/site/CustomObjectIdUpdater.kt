package com.ryansusana.site

import org.bson.types.ObjectId
import org.jongo.ObjectIdUpdater

//Because Jongo is as of now broken
class CustomObjectIdUpdater : ObjectIdUpdater {


    override fun mustGenerateObjectId(pojo: Any): Boolean {
        val obj = translate(pojo)
        return obj.id.trim().isEmpty()
    }

    override fun getId(pojo: Any): Any {
        return translate(pojo).id
    }

    override fun setObjectId(target: Any, id: ObjectId) {
        val pojo = translate(target)
        pojo.id = id.toString()
    }

    private fun translate(pojo: Any): Identifiable {
        if (pojo !is Identifiable) {
            throw IllegalArgumentException("Cannot identify: " + pojo.javaClass.name)
        }

        return pojo
    }
}