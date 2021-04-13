package com.aspmj.sorteio.exception

class ParticipantAlreadyRaffledException(name: String) : Exception("Participante $name foi sorteado novamente")