package com.hbb20

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.hbb20.databinding.CountrySelectionBottomSheetBinding
import com.hbb20.databinding.LayoutRecyclerCountryTileBinding
import java.util.Locale

internal class CountryCodeAdapter(
    var context: Context?,
    var countries: List<CCPCountry?>?,
    var codePicker: CountryCodePicker,
    val dialogBinding: CountrySelectionBottomSheetBinding,
    var dialog: CountryCodeBottomSheet
) : RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder>(), SectionTitleProvider {

    var filteredCountries: List<CCPCountry?>? = null
    var preferredCountriesCount = 0

    init {
        filteredCountries = getFilteredCountries("")
        setTextWatcher()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CountryCodeViewHolder {
        val rootView = LayoutInflater.from(context)
            .inflate(R.layout.layout_recycler_country_tile, viewGroup, false)
        return CountryCodeViewHolder(rootView)
    }

    override fun onBindViewHolder(countryCodeViewHolder: CountryCodeViewHolder, i: Int) {
        countryCodeViewHolder.setCountry(filteredCountries!![i])
        if (filteredCountries!!.size > i && filteredCountries!![i] != null) {
            countryCodeViewHolder.binding.mainView.setOnClickListener { view ->
                if (filteredCountries != null && filteredCountries!!.size > i) {
                    codePicker.onUserTappedCountry(filteredCountries!![i])
                }
                if (view != null && filteredCountries != null && filteredCountries!!.size > i && filteredCountries!![i] != null) {
                    val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    dialog.dismiss()
                }
            }
        } else {
            countryCodeViewHolder.binding.mainView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return filteredCountries!!.size
    }

    override fun getSectionTitle(position: Int): String {
        val ccpCountry = filteredCountries!![position]
        return if (preferredCountriesCount > position) {
            "★"
        } else ccpCountry?.name?.substring(0, 1) ?: "☺" //this should never be the case
    }

    private fun setTextWatcher() {
        dialogBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                applyQuery(s.toString())
            }
        })
        dialogBinding.editTextSearch.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    dialogBinding.editTextSearch.windowToken,
                    0
                )
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun applyQuery(query: String) {
        var mQuery = query
        dialogBinding.textViewNoresult.visibility = View.GONE
        mQuery = mQuery.lowercase(Locale.getDefault())

        //if query started from "+" ignore it
        if (mQuery.isNotEmpty() && mQuery[0] == '+') {
            mQuery = mQuery.substring(1)
        }
        filteredCountries = getFilteredCountries(mQuery)
        if (filteredCountries!!.isEmpty()) {
            dialogBinding.textViewNoresult.visibility = View.VISIBLE
        }
        notifyDataSetChanged()
    }

    private fun getFilteredCountries(query: String): List<CCPCountry?> {
        val tempCCPCountryList: MutableList<CCPCountry?> = ArrayList()
        preferredCountriesCount = 0
        if (codePicker.preferredCountries != null && codePicker.preferredCountries!!.isNotEmpty()) {
            for (preferredCountry in codePicker.preferredCountries!!) {
                if (preferredCountry!!.isEligibleForQuery(query)) {
                    tempCCPCountryList.add(preferredCountry)
                    preferredCountriesCount++
                }
            }
            if (tempCCPCountryList.isNotEmpty()) { //means at least one preferred country is added.
                val divider: CCPCountry? = null
                tempCCPCountryList.add(divider)
                preferredCountriesCount++
            }
        }
        for (queriedCountry in countries!!) {
            if (queriedCountry!!.isEligibleForQuery(query)) {
                tempCCPCountryList.add(queriedCountry)
            }
        }
        return tempCCPCountryList
    }

    internal inner class CountryCodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = LayoutRecyclerCountryTileBinding.bind(itemView)

        fun setCountry(ccpCountry: CCPCountry?) {
            if (ccpCountry != null) {
                binding.textViewCountryName.visibility = View.VISIBLE
                binding.textViewCode.visibility = View.VISIBLE
                if (codePicker.isCcpDialogShowPhoneCode) {
                    binding.textViewCode.visibility = View.VISIBLE
                } else {
                    binding.textViewCode.visibility = View.GONE
                }
                var countryName: String? = ""
                if (codePicker.ccpDialogShowFlag && codePicker.ccpUseEmoji) {
                    //extra space is just for alignment purpose
                    countryName += CCPCountry.getFlagEmoji(ccpCountry) + "   "
                }
                countryName += ccpCountry.name
                if (codePicker.isDialogInitialScrollToSelectionEnabled) {
                    countryName += " (" + ccpCountry.nameCode!!.uppercase(Locale.getDefault()) + ")"
                }
                binding.textViewCountryName.text = countryName
                binding.textViewCode.text = "(+${ccpCountry.phoneCode})"
                if (!codePicker.ccpDialogShowFlag || codePicker.ccpUseEmoji) {
                    binding.linearFlagHolder.visibility = View.GONE
                } else {
                    binding.linearFlagHolder.visibility = View.VISIBLE
                    context?.let {
                        Glide.with(it)
                            .load("file:///android_asset/country_flags/${ccpCountry.nameCode}.png")
                            .into(binding.imageFlag)
                    }
                }
            } else {
                binding.textViewCountryName.visibility = View.GONE
                binding.textViewCode.visibility = View.GONE
                binding.linearFlagHolder.visibility = View.GONE
            }
        }

        init {
            if (codePicker.dialogTextColor != 0) {
                binding.textViewCountryName.setTextColor(codePicker.dialogTextColor)
                binding.textViewCode.setTextColor(codePicker.dialogTextColor)
            }
            if (codePicker.ccpDialogRippleEnable) {
                val outValue = TypedValue()
                context!!.theme.resolveAttribute(
                    android.R.attr.selectableItemBackground,
                    outValue,
                    true
                )
                if (outValue.resourceId != 0) binding.mainView.setBackgroundResource(outValue.resourceId) else binding.mainView.setBackgroundResource(
                    outValue.data
                )
            }
            try {
                if (codePicker.getDialogTypeFace() != null) {
                    if (codePicker.dialogTypeFaceStyle != CountryCodePicker.DEFAULT_UNSET) {
                        binding.textViewCode.setTypeface(
                            codePicker.getDialogTypeFace(),
                            codePicker.dialogTypeFaceStyle
                        )
                        binding.textViewCountryName.setTypeface(
                            codePicker.getDialogTypeFace(),
                            codePicker.dialogTypeFaceStyle
                        )
                    } else {
                        binding.textViewCode.setTypeface(codePicker.getDialogTypeFace())
                        binding.textViewCountryName.setTypeface(codePicker.getDialogTypeFace())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}