package com.hbb20

interface CCPTalkBackTextProvider {
    fun getTalkBackTextForCountry(country: CCPCountry?): String?
}

internal class InternalTalkBackTextProvider : CCPTalkBackTextProvider {
    override fun getTalkBackTextForCountry(country: CCPCountry?): String? {
        return if (country == null) {
            null
        } else {
            country.name + " phone code is +" + country.phoneCode
        }
    }
}