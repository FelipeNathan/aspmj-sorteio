package com.aspmj.sorteio.controller

import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBegin
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.service.RaffleService
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Controller
@RequestMapping("/participantes")
class RaffleParticipantController(
    val raffleService: RaffleService,
) {

    @PostMapping("/novo")
    fun create(
        @Valid @ModelAttribute("participant") participant: RaffleParticipantVO,
        bindingResult: BindingResult,
        model: ModelMap
    ): String {

        model.addAttribute("raffle", raffleService.findRaffle(participant.raffleId!!))

        if (bindingResult.hasErrors()) {
            return NEW_PAGE
        }

        try {
            val newParticipant = raffleService.addParticipantToRaffle(participant)
            model.addAttribute("participant_number", newParticipant.id)
        } catch (e: Exception) {

            when (e) {
                is DateLimitExceedException,
                is ParticipantAlreadyExistsException -> {
                    model.addAttribute("error_title", "Que pena!")
                }
                is DateLimitStillNotBegin -> model.addAttribute("error_title", "Atenção!")
            }

            model.addAttribute("error", e.message)
        }

        return CREATED_PAGE
    }

    companion object {
        const val NEW_PAGE = "participant/new"
        const val CREATED_PAGE = "participant/created"
    }
}