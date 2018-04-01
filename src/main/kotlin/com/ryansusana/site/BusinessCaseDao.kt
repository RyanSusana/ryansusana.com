package com.ryansusana.site

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.bson.types.ObjectId
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.marshall.jackson.JacksonMapper
import java.util.*
import kotlin.collections.ArrayList


class BusinessCaseDao(dbServerAddress: String, dbServerPort: Int, dbUsername: String?, dbPassword: String?, dbName: String) {

    private val mongoClient: MongoClient = if (dbUsername != null && dbPassword != null) {
        MongoClient(ServerAddress(dbServerAddress, dbServerPort), Arrays.asList(MongoCredential.createCredential(dbUsername, dbName, dbPassword.toCharArray())))
    } else {
        MongoClient(ServerAddress(dbServerAddress, dbServerPort))
    };

    private val jongo: Jongo = Jongo(mongoClient.getDB(dbName), JacksonMapper.Builder().registerModule(KotlinModule())
            .withObjectIdUpdater(CustomObjectIdUpdater())
            .build());


    private fun getCollection(): MongoCollection {
        return jongo.getCollection("business-cases");
    }

    fun save(businessCase: BusinessCase) {
        getCollection().save(businessCase);
    }

    fun get(id: String): BusinessCase? {
        return getCollection().findOne("{_id: #}", id).`as`(BusinessCase::class.java);
    }

    fun getAll(): Collection<BusinessCase> {
        val cases = ArrayList<BusinessCase>()

        getCollection().find().`as`(BusinessCase::class.java).iterator().forEachRemaining({ bc -> run { cases.add(bc) } })
        return cases
    }

    fun delete(id: String) {
        getCollection().remove("{_id: #}", id);
    }

    fun delete(businessCase: BusinessCase) {
        delete(businessCase.id)
    }

}