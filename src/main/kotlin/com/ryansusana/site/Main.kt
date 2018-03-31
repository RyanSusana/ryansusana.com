@file:Suppress("UNUSED_EXPRESSION")

package com.ryansusana.site

import com.google.gson.Gson
import org.bson.types.ObjectId
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.MailerBuilder
import org.simplejavamail.mailer.config.TransportStrategy
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark
import spark.Spark.exception
import spark.Spark.halt
import spark.kotlin.before
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post
import spark.template.pebble.PebbleTemplateEngine


val mailer: Mailer = MailerBuilder
        .withSMTPServer("smtp.gmail.com", 465, System.getenv("GMAIL_USERNAME"), System.getenv("GMAIL_PASSWORD"))
        .withTransportStrategy(TransportStrategy.SMTPS)
        .withSessionTimeout(10 * 1000)
        .withDebugLogging(true)
        .buildMailer()

val UNLOCK_CODE = System.getenv("ryan_key")

val businessCaseDao: BusinessCaseDao = BusinessCaseDao(System.getenv("database_server_address"), 27017, System.getenv("database_username"), System.getenv("database_password"), System.getenv("database_name"))


fun main(args: Array<String>) {
    if(UNLOCK_CODE == null){
        System.exit(0)
    }
    Spark.port(8091)
    Spark.staticFiles.location("public")

    before{
        if(!requestMethod().toUpperCase().equals("GET") && uri().contains("/admin")){
            val key: String? = request.headers("ryan_key");
            if(key == null || !key.equals(UNLOCK_CODE)){
                halt(401,"Stop right there bro, you don't belong here!")
            }
        }
    }

    get("/") {
        render(HashMap(), "index.html");
    }

    post("/send-mail") {
        val name: String? = request.queryParams("name");
        val message: String? = request.queryParams("message");
        val email: String? = request.queryParams("email");
        if (name.isNullOrEmpty() || email.isNullOrEmpty() || message.isNullOrBlank()) {
            response.status(400)
            "Please make sure that you've properly filled out the form"
        } else {
            response.status(202)
            val message: String = sendMail(name.orEmpty(), email.orEmpty(), message.orEmpty(), request.ip());
            message
        }
    }

    post("/admin/bc") {
        val businessCase: BusinessCase = Gson().fromJson(request.body(), BusinessCase::class.java)
        businessCaseDao.save(businessCase)
        "Successfully inserted/updated business case";
    }

    delete("/admin/bc/:id") {
        val businessCase = businessCaseDao.get(params("id"))
        if (businessCase != null) {
            businessCaseDao.delete(businessCase)
            status(201)
            "Successfully deleted business case."
        } else {
            status(404)
            "Business case doesn't exist"
        }
    }

    exception(Exception::class.java) { exception: Exception, request: Request, response: Response ->
        exception.printStackTrace()
        response.status(500)
        response.body("Internal Server Error")
    }
}

fun sendMail(name: String, email: String, message: String, ip: String): String {
    val email = EmailBuilder.startingBlank()
            .from("Ryansusana.com", System.getenv("GMAIL_USERNAME"))
            .to("Ryan Susana", "ryansusana@live.com")
            .withSubject("New Message from $name at ryansusana.com!")
            .withPlainText("Their email: $email \n" +
                    "Message: $message \n" +
                    "Their IP: $ip")
            .buildEmail()

    Thread { mailer.sendMail(email) }.start();


    val firstName = name.split(" ").get(0)
    return "Thanks for the message, $firstName! Please be patient as I brew up a response."
}

fun render(model: Map<String, Any>, template: String): String {
    return PebbleTemplateEngine().render(ModelAndView(model, template))
}