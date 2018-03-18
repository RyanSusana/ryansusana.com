@file:Suppress("UNUSED_EXPRESSION")

package com.ryansusana.site

import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.MailerBuilder
import org.simplejavamail.mailer.config.TransportStrategy
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark
import spark.Spark.exception
import spark.kotlin.get
import spark.kotlin.post
import spark.template.pebble.PebbleTemplateEngine


var mailer: Mailer = MailerBuilder
        .withSMTPServer("smtp.gmail.com", 465, System.getenv("GMAIL_USERNAME"), System.getenv("GMAIL_PASSWORD"))
        .withTransportStrategy(TransportStrategy.SMTPS)
        .withSessionTimeout(10 * 1000)
        .withDebugLogging(true)
        .buildMailer()

fun main(args: Array<String>) {
    Spark.port(8091)
    Spark.staticFiles.location("public")

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

    exception(Exception().javaClass) { exception: Exception, request: Request, response: Response ->
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