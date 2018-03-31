package com.ryansusana.site

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.bson.types.ObjectId
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.marshall.jackson.JacksonMapper
import java.util.*


class BusinessCaseDao(dbServerAddress: String, dbServerPort: Int, dbUsername: String?, dbPassword: String?, dbName: String) {

    private val mongoClient: MongoClient = if (dbUsername != null && dbPassword != null) {
        MongoClient(ServerAddress(dbServerAddress, dbServerPort), Arrays.asList(MongoCredential.createCredential(dbUsername, dbName, dbPassword.toCharArray())))
    } else {
        MongoClient(ServerAddress(dbServerAddress, dbServerPort))
    };

    private val jongo: Jongo = Jongo(mongoClient.getDB(dbName), JacksonMapper.Builder()
            .withObjectIdUpdater(CustomObjectIdUpdater())
            .build());


    private fun getCollection(): MongoCollection {
        return jongo.getCollection("business-cases");
    }

    fun save(businessCase: BusinessCase) {
        getCollection().save(businessCase);
    }

    fun get(id: String): BusinessCase? {
        return getCollection().findOne(ObjectId(id)).`as`(BusinessCase::class.java);
    }

    fun delete(id: String) {
        getCollection().remove(id);
    }

    fun delete(businessCase: BusinessCase) {
        delete(businessCase.id)
    }

}