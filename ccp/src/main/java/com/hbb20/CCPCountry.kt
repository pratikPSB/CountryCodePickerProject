package com.hbb20

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.text.Collator
import java.util.Locale


/**
 * Created by hbb20 on 11/1/16.
 */
class CCPCountry : Comparable<CCPCountry> {
    var nameCode: String? = null
    var phoneCode: String? = null
    var name: String? = null
    var englishName: String? = null

    constructor()
    constructor(nameCode: String, phoneCode: String?, name: String?) {
        this.nameCode = nameCode.uppercase()
        this.phoneCode = phoneCode
        this.name = name
    }

    fun log() {
        try {
            Log.d(TAG, "Country->$nameCode:$phoneCode:$name")
        } catch (ex: NullPointerException) {
            Log.d(TAG, "Null")
        }
    }

    fun logString(): String {
        return nameCode!!.uppercase(Locale.getDefault()) + " +" + phoneCode + "(" + name + ")"
    }

    /**
     * If country have query word in name or name code or phone code, this will return true.
     *
     * @param query
     * @return
     */
    fun isEligibleForQuery(query: String): Boolean {
        var query = query
        query = query.lowercase(Locale.getDefault())
        return containsQueryWord("Name", name, query) ||
                containsQueryWord("NameCode", nameCode, query) ||
                containsQueryWord("PhoneCode", phoneCode, query) ||
                containsQueryWord("EnglishName", englishName, query)
    }

    private fun containsQueryWord(fieldName: String, fieldValue: String?, query: String?): Boolean {
        return try {
            if (fieldValue == null || query == null) {
                false
            } else {
                fieldValue.lowercase().contains(query)
            }
        } catch (e: Exception) {
            Log.w(
                "CCPCountry", fieldName + ":" + fieldValue +
                        " failed to execute toLowerCase(Locale.ROOT).contains(query) " +
                        "for query:" + query
            )
            false
        }
    }

    override fun compareTo(other: CCPCountry): Int {
        return Collator.getInstance().compare(name, other.name)
    }

