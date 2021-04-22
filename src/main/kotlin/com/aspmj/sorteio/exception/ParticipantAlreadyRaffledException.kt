package com.aspmj.sorteio.exception

class ParticipantAlreadyRaffledException(name: String) : RaffleException("Participante $name foi sorteado novamente")