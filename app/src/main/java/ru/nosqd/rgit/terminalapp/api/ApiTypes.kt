package ru.nosqd.rgit.terminalapp.api


data class ProcessCardRequest(val cardToken: String)
data class ProcessCardResponse(val cardId: String, val newToken: String)
data class GetCardReponse(val card: Card)