    companion object {
        var DEFAULT_FLAG_RES = -99
        var TAG = "Class Country"
        var loadedLibraryMasterListLanguage: CountryCodePicker.Language? = null
        private var dialogTitle: String? = null
        private var searchHintMessage: String? = null
        private var noResultFoundAckMessage: String? = null
        var loadedLibraryMaterList: MutableList<CCPCountry?>? = null

        private var context: Context? = null

        //countries with +1
        private const val ANTIGUA_AND_BARBUDA_AREA_CODES = "268"
        private const val ANGUILLA_AREA_CODES = "264"
        private const val BARBADOS_AREA_CODES = "246"
        private const val BERMUDA_AREA_CODES = "441"
        private const val BAHAMAS_AREA_CODES = "242"
        private const val CANADA_AREA_CODES = "204/226/236/249/250/289/306/343/365/403/416/418/431/437/438/450/506/514/519/579/581/587/600/604/613/639/647/705/709/769/778/780/782/807/819/825/867/873/902/905/"
        private const val DOMINICA_AREA_CODES = "767"
        private const val DOMINICAN_REPUBLIC_AREA_CODES = "809/829/849"
        private const val GRENADA_AREA_CODES = "473"
        private const val JAMAICA_AREA_CODES = "876"
        private const val SAINT_KITTS_AND_NEVIS_AREA_CODES = "869"
        private const val CAYMAN_ISLANDS_AREA_CODES = "345"
        private const val SAINT_LUCIA_AREA_CODES = "758"
        private const val MONTSERRAT_AREA_CODES = "664"
        private const val PUERTO_RICO_AREA_CODES = "787"
        private const val SINT_MAARTEN_AREA_CODES = "721"
        private const val TURKS_AND_CAICOS_ISLANDS_AREA_CODES = "649"
        private const val TRINIDAD_AND_TOBAGO_AREA_CODES = "868"
        private const val SAINT_VINCENT_AND_THE_GRENADINES_AREA_CODES = "784"
        private const val BRITISH_VIRGIN_ISLANDS_AREA_CODES = "284"
        private const val US_VIRGIN_ISLANDS_AREA_CODES = "340"

        //countries with +44
        private const val ISLE_OF_MAN = "1624"

        /**
         * This function parses the raw/countries.xml file, and get list of all the countries.
         *
         * @param context: required to access application resources (where country.xml is).
         * @return List of all the countries available in xml file.
         */
        fun loadDataFromXML(context: Context?, language: CountryCodePicker.Language?) {
            Companion.context = context
            var countries: MutableList<CCPCountry?> = ArrayList()
            var tempDialogTitle = ""
            var tempSearchHint = ""
            var tempNoResultAck = ""
            try {
                val xmlFactoryObject = XmlPullParserFactory.newInstance()
                val xmlPullParser = xmlFactoryObject.newPullParser()
                val ins = context!!.resources.openRawResource(
                    context.resources
                        .getIdentifier(
                            "ccp_" + language.toString().lowercase(),
                            "raw", context.packageName
                        )
                )
                xmlPullParser.setInput(ins, "UTF-8")
                var event = xmlPullParser.eventType
                while (event != XmlPullParser.END_DOCUMENT) {
                    val name = xmlPullParser.name
                    when (event) {
                        XmlPullParser.START_TAG -> {}
                        XmlPullParser.END_TAG -> when (name) {
                            "country" -> {
                                val ccpCountry = CCPCountry()
                                ccpCountry.nameCode = xmlPullParser.getAttributeValue(null, "name_code")
                                    .uppercase(Locale.getDefault())
                                ccpCountry.phoneCode = xmlPullParser.getAttributeValue(
                                    null,
                                    "phone_code"
                                )
                                ccpCountry.englishName = xmlPullParser.getAttributeValue(
                                    null,
                                    "english_name"
                                )
                                ccpCountry.name = xmlPullParser.getAttributeValue(null, "name")
                                countries.add(ccpCountry)
                            }
                            "ccp_dialog_title" -> {
                                tempDialogTitle = xmlPullParser.getAttributeValue(null, "translation")
                            }
                            "ccp_dialog_search_hint_message" -> {
                                tempSearchHint = xmlPullParser.getAttributeValue(null, "translation")
                            }
                            "ccp_dialog_no_result_ack_message" -> {
                                tempNoResultAck = xmlPullParser.getAttributeValue(null, "translation")
                            }
                        }
                    }
                    event = xmlPullParser.next()
                }
                loadedLibraryMasterListLanguage = language
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
            }

            //if anything went wrong, countries will be loaded for english language
            if (countries.isEmpty()) {
                loadedLibraryMasterListLanguage = CountryCodePicker.Language.ENGLISH
                countries = libraryMasterCountriesEnglish
            }
            dialogTitle = tempDialogTitle.ifEmpty { "Select Country Code" }
            searchHintMessage = tempSearchHint.ifEmpty { "Search Country" }
            noResultFoundAckMessage = tempNoResultAck.ifEmpty { "Results not found" }
            loadedLibraryMaterList = countries

            // sort list
            loadedLibraryMaterList!!.sortedBy { it!!.nameCode }
        }

        fun getDialogTitle(context: Context?, language: CountryCodePicker.Language?): String? {
            Companion.context = context
            if (loadedLibraryMasterListLanguage == null || loadedLibraryMasterListLanguage != language || dialogTitle == null || dialogTitle!!.isEmpty()) {
                loadDataFromXML(context, language)
            }
            return dialogTitle
        }

        fun getSearchHintMessage(
            context: Context?,
            language: CountryCodePicker.Language?
        ): String? {
            Companion.context = context
            if (loadedLibraryMasterListLanguage == null || loadedLibraryMasterListLanguage != language || searchHintMessage == null || searchHintMessage!!.isEmpty()) {
                loadDataFromXML(context, language)
            }
            return searchHintMessage
        }

        fun getNoResultFoundAckMessage(
            context: Context?,
            language: CountryCodePicker.Language?
        ): String? {
            Companion.context = context
            if (loadedLibraryMasterListLanguage == null || loadedLibraryMasterListLanguage != language || noResultFoundAckMessage == null || noResultFoundAckMessage!!.isEmpty()) {
                loadDataFromXML(context, language)
            }
            return noResultFoundAckMessage
        }

        fun setDialogTitle(dialogTitle: String?) {
            Companion.dialogTitle = dialogTitle
        }

        fun setSearchHintMessage(searchHintMessage: String?) {
            Companion.searchHintMessage = searchHintMessage
        }

        fun setNoResultFoundAckMessage(noResultFoundAckMessage: String?) {
            Companion.noResultFoundAckMessage = noResultFoundAckMessage
        }

        /**
         * Search a country which matches @param code.
         *
         * @param context
         * @param preferredCountries is list of preference countries.
         * @param code               phone code. i.e "91" or "1"
         * @return Country that has phone code as @param code.
         * or returns null if no country matches given code.
         * if same code (e.g. +1) available for more than one country ( US, canada) , this function will return preferred country.
         */
        fun getCountryForCode(
            context: Context?,
            language: CountryCodePicker.Language?,
            preferredCountries: List<CCPCountry?>?,
            code: String
        ): CCPCountry? {
            /**
             * check in preferred countries
             */
            Companion.context = context
            if (preferredCountries != null && !preferredCountries.isEmpty()) {
                for (CCPCountry in preferredCountries) {
                    if (CCPCountry!!.phoneCode == code) {
                        return CCPCountry
                    }
                }
            }
            for (CCPCountry in getLibraryMasterCountryList(context, language)!!) {
                if (CCPCountry!!.phoneCode == code) {
                    return CCPCountry
                }
            }
            return null
        }

        /**
         * @param code phone code. i.e "91" or "1"
         * @return Country that has phone code as @param code.
         * or returns null if no country matches given code.
         * if same code (e.g. +1) available for more than one country ( US, canada) , this function will return preferred country.
         * @avoid Search a country which matches @param code. This method is just to support correct preview
         */
        fun getCountryForCodeFromEnglishList(code: String): CCPCountry? {
            val countries: List<CCPCountry?>
            countries = libraryMasterCountriesEnglish
            for (ccpCountry in countries) {
                if (ccpCountry!!.phoneCode == code) {
                    return ccpCountry
                }
            }
            return null
        }

        fun getCustomMasterCountryList(
            context: Context?,
            codePicker: CountryCodePicker?
        ): List<CCPCountry?>? {
            Companion.context = context
            codePicker!!.refreshCustomMasterList()
            return if (codePicker.customMasterCountriesList != null && codePicker.customMasterCountriesList!!.size > 0) {
                codePicker.customMasterCountriesList
            } else {
                getLibraryMasterCountryList(context, codePicker.getLanguageToApply())
            }
        }

        /**
         * Search a country which matches @param nameCode.
         *
         * @param context
         * @param customMasterCountriesList
         * @param nameCode                  country name code. i.e US or us or Au. See countries.xml for all code names.
         * @return Country that has country name code as @param code.
         */
        fun getCountryForNameCodeFromCustomMasterList(
            context: Context?,
            customMasterCountriesList: List<CCPCountry?>?,
            language: CountryCodePicker.Language?,
            nameCode: String?
        ): CCPCountry? {
            Companion.context = context
            if (customMasterCountriesList == null || customMasterCountriesList.size == 0) {
                return getCountryForNameCodeFromLibraryMasterList(context, language, nameCode)
            } else {
                for (ccpCountry in customMasterCountriesList) {
                    if (ccpCountry!!.nameCode.equals(nameCode, ignoreCase = true)) {
                        return ccpCountry
                    }
                }
            }
            return null
        }

        /**
         * Search a country which matches @param nameCode.
         *
         * @param context
         * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
         * @return Country that has country name code as @param code.
         * or returns null if no country matches given code.
         */
        fun getCountryForNameCodeFromLibraryMasterList(
            context: Context?,
            language: CountryCodePicker.Language?,
            nameCode: String?
        ): CCPCountry? {
            Companion.context = context
            val countries: List<CCPCountry?>? = getLibraryMasterCountryList(context, language)
            for (ccpCountry in countries!!) {
                if (ccpCountry!!.nameCode.equals(nameCode, ignoreCase = true)) {
                    return ccpCountry
                }
            }
            return null
        }

        /**
         * Search a country which matches @param nameCode.
         * This searches through local english name list. This should be used only for the preview purpose.
         *
         * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
         * @return Country that has country name code as @param code.
         * or returns null if no country matches given code.
         */
        fun getCountryForNameCodeFromEnglishList(nameCode: String?): CCPCountry? {
            val countries = libraryMasterCountriesEnglish
            for (CCPCountry in countries) {
                if (CCPCountry!!.nameCode.equals(nameCode, ignoreCase = true)) {
                    return CCPCountry
                }
            }
            return null
        }

        /**
         * Search a country which matches @param code.
         *
         * @param context
         * @param preferredCountries list of country with priority,
         * @param code               phone code. i.e 91 or 1
         * @return Country that has phone code as @param code.
         * or returns null if no country matches given code.
         */
        fun getCountryForCode(
            context: Context?,
            language: CountryCodePicker.Language?,
            preferredCountries: List<CCPCountry?>?,
            code: Int
        ): CCPCountry? {
            Companion.context = context
            return getCountryForCode(context, language, preferredCountries, code.toString() + "")
        }

        /**
         * Finds country code by matching substring from left to right from full number.
         * For example. if full number is +819017901357
         * function will ignore "+" and try to find match for first character "8"
         * if any country found for code "8", will return that country. If not, then it will
         * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
         *
         * @param context
         * @param preferredCountries countries of preference
         * @param fullNumber         full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
         * @return Country JP +81(Japan) for +819017901357 or 819017901357
         * Country IN +91(India) for  918866667722
         * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
         */
        fun getCountryForNumber(
            context: Context?,
            language: CountryCodePicker.Language?,
            preferredCountries: List<CCPCountry?>?,
            fullNumber: String?
        ): CCPCountry? {
            Companion.context = context
            var fullNumber = fullNumber
            val firstDigit: Int
            fullNumber = (fullNumber?.trim { it <= ' ' } ?: return null)
            if (fullNumber.isNotEmpty()) {
                firstDigit = if (fullNumber[0] == '+') {
                    1
                } else {
                    0
                }
                var ccpCountry: CCPCountry? = null
                for (i in firstDigit..fullNumber.length) {
                    val code = fullNumber.substring(firstDigit, i)
                    /*var countryGroup: CCPCountryGroup? = null
                    try {
                        countryGroup = CCPCountryGroup.getCountryGroupForPhoneCode(code.toInt())
                    } catch (ignored: Exception) {
                        Log.e(TAG, "getCountryForNumber: ${ignored.localizedMessage}")
                    }
                    if (countryGroup != null) {
                        val areaCodeStartsAt = firstDigit + code.length
                        //when phone number covers area code too.
                        return if (fullNumber.length >= areaCodeStartsAt + countryGroup.areaCodeLength) {
                            val areaCode = fullNumber.substring(areaCodeStartsAt, areaCodeStartsAt + countryGroup.areaCodeLength)
                            countryGroup.getCountryForAreaCode(context, language, areaCode)
                        } else {
                            getCountryForNameCodeFromLibraryMasterList(context, language, countryGroup.defaultNameCode)
                        }
                    } else {*/
                    ccpCountry = getCountryForCode(context, language, preferredCountries, code)
                    if (ccpCountry != null) {
                        return ccpCountry
                    }
//                    }
                }
            }
            //it reaches here means, phone number has some problem.
            return null
        }

        /**
         * Finds country code by matching substring from left to right from full number.
         * For example. if full number is +819017901357
         * function will ignore "+" and try to find match for first character "8"
         * if any country found for code "8", will return that country. If not, then it will
         * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
         *
         * @param context
         * @param fullNumber full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
         * @return Country JP +81(Japan) for +819017901357 or 819017901357
         * Country IN +91(India) for  918866667722
         * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
         */
        fun getCountryForNumber(
            context: Context?,
            language: CountryCodePicker.Language?,
            fullNumber: String?
        ): CCPCountry? {
            Companion.context = context
            return getCountryForNumber(context, language, null, fullNumber)
        }

        /**
         * Returns image res based on country name code
         *
         * @param CCPCountry
         * @return
         */
        fun getFlagEmoji(CCPCountry: CCPCountry): String = CCPCountry.nameCode!!
        /*{
            return when (CCPCountry.nameCode!!.lowercase(Locale.getDefault())) {
                "ad" -> "🇦🇩"
                "ae" -> "🇦🇪"
                "af" -> "🇦🇫"
                "ag" -> "🇦🇬"
                "ai" -> "🇦🇮"
                "al" -> "🇦🇱"
                "am" -> "🇦🇲"
                "ao" -> "🇦🇴"
                "aq" -> "🇦🇶"
                "ar" -> "🇦🇷"
                "as" -> "🇦🇸"
                "at" -> "🇦🇹"
                "au" -> "🇦🇺"
                "aw" -> "🇦🇼"
                "ax" -> "🇦🇽"
                "az" -> "🇦🇿"
                "ba" -> "🇧🇦"
                "bb" -> "🇧🇧"
                "bd" -> "🇧🇩"
                "be" -> "🇧🇪"
                "bf" -> "🇧🇫"
                "bg" -> "🇧🇬"
                "bh" -> "🇧🇭"
                "bi" -> "🇧🇮"
                "bj" -> "🇧🇯"
                "bl" -> "🇧🇱"
                "bm" -> "🇧🇲"
                "bn" -> "🇧🇳"
                "bo" -> "🇧🇴"
                "bq" -> "🇧🇶"
                "br" -> "🇧🇷"
                "bs" -> "🇧🇸"
                "bt" -> "🇧🇹"
                "bv" -> "🇧🇻"
                "bw" -> "🇧🇼"
                "by" -> "🇧🇾"
                "bz" -> "🇧🇿"
                "ca" -> "🇨🇦"
                "cc" -> "🇨🇨"
                "cd" -> "🇨🇩"
                "cf" -> "🇨🇫"
                "cg" -> "🇨🇬"
                "ch" -> "🇨🇭"
                "ci" -> "🇨🇮"
                "ck" -> "🇨🇰"
                "cl" -> "🇨🇱"
                "cm" -> "🇨🇲"
                "cn" -> "🇨🇳"
                "co" -> "🇨🇴"
                "cr" -> "🇨🇷"
                "cu" -> "🇨🇺"
                "cv" -> "🇨🇻"
                "cw" -> "🇨🇼"
                "cx" -> "🇨🇽"
                "cy" -> "🇨🇾"
                "cz" -> "🇨🇿"
                "de" -> "🇩🇪"
                "dj" -> "🇩🇯"
                "dk" -> "🇩🇰"
                "dm" -> "🇩🇲"
                "do" -> "🇩🇴"
                "dz" -> "🇩🇿"
                "ec" -> "🇪🇨"
                "ee" -> "🇪🇪"
                "eg" -> "🇪🇬"
                "eh" -> "🇪🇭"
                "er" -> "🇪🇷"
                "es" -> "🇪🇸"
                "et" -> "🇪🇹"
                "fi" -> "🇫🇮"
                "fj" -> "🇫🇯"
                "fk" -> "🇫🇰"
                "fm" -> "🇫🇲"
                "fo" -> "🇫🇴"
                "fr" -> "🇫🇷"
                "ga" -> "🇬🇦"
                "gb" -> "🇬🇧"
                "gd" -> "🇬🇩"
                "ge" -> "🇬🇪"
                "gf" -> "🇬🇫"
                "gg" -> "🇬🇬"
                "gh" -> "🇬🇭"
                "gi" -> "🇬🇮"
                "gl" -> "🇬🇱"
                "gm" -> "🇬🇲"
                "gn" -> "🇬🇳"
                "gp" -> "🇬🇵"
                "gq" -> "🇬🇶"
                "gr" -> "🇬🇷"
                "gs" -> "🇬🇸"
                "gt" -> "🇬🇹"
                "gu" -> "🇬🇺"
                "gw" -> "🇬🇼"
                "gy" -> "🇬🇾"
                "hk" -> "🇭🇰"
                "hm" -> "🇭🇲"
                "hn" -> "🇭🇳"
                "hr" -> "🇭🇷"
                "ht" -> "🇭🇹"
                "hu" -> "🇭🇺"
                "id" -> "🇮🇩"
                "ie" -> "🇮🇪"
                "il" -> "🇮🇱"
                "im" -> "🇮🇲"
                "in" -> "🇮🇳"
                "io" -> "🇮🇴"
                "iq" -> "🇮🇶"
                "ir" -> "🇮🇷"
                "is" -> "🇮🇸"
                "it" -> "🇮🇹"
                "je" -> "🇯🇪"
                "jm" -> "🇯🇲"
                "jo" -> "🇯🇴"
                "jp" -> "🇯🇵"
                "ke" -> "🇰🇪"
                "kg" -> "🇰🇬"
                "kh" -> "🇰🇭"
                "ki" -> "🇰🇮"
                "km" -> "🇰🇲"
                "kn" -> "🇰🇳"
                "kp" -> "🇰🇵"
                "kr" -> "🇰🇷"
                "kw" -> "🇰🇼"
                "ky" -> "🇰🇾"
                "kz" -> "🇰🇿"
                "la" -> "🇱🇦"
                "lb" -> "🇱🇧"
                "lc" -> "🇱🇨"
                "li" -> "🇱🇮"
                "lk" -> "🇱🇰"
                "lr" -> "🇱🇷"
                "ls" -> "🇱🇸"
                "lt" -> "🇱🇹"
                "lu" -> "🇱🇺"
                "lv" -> "🇱🇻"
                "ly" -> "🇱🇾"
                "ma" -> "🇲🇦"
                "mc" -> "🇲🇨"
                "md" -> "🇲🇩"
                "me" -> "🇲🇪"
                "mf" -> "🇲🇫"
                "mg" -> "🇲🇬"
                "mh" -> "🇲🇭"
                "mk" -> "🇲🇰"
                "ml" -> "🇲🇱"
                "mm" -> "🇲🇲"
                "mn" -> "🇲🇳"
                "mo" -> "🇲🇴"
                "mp" -> "🇲🇵"
                "mq" -> "🇲🇶"
                "mr" -> "🇲🇷"
                "ms" -> "🇲🇸"
                "mt" -> "🇲🇹"
                "mu" -> "🇲🇺"
                "mv" -> "🇲🇻"
                "mw" -> "🇲🇼"
                "mx" -> "🇲🇽"
                "my" -> "🇲🇾"
                "mz" -> "🇲🇿"
                "na" -> "🇳🇦"
                "nc" -> "🇳🇨"
                "ne" -> "🇳🇪"
                "nf" -> "🇳🇫"
                "ng" -> "🇳🇬"
                "ni" -> "🇳🇮"
                "nl" -> "🇳🇱"
                "no" -> "🇳🇴"
                "np" -> "🇳🇵"
                "nr" -> "🇳🇷"
                "nu" -> "🇳🇺"
                "nz" -> "🇳🇿"
                "om" -> "🇴🇲"
                "pa" -> "🇵🇦"
                "pe" -> "🇵🇪"
                "pf" -> "🇵🇫"
                "pg" -> "🇵🇬"
                "ph" -> "🇵🇭"
                "pk" -> "🇵🇰"
                "pl" -> "🇵🇱"
                "pm" -> "🇵🇲"
                "pn" -> "🇵🇳"
                "pr" -> "🇵🇷"
                "ps" -> "🇵🇸"
                "pt" -> "🇵🇹"
                "pw" -> "🇵🇼"
                "py" -> "🇵🇾"
                "qa" -> "🇶🇦"
                "re" -> "🇷🇪"
                "ro" -> "🇷🇴"
                "rs" -> "🇷🇸"
                "ru" -> "🇷🇺"
                "rw" -> "🇷🇼"
                "sa" -> "🇸🇦"
                "sb" -> "🇸🇧"
                "sc" -> "🇸🇨"
                "sd" -> "🇸🇩"
                "se" -> "🇸🇪"
                "sg" -> "🇸🇬"
                "sh" -> "🇸🇭"
                "si" -> "🇸🇮"
                "sj" -> "🇸🇯"
                "sk" -> "🇸🇰"
                "sl" -> "🇸🇱"
                "sm" -> "🇸🇲"
                "sn" -> "🇸🇳"
                "so" -> "🇸🇴"
                "sr" -> "🇸🇷"
                "ss" -> "🇸🇸"
                "st" -> "🇸🇹"
                "sv" -> "🇸🇻"
                "sx" -> "🇸🇽"
                "sy" -> "🇸🇾"
                "sz" -> "🇸🇿"
                "tc" -> "🇹🇨"
                "td" -> "🇹🇩"
                "tf" -> "🇹🇫"
                "tg" -> "🇹🇬"
                "th" -> "🇹🇭"
                "tj" -> "🇹🇯"
                "tk" -> "🇹🇰"
                "tl" -> "🇹🇱"
                "tm" -> "🇹🇲"
                "tn" -> "🇹🇳"
                "to" -> "🇹🇴"
                "tr" -> "🇹🇷"
                "tt" -> "🇹🇹"
                "tv" -> "🇹🇻"
                "tw" -> "🇹🇼"
                "tz" -> "🇹🇿"
                "ua" -> "🇺🇦"
                "ug" -> "🇺🇬"
                "um" -> "🇺🇲"
                "us" -> "🇺🇸"
                "uy" -> "🇺🇾"
                "uz" -> "🇺🇿"
                "va" -> "🇻🇦"
                "vc" -> "🇻🇨"
                "ve" -> "🇻🇪"
                "vg" -> "🇻🇬"
                "vi" -> "🇻🇮"
                "vn" -> "🇻🇳"
                "vu" -> "🇻🇺"
                "wf" -> "🇼🇫"
                "ws" -> "🇼🇸"
                "xk" -> "🇽🇰"
                "ye" -> "🇾🇪"
                "yt" -> "🇾🇹"
                "za" -> "🇿🇦"
                "zm" -> "🇿🇲"
                "zw" -> "🇿🇼"
                else -> " "
            }
        }*/

        /**
         * This will return all the countries. No preference is manages.
         * Anytime new country need to be added, add it
         *
         * @return
         */
        fun getLibraryMasterCountryList(
            context: Context?,
            language: CountryCodePicker.Language?
        ): List<CCPCountry?>? {
            Companion.context = context
            if (loadedLibraryMasterListLanguage == null || language != loadedLibraryMasterListLanguage || loadedLibraryMaterList == null || loadedLibraryMaterList!!.isEmpty()) {
                //when it is required to load country in country list
                loadDataFromXML(context, language)
            }
            return loadedLibraryMaterList
        }

        val libraryMasterCountriesEnglish: MutableList<CCPCountry?>
            get() {
                val countries: MutableList<CCPCountry?> = ArrayList()

                val array = JSONArray(loadCountryJSONFromAsset())

                for (i in 0 until array.length()) {
                    val jo_inside: JSONObject = array.getJSONObject(i)
                    val name = jo_inside.getString("name")
                    val dial_code = jo_inside.getString("dial_code").substring(1)
                    val code = jo_inside.getString("code")
                    countries.add(CCPCountry(code, dial_code, name))
                }
                return countries
            }

        private fun loadCountryJSONFromAsset(): String? {
            val json: String? = try {
                val inputStream: InputStream? = context?.assets?.open("countryCodes.json")
                val size: Int? = inputStream?.available()
                val buffer = size?.let { ByteArray(it) }
                inputStream?.read(buffer)
                inputStream?.close()
                buffer?.let { String(it, Charsets.UTF_8) }
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }
            return json
        }
    }
}