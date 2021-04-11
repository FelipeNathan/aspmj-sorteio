package com.aspmj.sorteio.controller

import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.NoHandlerFoundException
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class ControllerExceptions {

    @ExceptionHandler(*[HttpRequestMethodNotSupportedException::class, EntityNotFoundException::class])
    fun httpRequestMethodNotSupportedException(): String {
        return RaffleController.REDIRECT_SORTEIOS
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun pageNotFound(): String {
        return "404"
    }
}