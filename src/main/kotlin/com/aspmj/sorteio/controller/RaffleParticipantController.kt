package com.aspmj.sorteio.controller

import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.service.RaffleParticipantService
import com.aspmj.sorteio.service.RaffleService
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Controller
@RequestMapping("/participantes")
class RaffleParticipantController(
    val raffleService: RaffleService,
    val raffleParticipantService: RaffleParticipantService
) {

    @PostMapping("/novo")
    fun create(
        @Valid @ModelAttribute("participant") participant: RaffleParticipantVO,
        bindingResult: BindingResult,
        model: ModelMap
    ): String {

        try {
            model.addAttribute("raffle", raffleService.findRaffle(participant.raffleId!!))

            if (bindingResult.hasErrors()) {
                return NEW_PAGE
            }

            val newParticipant = raffleParticipantService.sendParticipantToQueue(participant)
            model.addAttribute("participant_number", newParticipant?.number)

        } catch (e: Exception) {

            when (e) {
                is DateLimitExceedException,
                is ParticipantAlreadyExistsException -> {
                    model.addAttribute("error_title", "Que pena!")
                }
                is DateLimitStillNotBeginException -> model.addAttribute("error_title", "Atenção!")
                else -> {
                    model.addAttribute("error_title", "Ops!")
                    model.addAttribute("error", "Houve uma falha no cadastro, por favor tente novamente!")
                    LOG.error(e.message, e)
                }
            }

            if (!model.containsAttribute("error"))
                model.addAttribute("error", e.message)
        }

        return CREATED_PAGE
    }

    @GetMapping("/{id}")
    fun view(@PathVariable("id") id: Long, model: ModelMap): String {
        model.addAttribute("participant", raffleParticipantService.findById(id))
        model.addAttribute("viewing", true)
        return NEW_PAGE
    }

    companion object {
        const val NEW_PAGE = "participant/new"
        const val CREATED_PAGE = "participant/created"
        val LOG: Logger = LoggerFactory.getLogger(RaffleParticipantController::class.java)
    }
}