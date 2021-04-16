package com.aspmj.sorteio.controller

import com.aspmj.sorteio.config.ROLES
import com.aspmj.sorteio.model.FeatureFlag
import com.aspmj.sorteio.service.FeatureFlagService
import com.aspmj.sorteio.service.RaffleService
import com.aspmj.sorteio.vo.RaffleParticipantVO
import com.aspmj.sorteio.vo.RaffleVO
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@Controller
@RequestMapping("/sorteios")
class RaffleController(
    val raffleService: RaffleService,
    val featureFlagService: FeatureFlagService,
) {

    @GetMapping
    fun raffles(model: ModelMap, auth: Authentication): String {

        val isAdmin = auth.authorities.contains(SimpleGrantedAuthority(ROLES.ADMIN.role))
        val canCreateFlag = (featureFlagService.findById(FeatureFlag.FLAGS.CREATE_RAFFLE)?.active ?: false || isAdmin)

        model.addAttribute("raffles", raffleService.loadRaffles())
        model.addAttribute("can_create_raffle", canCreateFlag)

        return INDEX
    }

    @GetMapping("/novo")
    fun new(model: ModelMap): String {
        model.addAttribute("raffle", RaffleVO())
        return NEW_PAGE
    }

    @GetMapping("/editar/{raffleId}")
    fun edit(@PathVariable("raffleId") raffleId: String, model: ModelMap): String {
        model.addAttribute("raffle", raffleService.findRaffle(raffleId))
        return NEW_PAGE
    }

    @PostMapping("/salvar")
    fun save(
        @Valid @ModelAttribute("raffle") vo: RaffleVO,
        bindingResult: BindingResult,
        model: ModelMap
    ): String {

        if (bindingResult.hasErrors()) return NEW_PAGE

        raffleService.saveRaffle(vo)
        return REDIRECT_SORTEIOS
    }

    @RequestMapping("/remover/{raffleId}", method = [RequestMethod.DELETE, RequestMethod.POST])
    fun remover(@PathVariable("raffleId") raffleId: String): String {
        raffleService.delete(raffleId);
        return REDIRECT_SORTEIOS
    }

    @GetMapping("/participar/{raffleId}")
    fun newParticipant(@PathVariable("raffleId") raffleId: String, model: ModelMap): String {

        model.addAttribute("raffle", raffleService.findRaffle(raffleId))
        model.addAttribute("participant", RaffleParticipantVO())

        return RaffleParticipantController.NEW_PAGE
    }

    @GetMapping("/{raffleId}/sortear")
    fun showRaffle(@PathVariable("raffleId") raffleId: String, model: ModelMap): String {
        model.addAttribute("raffle", raffleService.findRaffle(raffleId))
        model.addAttribute("all_winners", raffleService.loadAllRaffled(raffleId))
        return RAFFLE_PAGE
    }

    @PostMapping("/{raffleId}/sortear")
    fun raffleParticipant(@PathVariable("raffleId") raffleId: String, model: ModelMap): String {
        try {
            val winner = raffleService.raffleParticipant(raffleId)
            model.addAttribute("winner", winner)
        } catch (e: Exception) {
            model.addAttribute("error", e.message)
        }

        val raffle = raffleService.findRaffle(raffleId)
        val allWinners = raffleService.loadAllRaffled(raffleId)

        model.addAttribute("raffle", raffle)
        model.addAttribute("all_winners", allWinners)
        return RAFFLE_PAGE
    }

    companion object {
        const val REDIRECT_SORTEIOS = "redirect:/sorteios"
        const val INDEX = "index"
        const val NEW_PAGE = "raffle/new"
        const val RAFFLE_PAGE = "raffle/raffle"
    }
}
