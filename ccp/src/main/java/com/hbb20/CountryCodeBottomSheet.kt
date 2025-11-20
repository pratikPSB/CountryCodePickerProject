package com.hbb20

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hbb20.databinding.CountrySelectionBottomSheetBinding

class CountryCodeBottomSheet(
    private val codePicker: CountryCodePicker,
    private val countryNameCode: String? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: CountrySelectionBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CountrySelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        codePicker.refreshCustomMasterList()
        codePicker.refreshPreferredCountries()
        val masterCountries: List<CCPCountry?>? = CCPCountry.getCustomMasterCountryList(
            context,
            codePicker
        )

        if (codePicker.isSearchAllowed && codePicker.isDialogKeyboardAutoPopup) {
            binding.editTextSearch.requestFocus()
            bottomSheetDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        } else {
            bottomSheetDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }

        try {
            if (codePicker.getDialogTypeFace() != null) {
                if (codePicker.dialogTypeFaceStyle != CountryCodePicker.DEFAULT_UNSET) {
                    binding.textViewNoresult.setTypeface(
                        codePicker.getDialogTypeFace(),
                        codePicker.dialogTypeFaceStyle
                    )
                    binding.editTextSearch.setTypeface(
                        codePicker.getDialogTypeFace(),
                        codePicker.dialogTypeFaceStyle
                    )
                    binding.textViewTitle.setTypeface(
                        codePicker.getDialogTypeFace(),
                        codePicker.dialogTypeFaceStyle
                    )
                } else {
                    binding.textViewNoresult.setTypeface(codePicker.getDialogTypeFace())
                    binding.editTextSearch.setTypeface(codePicker.getDialogTypeFace())
                    binding.textViewTitle.setTypeface(codePicker.getDialogTypeFace())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //dialog background color
        if (codePicker.dialogBackgroundColor != 0) {
            binding.root.setBackgroundColor(codePicker.dialogBackgroundColor)
        }
        if (codePicker.dialogBackgroundResId != 0) {
            binding.root.setBackgroundResource(codePicker.dialogBackgroundResId)
        }

        //close button visibility
        if (codePicker.isShowCloseIcon) {
            binding.imgDismiss.visibility = View.VISIBLE
            binding.imgDismiss.setOnClickListener { dismiss() }
        } else {
            binding.imgDismiss.visibility = View.GONE
        }

        //title
        if (!codePicker.ccpDialogShowTitle) {
            binding.textViewTitle.visibility = View.GONE
        }

        //clear button color and title color
        if (codePicker.dialogTextColor != 0) {
            val textColor = codePicker.dialogTextColor
            binding.imgDismiss.setColorFilter(textColor)
            binding.textViewTitle.setTextColor(textColor)
            binding.textViewNoresult.setTextColor(textColor)
            binding.editTextSearch.setTextColor(textColor)
            binding.editTextSearch.setHintTextColor(
                Color.argb(
                    100,
                    Color.red(textColor),
                    Color.green(textColor),
                    Color.blue(textColor)
                )
            )
        }

        //add messages to views
        binding.textViewTitle.text = codePicker.dialogTitle
        binding.editTextSearch.hint = codePicker.searchHintText
        binding.textViewNoresult.text = codePicker.noResultACK

        //this will make dialog compact
        if (!codePicker.isSearchAllowed) {
            val params = binding.recyclerCountryDialog.layoutParams as RelativeLayout.LayoutParams
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT
            binding.recyclerCountryDialog.layoutParams = params
        }

        val cca = CountryCodeAdapter(context, masterCountries, codePicker, binding, this)
        binding.recyclerCountryDialog.layoutManager = LinearLayoutManager(context)
        binding.recyclerCountryDialog.adapter = cca

        //fast scroller
        binding.fastscroll.setRecyclerView(binding.recyclerCountryDialog)
        if (codePicker.isShowFastScroller) {
            if (codePicker.fastScrollerBubbleColor != 0) {
                binding.fastscroll.setBubbleColor(codePicker.fastScrollerBubbleColor)
            }
            if (codePicker.fastScrollerHandleColor != 0) {
                binding.fastscroll.setHandleColor(codePicker.fastScrollerHandleColor)
            }
            if (codePicker.fastScrollerBubbleTextAppearance != 0) {
                try {
                    binding.fastscroll.setBubbleTextAppearance(codePicker.fastScrollerBubbleTextAppearance)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            binding.fastscroll.visibility = View.GONE
        }
        bottomSheetDialog.setOnDismissListener { dialogInterface ->
            hideKeyboard(context)
            if (codePicker.dialogEventsListener != null) {
                codePicker.dialogEventsListener!!.onCcpDialogDismiss(dialogInterface)
            }
        }
        bottomSheetDialog.setOnCancelListener { dialogInterface ->
            hideKeyboard(context)
            if (codePicker.dialogEventsListener != null) {
                codePicker.dialogEventsListener!!.onCcpDialogCancel(dialogInterface)
            }
        }

        //auto scroll to mentioned countryNameCode
        if (countryNameCode != null) {
            var isPreferredCountry = false
            if (codePicker.preferredCountries != null) {
                for (preferredCountry in codePicker.preferredCountries!!) {
                    if (preferredCountry!!.nameCode.equals(countryNameCode, ignoreCase = true)) {
                        isPreferredCountry = true
                        break
                    }
                }
            }

            //if selection is from preferred countries then it should show all (or maximum) preferred countries.
            // don't scroll if it was one of those preferred countries
            if (!isPreferredCountry) {
                var preferredCountriesOffset = 0
                if (codePicker.preferredCountries != null && codePicker.preferredCountries!!.isNotEmpty()) {
                    preferredCountriesOffset = codePicker.preferredCountries!!.size + 1 //+1 is for divider
                }
                for (i in masterCountries!!.indices) {
                    if (masterCountries[i]!!.nameCode.equals(countryNameCode, ignoreCase = true)) {
                        binding.recyclerCountryDialog.scrollToPosition(i + preferredCountriesOffset)
                        break
                    }
                }
            }
        }

        if (codePicker.dialogEventsListener != null) {
            codePicker.dialogEventsListener!!.onCcpDialogOpen(bottomSheetDialog)
        }
    }

    private fun getRecommendedList(masterCountries: List<CCPCountry?>?): ArrayList<Any?> {
        val mastercountries2 = ArrayList<CCPCountry?>()
        val mastercountries3 = ArrayList<CCPCountry?>()

        for (country in masterCountries!!) {
            if (country?.nameCode == "IN" || country?.nameCode == "AU" || country?.nameCode == "CA" || country?.nameCode == "GB" || country?.nameCode == "US") {
                mastercountries2.add(country)
            } else {
                mastercountries3.add(country)
            }
        }

        val countryListWithRecommended = ArrayList<Any?>()

        countryListWithRecommended.add("RECOMMENDED COUNTRIES")
        countryListWithRecommended.addAll(mastercountries2)
        countryListWithRecommended.add("All OTHER COUNTRIES")
        countryListWithRecommended.addAll(mastercountries3)

        return countryListWithRecommended
    }

    fun clear() {
        dismiss()
    }

    private fun getDrawable(context: Context, id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    private fun hideKeyboard(context: Context?) {
        if (context is Activity) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = context.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(context)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}