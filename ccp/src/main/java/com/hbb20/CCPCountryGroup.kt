package com.hbb20

import android.content.Context
import android.util.SparseArray

/**
 * This all those countries that shares the same country code but can be differentiated based on area code
 */
class CCPCountryGroup private constructor(
    var defaultNameCode: String,
    var areaCodeLength: Int,
    private val nameCodeToAreaCodesMap: HashMap<String?, String>
) {
    /**
     * Go though nameCodeToAreaCodesMap entries to find name code of country.
     *
     * @param context
     * @param language
     * @param areaCode for which we are looking for country
     * @return country that matches areaCode. If no country matched, returns default country.
     */
    fun getCountryForAreaCode(
        context: Context?,
        language: CountryCodePicker.Language?,
        areaCode: String?
    ): CCPCountry? {
        var nameCode = defaultNameCode
        for ((key, value) in nameCodeToAreaCodesMap) {
            if (value.contains(areaCode!!)) {
                nameCode = key.toString()
            }
        }
        return CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, language, nameCode)
    }

    companion object {
        private val countryGroups: SparseArray<CCPCountryGroup> = SparseArray()

        private fun initializeGroups(countryCode: Int): CCPCountryGroup {
            return addGroupForPhoneCode1(countryCode)
        }

        private fun addGroupForPhoneCode358(countryCode: Int): CCPCountryGroup {
            val nameCodeToAreaCodes = HashMap<String?, String>()
            nameCodeToAreaCodes["ax"] = "18" //Åland Islands
            countryGroups.put(358, CCPCountryGroup("fi", 2, nameCodeToAreaCodes)) // Finland

            return countryGroups.get(countryCode.toString().length - 1)
        }

        private fun addGroupForPhoneCode44(countryCode: Int): CCPCountryGroup {
            val nameCodeToAreaCodes = HashMap<String?, String>()
            nameCodeToAreaCodes["gg"] = "1481" //Guernsey
            nameCodeToAreaCodes["im"] = "1624" //ISLE_OF_MAN
            nameCodeToAreaCodes["je"] = "1534" //Jersey
            countryGroups.put(44, CCPCountryGroup("gb", 4, nameCodeToAreaCodes)) // UK
            return addGroupForPhoneCode358(countryCode)
        }

        private fun addGroupForPhoneCode1(countryCode: Int): CCPCountryGroup {
            val nameCodeToAreaCodes = HashMap<String?, String>()
            nameCodeToAreaCodes["ag"] = "268" //ANTIGUA_AND_BARBUDA_AREA_CODES
            nameCodeToAreaCodes["ai"] = "264" //ANGUILLA_AREA_CODES
            nameCodeToAreaCodes["as"] = "684" //American Samoa
            nameCodeToAreaCodes["bb"] = "246" //BARBADOS_AREA_CODES
            nameCodeToAreaCodes["bm"] = "441" //BERMUDA_AREA_CODES
            nameCodeToAreaCodes["bs"] = "242" //BAHAMAS_AREA_CODES
            nameCodeToAreaCodes["ca"] = "204/226/236/249/250/289/306/343/365/403/416/418/431/437/438/450/506/514/519/579/581/587/600/601/604/613/639/647/705/709/769/778/780/782/807/819/825/867/873/902/905/" //CANADA_AREA_CODES
            nameCodeToAreaCodes["dm"] = "767" //DOMINICA_AREA_CODES
            nameCodeToAreaCodes["do"] = "809/829/849" //DOMINICAN_REPUBLIC_AREA_CODES
            nameCodeToAreaCodes["gd"] = "473" //GRENADA_AREA_CODES
            nameCodeToAreaCodes["gu"] = "671" //Guam
            nameCodeToAreaCodes["jm"] = "876" //JAMAICA_AREA_CODES
            nameCodeToAreaCodes["kn"] = "869" //SAINT_KITTS_AND_NEVIS_AREA_CODES
            nameCodeToAreaCodes["ky"] = "345" //CAYMAN_ISLANDS_AREA_CODES
            nameCodeToAreaCodes["lc"] = "758" //SAINT_LUCIA_AREA_CODES
            nameCodeToAreaCodes["mp"] = "670" //Northern Mariana Islands
            nameCodeToAreaCodes["ms"] = "664" //MONTSERRAT_AREA_CODES
            nameCodeToAreaCodes["pr"] = "787" //PUERTO_RICO_AREA_CODES
            nameCodeToAreaCodes["sx"] = "721" //SINT_MAARTEN_AREA_CODES
            nameCodeToAreaCodes["tc"] = "649" //TURKS_AND_CAICOS_ISLANDS_AREA_CODES
            nameCodeToAreaCodes["tt"] = "868" //TRINIDAD_AND_TOBAGO_AREA_CODES
            nameCodeToAreaCodes["vc"] = "784" //SAINT_VINCENT_AND_THE_GRENADINES_AREA_CODES
            nameCodeToAreaCodes["vg"] = "284" //BRITISH_VIRGIN_ISLANDS_AREA_CODES
            nameCodeToAreaCodes["vi"] = "340" //US_VIRGIN_ISLANDS_AREA_CODES
            countryGroups.put(1, CCPCountryGroup("us", 3, nameCodeToAreaCodes)) // USA
            return addGroupForPhoneCode44(countryCode)
        }

        fun getCountryGroupForPhoneCode(countryCode: Int): CCPCountryGroup {
            return initializeGroups(countryCode)
        }
    }
}