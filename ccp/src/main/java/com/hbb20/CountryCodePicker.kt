package com.hbb20

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Locale

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodePicker : RelativeLayout {
    private var talkBackTextProvider: CCPTalkBackTextProvider? = InternalTalkBackTextProvider()
    var ccpPrefFile = "CCP_PREF_FILE"
    var defaultCountryCode = 0
    var defaultCountryNameCode: String? = null
    var mContext: Context
    lateinit var holderView: View
    lateinit var mInflater: LayoutInflater
    var textviewSelectedCountry: TextView? = null
    var edittextRegisteredCarrierNumber: EditText? = null
    var holder: RelativeLayout? = null
    var imageViewArrow: ImageView? = null
    var imageViewFlag: ImageView? = null

    var linearFlagHolder: LinearLayout? = null
    var selectedCCPCountry: CCPCountry? = null

    lateinit var countryCodeBottomSheet: CountryCodeBottomSheet
    lateinit var manager: FragmentManager

    var defaultCountry: CCPCountry? = null
    var relativeClickConsumer: RelativeLayout? = null
    var codePicker: CountryCodePicker? = null
    var currentTextGravity: TextGravity? = null
    var originalHint = ""
    var ccpPadding = 0

    // see attr.xml to see corresponding values for pref
    var selectedAutoDetectionPref = AutoDetectionPref.SIM_NETWORK_LOCALE
    var phoneUtil: PhoneNumberUtil? = null
    var rippleEnable = true
    var showNameCode = true
    var showPhoneCode = true

    /**
     * To show/hide phone code from country selection dialog
     *
     * @param isCcpDialogShowPhoneCode
     */
    var isCcpDialogShowPhoneCode = true
    var showFlag = true
    var showFullName = false

    /**
     * Set visibility of fast scroller.
     *
     * @param isShowFastScroller
     */
    var isShowFastScroller = true
    /**
     * To show/hide name code from country selection dialog
     */
    /**
     * To show/hide title from country selection dialog
     *
     * @param ccpDialogShowTitle
     */
    var ccpDialogShowTitle = true
    /**
     * To show/hide flag from country selection dialog
     */
    /**
     * To show/hide flag from country selection dialog
     *
     * @param ccpDialogShowFlag
     */
    var ccpDialogShowFlag = true
    /**
     * To show/hide ripple from country selection dialog
     */
    /**
     * To show/hide ripple from country selection dialog
     *
     * @param ccpDialogRippleEnable
     */
    var ccpDialogRippleEnable = true
    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @return true if search is set allowed
     */
    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @param isSearchAllowed true will allow search and false will hide search box
     */
    var isSearchAllowed = true
    var showArrow = true
    var isShowCloseIcon = true
    var rememberLastSelection = false
    var detectCountryWithAreaCode = true

    var isDialogInitialScrollToSelectionEnabled = true
        get() = ccpDialogInitialScrollToSelection
    var ccpDialogInitialScrollToSelection = false
    var ccpUseEmoji = false
    var ccpUseDummyEmojiForPreview = false
    private var isInternationalFormattingOnlyEnabled = true
    var hintExampleNumberType = PhoneNumberType.MOBILE
    var selectionMemoryTag: String? = "ccp_last_selection"
    var contentColor = DEFAULT_UNSET
    var arrowColor = DEFAULT_UNSET
    var borderFlagColor = 0
    var dialogTypeFace: Typeface? = null
    var dialogTypeFaceStyle = 0
    var preferredCountries: List<CCPCountry?>? = null
    var ccpTextGravity = TEXT_GRAVITY_CENTER

    //this will be "AU,IN,US"
    var countryPreference: String? = null

    /**
     * Sets bubble color for fast scroller
     *
     * @param fastScrollerBubbleColor
     */
    var fastScrollerBubbleColor = 0

    /**
     * @param customMasterCountriesList is list of countries that we need as custom master list
     */
    var customMasterCountriesList: List<CCPCountry?>? = null

    /**
     * @return comma separated custom master countries' name code. i.e "gb,us,nz,in,pk"
     */
    //this will be "AU,IN,US"
    var customMasterCountriesParam: String? = null
    var excludedCountriesParam: String? = null
    var customDefaultLanguage: Language? = Language.ENGLISH
    var languageToApply: Language? = Language.ENGLISH

    /**
     * By default, keyboard pops up every time ccp is clicked and selection dialog is opened.
     *
     * @param isDialogKeyboardAutoPopup true: to open keyboard automatically when selection dialog is opened
     * false: to avoid auto pop of keyboard
     */
    var isDialogKeyboardAutoPopup = true
    var ccpClickable = true
    var isAutoDetectLanguageEnabled = false
    var isAutoDetectCountryEnabled = false
    var numberAutoFormattingEnabled = true
    var hintExampleNumberEnabled = false
    var xmlWidth: String? = "notSet"
    var validityTextWatcher: TextWatcher? = null
    var formattingTextWatcher: InternationalPhoneTextWatcher? = null
    var reportedValidity = false
    var areaCodeCountryDetectorTextWatcher: TextWatcher? = null
    var countryDetectionBasedOnAreaAllowed = false
    var lastCheckedAreaCode: String? = null
    var lastCursorPosition = 0
    var countryChangedDueToAreaCode = false
    private var onCountryChangeListener: OnCountryChangeListener? = null
    private var phoneNumberValidityChangeListener: PhoneNumberValidityChangeListener? = null
    private var failureListener: FailureListener? = null
    /**
     * @return registered dialog event listener
     */
    /**
     * Dialog events listener will give call backs on various dialog events
     *
     * @param dialogEventsListener
     */
    var dialogEventsListener: DialogEventsListener? = null
    private var customDialogTextProvider: CustomDialogTextProvider? = null

    /**
     * This should be the color for fast scroller handle.
     *
     * @param fastScrollerHandleColor
     */
    var fastScrollerHandleColor = 0
    var dialogBackgroundResId = 0
        private set

    /**
     * This will be color of dialog background
     *
     * @param dialogBackgroundColor
     */
    var dialogBackgroundColor = 0

    /**
     * This color will be applied to
     * Title of dialog
     * Name of country
     * Phone code of country
     * "X" button to clear query
     * preferred country divider if preferred countries defined (semi transparent)
     *
     * @param dialogTextColor
     */
    var dialogTextColor = 0

    /**
     * If device is running above or equal LOLLIPOP version, this will change tint of search edittext background.
     *
     * @param dialogSearchEditTextTintColor
     */
    var dialogSearchEditTextTintColor = 0

    /**
     * This sets text appearance for fast scroller index character
     *
     * @param fastScrollerBubbleTextAppearance should be reference id of text appearance style. i.e. R.style.myBubbleTextAppearance
     */
    var fastScrollerBubbleTextAppearance = 0
    private var currentCountryGroup: CCPCountryGroup? = null
    private var customClickListener: OnClickListener? = null
    private var countryCodeHolderClickListener = OnClickListener { v ->
        if (customClickListener == null) {
            if (isCcpClickable()) {
                if (ccpDialogInitialScrollToSelection) {
                    launchCountrySelectionDialog(selectedCountryNameCode)
                } else {
                    launchCountrySelectionDialog()
                }
            }
        } else {
            customClickListener!!.onClick(v)
        }
    }

    constructor(context: Context) : super(context) {
        this.mContext = context
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        init(attrs)
    }

    private fun isNumberAutoFormattingEnabled(): Boolean {
        return numberAutoFormattingEnabled
    }

    fun setFragmentManager(manager: FragmentManager) {
        this.manager = manager
    }

    /**
     * This will set boolean for numberAutoFormattingEnabled and refresh formattingTextWatcher
     *
     * @param numberAutoFormattingEnabled
     */
    @JvmName("setNumberAutoFormattingEnabled1")
    fun setNumberAutoFormattingEnabled(numberAutoFormattingEnabled: Boolean) {
        this.numberAutoFormattingEnabled = numberAutoFormattingEnabled
        if (edittextRegisteredCarrierNumber != null) {
            updateFormattingTextWatcher()
        }
    }

    /**
     * This will set boolean for internationalFormattingOnly and refresh formattingTextWatcher
     *
     * @param internationalFormattingOnly
     */
    fun setInternationalFormattingOnly(internationalFormattingOnly: Boolean) {
        isInternationalFormattingOnlyEnabled = internationalFormattingOnly
        if (edittextRegisteredCarrierNumber != null) {
            updateFormattingTextWatcher()
        }
    }

    private fun init(attrs: AttributeSet?) {
        mInflater = LayoutInflater.from(mContext)
        if (attrs != null) {
            xmlWidth = attrs.getAttributeValue(ANDROID_NAME_SPACE, "layout_width")
        }
        removeAllViewsInLayout()
        //at run time, match parent value returns LayoutParams.MATCH_PARENT ("-1")
        holderView = if (attrs != null && xmlWidth != null && (xmlWidth == LayoutParams.MATCH_PARENT.toString() + "" || xmlWidth == "match_parent")) {
            mInflater.inflate(R.layout.layout_full_width_code_picker, this, true)
        } else {
            mInflater.inflate(R.layout.layout_code_picker, this, true)
        }
        textviewSelectedCountry = holderView.findViewById<View>(R.id.textView_selectedCountry) as TextView
        holder = holderView.findViewById<View>(R.id.countryCodeHolder) as RelativeLayout
        imageViewArrow = holderView.findViewById<View>(R.id.imageView_arrow) as ImageView
        imageViewFlag = holderView.findViewById<View>(R.id.image_flag) as ImageView
        linearFlagHolder = holderView.findViewById<View>(R.id.linear_flag_holder) as LinearLayout
//        linearFlagBorder = holderView.findViewById<View>(R.id.linear_flag_border) as LinearLayout
        relativeClickConsumer = holderView.findViewById<View>(R.id.rlClickConsumer) as RelativeLayout
        codePicker = this
        attrs?.let { applyCustomProperty(it) }
        relativeClickConsumer!!.setOnClickListener(countryCodeHolderClickListener)
    }

    private fun applyCustomProperty(attrs: AttributeSet) {
        //        Log.d(TAG, "Applying custom property");
        val a = mContext.theme.obtainStyledAttributes(attrs, R.styleable.CountryCodePicker, 0, 0)
        //default country code
        try {
            //hide nameCode. If someone wants only phone code to avoid name collision for same country phone code.
            showNameCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_showNameCode, true)

            //number auto formatting
            numberAutoFormattingEnabled = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_autoFormatNumber,
                true
            )

            //show phone code.
            showPhoneCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_showPhoneCode, true)

            //show phone code on dialog
            isCcpDialogShowPhoneCode = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_showPhoneCode,
                showPhoneCode
            )

            //show name code on dialog
            isDialogInitialScrollToSelectionEnabled = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_showNameCode,
                true
            )

            //show title on dialog
            ccpDialogShowTitle = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_showTitle,
                true
            )

            //show title on dialog
            ccpUseEmoji = a.getBoolean(R.styleable.CountryCodePicker_ccp_useFlagEmoji, false)

            //show title on dialog
            ccpUseDummyEmojiForPreview = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_useDummyEmojiForPreview,
                false
            )

            //show flag on dialog
            ccpDialogShowFlag = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showFlag, true)

            //ccpDialog initial scroll to selection
            ccpDialogInitialScrollToSelection = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_initialScrollToSelection,
                false
            )

            //ripple enable on dialog
            ccpDialogRippleEnable = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_rippleEnable,
                true
            )

            //show full name
            showFullName = a.getBoolean(R.styleable.CountryCodePicker_ccp_showFullName, false)

            //show fast scroller
            isShowFastScroller = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_showFastScroller,
                true
            )

            //bubble color
            fastScrollerBubbleColor = a.getColor(
                R.styleable.CountryCodePicker_ccpDialog_fastScroller_bubbleColor,
                0
            )

            //scroller handle color
            fastScrollerHandleColor = a.getColor(
                R.styleable.CountryCodePicker_ccpDialog_fastScroller_handleColor,
                0
            )

            //scroller text appearance
            fastScrollerBubbleTextAppearance = a.getResourceId(
                R.styleable.CountryCodePicker_ccpDialog_fastScroller_bubbleTextAppearance,
                0
            )

            //auto detect language
            isAutoDetectLanguageEnabled = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_autoDetectLanguage,
                false
            )

            //detect country from area code
            detectCountryWithAreaCode = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_areaCodeDetectedCountry,
                true
            )

            //remember last selection
            rememberLastSelection = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_rememberLastSelection,
                false
            )

            //example number hint enabled?
            hintExampleNumberEnabled = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_hintExampleNumber,
                false
            )

            //international formatting only
            isInternationalFormattingOnlyEnabled = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_internationalFormattingOnly,
                true
            )

            // dialog content padding.
            ccpPadding = a.getDimension(
                R.styleable.CountryCodePicker_ccp_padding,
                mContext.resources.getDimension(R.dimen.ccp_padding)
            )
                .toInt()
            relativeClickConsumer!!.setPadding(ccpPadding, ccpPadding, ccpPadding, ccpPadding)

            //example number hint type
            val hintNumberTypeIndex = a.getInt(
                R.styleable.CountryCodePicker_ccp_hintExampleNumberType,
                0
            )
            hintExampleNumberType = PhoneNumberType.entries.toTypedArray()[hintNumberTypeIndex]

            //memory tag name for selection
            selectionMemoryTag = a.getString(R.styleable.CountryCodePicker_ccp_selectionMemoryTag)
            if (selectionMemoryTag == null) {
                selectionMemoryTag = "CCP_last_selection"
            }

            //country auto detection pref
            val autoDetectionPrefValue = a.getInt(
                R.styleable.CountryCodePicker_ccp_countryAutoDetectionPref,
                123
            )
            selectedAutoDetectionPref = AutoDetectionPref.getPrefForValue(autoDetectionPrefValue.toString())

            //auto detect county
            isAutoDetectCountryEnabled = a.getBoolean(
                R.styleable.CountryCodePicker_ccp_autoDetectCountry,
                false
            )

            //show arrow
            showArrow = a.getBoolean(R.styleable.CountryCodePicker_ccp_showArrow, true)
            refreshArrowViewVisibility()

            //show close icon
            isShowCloseIcon = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_showCloseIcon,
                true
            )

            //ripple enable
            rippleEnable = a.getBoolean(R.styleable.CountryCodePicker_ccp_rippleEnable, true)
            refreshEnableRipple()

            //show flag
            showFlag(a.getBoolean(R.styleable.CountryCodePicker_ccp_showFlag, true))

            //auto-pop keyboard
            isDialogKeyboardAutoPopup = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_keyboardAutoPopup,
                true
            )

            //if custom default language is specified, then set it as custom else sets english as custom
            val attrLanguage: Int = a.getInt(
                R.styleable.CountryCodePicker_ccp_defaultLanguage,
                Language.ENGLISH.ordinal
            )
            customDefaultLanguage = getLanguageEnum(attrLanguage)
            updateLanguageToApply()

            //custom master list
            customMasterCountriesParam = a.getString(R.styleable.CountryCodePicker_ccp_customMasterCountries)
            excludedCountriesParam = a.getString(R.styleable.CountryCodePicker_ccp_excludedCountries)
            if (!isInEditMode) {
                refreshCustomMasterList()
            }

            //preference
            countryPreference = a.getString(R.styleable.CountryCodePicker_ccp_countryPreference)
            //as3 is raising problem while rendering preview. to avoid such issue, it will update preferred list only on run time.
            if (!isInEditMode) {
                refreshPreferredCountries()
            }

            //text gravity
            if (a.hasValue(R.styleable.CountryCodePicker_ccp_textGravity)) {
                ccpTextGravity = a.getInt(
                    R.styleable.CountryCodePicker_ccp_textGravity,
                    TEXT_GRAVITY_CENTER
                )
            }
            applyTextGravity(ccpTextGravity)

            //default country
            //AS 3 has some problem with reading list so this is to make CCP preview work
            defaultCountryNameCode = a.getString(R.styleable.CountryCodePicker_ccp_defaultNameCode)
            var setUsingNameCode = false
            if (defaultCountryNameCode != null && defaultCountryNameCode!!.isNotEmpty()) {
                if (!isInEditMode) {
                    if (CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                            context,
                            getLanguageToApply(),
                            defaultCountryNameCode
                        ) != null) {
                        setUsingNameCode = true
                        defaultCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                            context,
                            getLanguageToApply(),
                            defaultCountryNameCode
                        )
                        selectedCountry = defaultCountry
                    }
                } else {
                    if (CCPCountry.getCountryForNameCodeFromEnglishList(
                            defaultCountryNameCode
                        ) != null) {
                        setUsingNameCode = true
                        defaultCountry = CCPCountry.getCountryForNameCodeFromEnglishList(
                            defaultCountryNameCode
                        )
                        selectedCountry = defaultCountry
                    }
                }

                //when it was not set means something was wrong with name code
                if (!setUsingNameCode) {
                    defaultCountry = CCPCountry.getCountryForNameCodeFromEnglishList("IN")
                    selectedCountry = defaultCountry
                    setUsingNameCode = true
                }
            }

            //if default country is not set using name code.
            var defaultCountryCode = a.getInteger(
                R.styleable.CountryCodePicker_ccp_defaultPhoneCode,
                -1
            )
            if (!setUsingNameCode && defaultCountryCode != -1) {
                if (!isInEditMode) {
                    //if invalid country is set using xml, it will be replaced with LIB_DEFAULT_COUNTRY_CODE
                    if (CCPCountry.getCountryForCode(
                            context,
                            getLanguageToApply(),
                            preferredCountries,
                            defaultCountryCode
                        ) == null) {
                        defaultCountryCode = LIB_DEFAULT_COUNTRY_CODE
                    }
                    setDefaultCountryUsingPhoneCode(defaultCountryCode)
                    selectedCountry = defaultCountry
                } else {
                    //when it is in edit mode, we will check in english list only.
                    var defaultCountry: CCPCountry? = CCPCountry.getCountryForCodeFromEnglishList(
                        defaultCountryCode.toString() + ""
                    )
                    if (defaultCountry == null) {
                        defaultCountry = CCPCountry.getCountryForCodeFromEnglishList(
                            LIB_DEFAULT_COUNTRY_CODE.toString() + ""
                        )
                    }
                    this.defaultCountry = defaultCountry
                    selectedCountry = defaultCountry
                }
            }

            //if default country is not set using nameCode or phone code, let's set library default as default
            if (defaultCountry == null) {
                defaultCountry = CCPCountry.getCountryForNameCodeFromEnglishList("IN")
                if (selectedCountry == null) {
                    selectedCountry = defaultCountry
                }
            }


            //set auto detected country
            if (isAutoDetectCountryEnabled && !isInEditMode) {
                setAutoDetectedCountry(true)
            }

            //set last selection
            if (rememberLastSelection && !isInEditMode) {
                loadLastSelectedCountryInCCP()
            }
            val arrowColor: Int = a.getColor(
                R.styleable.CountryCodePicker_ccp_arrowColor,
                DEFAULT_UNSET
            )
            setArrowColor(arrowColor)

            //content color
            val contentColor: Int = if (isInEditMode) {
                a.getColor(R.styleable.CountryCodePicker_ccp_contentColor, DEFAULT_UNSET)
            } else {
                a.getColor(
                    R.styleable.CountryCodePicker_ccp_contentColor,
                    ContextCompat.getColor(mContext, R.color.black)
                )
            }
            if (contentColor != DEFAULT_UNSET) {
                setContentColor(contentColor)
            }

            // flag border color
            val borderFlagColor: Int = if (isInEditMode) {
                a.getColor(R.styleable.CountryCodePicker_ccp_flagBorderColor, 0)
            } else {
                a.getColor(
                    R.styleable.CountryCodePicker_ccp_flagBorderColor,
                    ContextCompat.getColor(mContext, R.color.defaultBorderFlagColor)
                )
            }
            if (borderFlagColor != 0) {
                setFlagBorderColor(borderFlagColor)
            }

            //dialog colors
            dialogBackgroundColor = a.getColor(
                R.styleable.CountryCodePicker_ccpDialog_backgroundColor,
                0
            )
            setDialogBackground(
                a.getResourceId(
                    R.styleable.CountryCodePicker_ccpDialog_background,
                    0
                )
            )
            dialogTextColor = a.getColor(R.styleable.CountryCodePicker_ccpDialog_textColor, 0)
            dialogSearchEditTextTintColor = a.getColor(
                R.styleable.CountryCodePicker_ccpDialog_searchEditTextTint,
                0
            )

            //text size
            val textSize = a.getDimensionPixelSize(
                R.styleable.CountryCodePicker_ccp_textSize,
                mContext.resources.getDimension(com.intuit.sdp.R.dimen._12sdp)
                    .toInt()
            )
            textviewSelectedCountry!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
//            setFlagSize(textSize)
            setArrowSize(textSize)

            // text style
            val textStyle = a.getResourceId(
                R.styleable.CountryCodePicker_ccp_textStyle,
                R.style.BookText
            )
            TextViewCompat.setTextAppearance(textviewSelectedCountry!!, textStyle)

            //if arrow size is explicitly defined
            val arrowSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_ccp_arrowSize, 0)
            if (arrowSize > 0) {
                setArrowSize(arrowSize)
            }
            isSearchAllowed = a.getBoolean(
                R.styleable.CountryCodePicker_ccpDialog_allowSearch,
                true
            )
            setCcpClickable(a.getBoolean(R.styleable.CountryCodePicker_ccp_clickable, true))
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                textviewSelectedCountry!!.maxLines = 25
                textviewSelectedCountry!!.textSize = 10f
                textviewSelectedCountry!!.text = sw.toString()
            }
            e.printStackTrace()
        } finally {
            a.recycle()
        }
    }

    private fun refreshArrowViewVisibility() {
        if (showArrow) {
            imageViewArrow!!.visibility = VISIBLE
        } else {
            imageViewArrow!!.visibility = GONE
        }
    }

    private fun refreshEnableRipple() {
        if (rippleEnable) {
            val outValue = TypedValue()
            context.theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValue,
                true
            )
            if (outValue.resourceId != 0) relativeClickConsumer!!.setBackgroundResource(outValue.resourceId) else relativeClickConsumer!!.setBackgroundResource(
                outValue.data
            )
        }
    }

    /**
     * this will read last selected country name code from the shared pref.
     * if that name code is not null, load that country in the CCP
     * else leaves as it is.(when used for the first time)
     */
    private fun loadLastSelectedCountryInCCP() {
        //get the shared pref
        val sharedPref = mContext.getSharedPreferences(
            ccpPrefFile, Context.MODE_PRIVATE
        )

        // read last selection value
        val lastSelectedCountryNameCode = sharedPref.getString(selectionMemoryTag, null)

        //if last selection value is not null, load it into the CCP
        lastSelectedCountryNameCode?.let { setCountryForNameCode(it) }
    }

    /**
     * This will store the selected name code in the preferences
     *
     * @param selectedCountryNameCode name code of the selected country
     */
    fun storeSelectedCountryNameCode(selectedCountryNameCode: String?) {
        //get the shared pref
        val sharedPref = mContext.getSharedPreferences(
            ccpPrefFile, Context.MODE_PRIVATE
        )

        //we want to write in shared pref, so lets get editor for it
        val editor = sharedPref.edit()

        // add our last selection country name code in pref
        editor.putString(selectionMemoryTag, selectedCountryNameCode)

        //finally save it...
        editor.apply()
    }

    fun isShowPhoneCode(): Boolean {
        return showPhoneCode
    }

    /**
     * To show/hide phone code from ccp view
     *
     * @param showPhoneCode
     */
    @JvmName("setShowPhoneCode1")
    fun setShowPhoneCode(showPhoneCode: Boolean) {
        this.showPhoneCode = showPhoneCode
        selectedCountry = selectedCCPCountry
    }

    /**
     * When width is set "match_parent", this gravity will set placement of text (Between flag and down arrow).
     *
     * @param textGravity expected placement
     */
    @JvmName("setCurrentTextGravity1")
    fun setCurrentTextGravity(textGravity: TextGravity) {
        currentTextGravity = textGravity
        applyTextGravity(textGravity.enumIndex)
    }

    private fun applyTextGravity(enumIndex: Int) {
        when (enumIndex) {
            TextGravity.LEFT.enumIndex -> {
                textviewSelectedCountry!!.gravity = Gravity.LEFT
            }

            TextGravity.CENTER.enumIndex -> {
                textviewSelectedCountry!!.gravity = Gravity.CENTER
            }

            TextGravity.RIGHT.enumIndex -> {
                textviewSelectedCountry!!.gravity = Gravity.CENTER
            }
        }
    }

    /**
     * which language to show is decided based on
     * autoDetectLanguage flag
     * if autoDetectLanguage is true, then it should check language based on locale, if no language is found based on locale, customDefault language will returned
     * else autoDetectLanguage is false, then customDefaultLanguage will be returned.
     *
     * @return
     */
    private fun updateLanguageToApply() {
        //when in edit mode, it will return default language only
        languageToApply = if (isInEditMode) {
            if (customDefaultLanguage != null) {
                customDefaultLanguage
            } else {
                Language.ENGLISH
            }
        } else {
            if (isAutoDetectLanguageEnabled) {
                val localeBasedLanguage = cCPLanguageFromLocale
                localeBasedLanguage ?: //if no language is found from locale
                if (customDefaultLanguage != null) { //and custom language is defined
                    customDefaultLanguage
                } else {
                    Language.ENGLISH
                }
            } else {
                if (customDefaultLanguage != null) {
                    customDefaultLanguage
                } else {
                    Language.ENGLISH //library default
                }
            }
        }
    }

    //        Log.d(TAG, "getCCPLanguageFromLocale: current locale language" + currentLocale.getLanguage());
    private val cCPLanguageFromLocale: Language?
        get() {
            val currentLocale = mContext.resources.configuration.locales[0]
            //        Log.d(TAG, "getCCPLanguageFromLocale: current locale language" + currentLocale.getLanguage());
            for (language in Language.entries) {
                if (language.code.equals(currentLocale.language, ignoreCase = true)) {
                    if (language.country == null
                        || language.country.equals(currentLocale.country, ignoreCase = true)
                    ) return language
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (language.script == null
                            || language.script.equals(currentLocale.script, ignoreCase = true)
                        ) return language
                    }
                }
            }
            return null
        }//show chequered flag if dummy preview is expected.

    // add full name to if required

    // adds name code if required

    // hide phone code if required

    //avoid blank state of ccp

    //notify to registered validity listener

    //once updates are done, this will release lock

    //if the country was auto detected based on area code, this will correct the cursor position.

    //update country group
//                android studio preview shows huge space if 0 width space is not added.
    //force disable area code country detection

    //as soon as country is selected, textView should be updated

    // add flag if required
    private var selectedCountry: CCPCountry?
        get() {
            if (selectedCCPCountry == null) {
                selectedCountry = defaultCountry
            }
            return selectedCCPCountry
        }
        set(selectedCCPCountry) {
            var selectedCCPCountry = selectedCCPCountry
            if (talkBackTextProvider != null && talkBackTextProvider!!.getTalkBackTextForCountry(
                    selectedCCPCountry
                ) != null) {
                textviewSelectedCountry!!.contentDescription = talkBackTextProvider!!.getTalkBackTextForCountry(
                    selectedCCPCountry
                )
            }

            //force disable area code country detection
            countryDetectionBasedOnAreaAllowed = false
            lastCheckedAreaCode = ""

            //as soon as country is selected, textView should be updated
            if (selectedCCPCountry == null) {
                selectedCCPCountry = CCPCountry.getCountryForCode(
                    context,
                    getLanguageToApply(),
                    preferredCountries,
                    defaultCountryCode
                )
                if (selectedCCPCountry == null) {
                    return
                }
            }
            this.selectedCCPCountry = selectedCCPCountry
            var displayText = ""

            // add flag if required
            if (showFlag && ccpUseEmoji) {
                displayText += if (isInEditMode) {
                    if (ccpUseDummyEmojiForPreview) {
                        //show chequered flag if dummy preview is expected.
                        "\uD83C\uDFC1\u200B "
                    } else {
                        CCPCountry.getFlagEmoji(selectedCCPCountry) + "\u200B "
                    }
                } else {
                    CCPCountry.getFlagEmoji(selectedCCPCountry) + "  "
                }
            }

            // add full name to if required
            if (showFullName) {
                displayText += selectedCCPCountry.name
            }

            // adds name code if required
            if (showNameCode) {
                displayText += if (showFullName) {
                    " (" + selectedCCPCountry.nameCode!!.uppercase(Locale.getDefault()) + ")"
                } else {
                    " " + selectedCCPCountry.nameCode!!.uppercase(Locale.getDefault())
                }
            }

            // hide phone code if required
            if (showPhoneCode) {
                if (displayText.isNotEmpty()) {
                    displayText += "  "
                }
                displayText += "+" + selectedCCPCountry.phoneCode
            }
            textviewSelectedCountry!!.text = displayText

            //avoid blank state of ccp
            if (!showFlag && displayText.isEmpty()) {
                displayText += "+" + selectedCCPCountry.phoneCode
                textviewSelectedCountry!!.text = displayText
            }
            Glide.with(context)
                .load("file:///android_asset/country_flags/${selectedCCPCountry.nameCode}.png")
                .into(imageViewFlag!!)
//            imageViewFlag!!.setImageDrawable(selectedCCPCountry.flagID)
            if (onCountryChangeListener != null) {
                onCountryChangeListener!!.onCountrySelected()
            }
            updateFormattingTextWatcher()
            updateHint()

            //notify to registered validity listener
            if (edittextRegisteredCarrierNumber != null && phoneNumberValidityChangeListener != null) {
                reportedValidity = isValidFullNumber
                phoneNumberValidityChangeListener!!.onValidityChanged(reportedValidity)
            }

            //once updates are done, this will release lock
            countryDetectionBasedOnAreaAllowed = true

            //if the country was auto detected based on area code, this will correct the cursor position.
            if (countryChangedDueToAreaCode) {
                try {
                    edittextRegisteredCarrierNumber!!.setSelection(lastCursorPosition)
                    countryChangedDueToAreaCode = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            //update country group
//            updateCountryGroup()
        }

    /**
     * update country group
     */
    /*private fun updateCountryGroup() {
        currentCountryGroup = CCPCountryGroup.getCountryGroupForPhoneCode(selectedCountryCodeAsInt)
    }*/

    /**
     * updates hint
     */
    private fun updateHint() {
        if (edittextRegisteredCarrierNumber != null && hintExampleNumberEnabled) {
            var formattedNumber: String? = ""
            val exampleNumber = getPhoneUtil()!!.getExampleNumberForType(
                selectedCountryNameCode,
                selectedHintNumberType
            )
            if (exampleNumber != null) {
                formattedNumber = exampleNumber.nationalNumber.toString() + ""
                formattedNumber =
                    PhoneNumberUtils.formatNumber(
                        selectedCountryCodeWithPlus + formattedNumber,
                        selectedCountryNameCode
                    )
                if (formattedNumber != null) {
                    formattedNumber = formattedNumber.substring(selectedCountryCodeWithPlus.length)
                        .trim { it <= ' ' }
                }
            } else {
//                Log.w(TAG, "updateHint: No example number found for this country (" + getSelectedCountryNameCode() + ") or this type (" + hintExampleNumberType.name() + ").");
            }

            //fallback to original hint
            if (formattedNumber == null) {
                formattedNumber = originalHint
            }
            edittextRegisteredCarrierNumber!!.hint = formattedNumber
        }
    }

    /**
     * this function maps CountryCodePicker.PhoneNumberType to PhoneNumberUtil.PhoneNumberType.
     *
     * @return respective PhoneNumberUtil.PhoneNumberType based on selected CountryCodePicker.PhoneNumberType.
     */
    private val selectedHintNumberType: PhoneNumberUtil.PhoneNumberType
        get() = when (hintExampleNumberType) {
            PhoneNumberType.MOBILE -> PhoneNumberUtil.PhoneNumberType.MOBILE
            PhoneNumberType.FIXED_LINE -> PhoneNumberUtil.PhoneNumberType.FIXED_LINE
            PhoneNumberType.FIXED_LINE_OR_MOBILE -> PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE
            PhoneNumberType.TOLL_FREE -> PhoneNumberUtil.PhoneNumberType.TOLL_FREE
            PhoneNumberType.PREMIUM_RATE -> PhoneNumberUtil.PhoneNumberType.PREMIUM_RATE
            PhoneNumberType.SHARED_COST -> PhoneNumberUtil.PhoneNumberType.SHARED_COST
            PhoneNumberType.VOIP -> PhoneNumberUtil.PhoneNumberType.VOIP
            PhoneNumberType.PERSONAL_NUMBER -> PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER
            PhoneNumberType.PAGER -> PhoneNumberUtil.PhoneNumberType.PAGER
            PhoneNumberType.UAN -> PhoneNumberUtil.PhoneNumberType.UAN
            PhoneNumberType.VOICEMAIL -> PhoneNumberUtil.PhoneNumberType.VOICEMAIL
            PhoneNumberType.UNKNOWN -> PhoneNumberUtil.PhoneNumberType.UNKNOWN
        }

    @JvmName("getLanguageToApply1")
    fun getLanguageToApply(): Language? {
        if (languageToApply == null) {
            updateLanguageToApply()
        }
        return languageToApply
    }

    @JvmName("setLanguageToApply1")
    fun setLanguageToApply(languageToApply: Language?) {
        this.languageToApply = languageToApply
    }

    private fun updateFormattingTextWatcher() {
        if (edittextRegisteredCarrierNumber != null && selectedCCPCountry != null) {
            val enteredValue = getEditTextRegisteredCarrierNumber()!!.text.toString()
            val digitsValue = PhoneNumberUtil.normalizeDigitsOnly(enteredValue)
            if (formattingTextWatcher != null) {
                edittextRegisteredCarrierNumber!!.removeTextChangedListener(formattingTextWatcher)
            }
            if (areaCodeCountryDetectorTextWatcher != null) {
                edittextRegisteredCarrierNumber!!.removeTextChangedListener(
                    areaCodeCountryDetectorTextWatcher
                )
            }
            if (numberAutoFormattingEnabled) {
                formattingTextWatcher = InternationalPhoneTextWatcher(
                    mContext,
                    selectedCountryNameCode,
                    selectedCountryCodeAsInt,
                    isInternationalFormattingOnlyEnabled
                )
                edittextRegisteredCarrierNumber!!.addTextChangedListener(formattingTextWatcher)
            }

            //if country detection from area code is enabled, then it will add areaCodeCountryDetectorTextWatcher
            if (detectCountryWithAreaCode) {
                areaCodeCountryDetectorTextWatcher = countryDetectorTextWatcher
                edittextRegisteredCarrierNumber!!.addTextChangedListener(
                    areaCodeCountryDetectorTextWatcher
                )
            }

            //text watcher stops working when it finds non digit character in previous phone code. This will reset its function
            edittextRegisteredCarrierNumber!!.setText("")
            edittextRegisteredCarrierNumber!!.setText(digitsValue)
            edittextRegisteredCarrierNumber!!.setSelection(edittextRegisteredCarrierNumber!!.text.length)
        } else {
            if (edittextRegisteredCarrierNumber == null) {
                Log.v(
                    TAG,
                    "updateFormattingTextWatcher: EditText not registered $selectionMemoryTag"
                )
            } else {
                Log.v(
                    TAG,
                    "updateFormattingTextWatcher: selected country is null $selectionMemoryTag"
                )
            }
        }
    }//possible countries

    /**
     * This updates country dynamically as user types in area code
     *
     * @return
     */
    private val countryDetectorTextWatcher: TextWatcher?
        get() {
            if (edittextRegisteredCarrierNumber != null) {
                if (areaCodeCountryDetectorTextWatcher == null) {
                    areaCodeCountryDetectorTextWatcher = object : TextWatcher {
                        var lastCheckedNumber: String? = null
                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            var selectedCountry = selectedCountry
                            if (selectedCountry != null && (lastCheckedNumber == null || lastCheckedNumber != s.toString()) && countryDetectionBasedOnAreaAllowed) {
                                //possible countries
                                if (currentCountryGroup != null) {
                                    val enteredValue = getEditTextRegisteredCarrierNumber()!!.text.toString()
                                    if (enteredValue.length >= currentCountryGroup!!.areaCodeLength) {
                                        val digitsValue = PhoneNumberUtil.normalizeDigitsOnly(
                                            enteredValue
                                        )
                                        if (digitsValue.length >= currentCountryGroup!!.areaCodeLength) {
                                            val currentAreaCode = digitsValue.take(
                                                currentCountryGroup!!.areaCodeLength
                                            )
                                            if (currentAreaCode != lastCheckedAreaCode) {
                                                val detectedCountry = currentCountryGroup!!.getCountryForAreaCode(
                                                    mContext,
                                                    getLanguageToApply(),
                                                    currentAreaCode
                                                )
                                                if (detectedCountry != selectedCountry) {
                                                    countryChangedDueToAreaCode = true
                                                    lastCursorPosition = Selection.getSelectionEnd(s)
                                                    selectedCountry = detectedCountry
                                                }
                                                lastCheckedAreaCode = currentAreaCode
                                            }
                                        }
                                    }
                                }
                                lastCheckedNumber = s.toString()
                            }
                        }

                        override fun afterTextChanged(s: Editable) {}
                    }
                }
            }
            return areaCodeCountryDetectorTextWatcher
        }

    @JvmName("setCustomDefaultLanguage1")
    private fun setCustomDefaultLanguage(customDefaultLanguage: Language) {
        this.customDefaultLanguage = customDefaultLanguage
        updateLanguageToApply()
        if (selectedCCPCountry != null) {
            val country: CCPCountry? = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                mContext,
                getLanguageToApply(),
                selectedCCPCountry!!.name
            )
            if (country != null) {
                selectedCountry = country
            }
        }
    }

    @JvmName("setHolderView1")
    private fun setHolderView(holderView: View) {
        this.holderView = holderView
    }

    @JvmName("setHolder1")
    private fun setHolder(holder: RelativeLayout) {
        this.holder = holder
    }

    /**
     * if true, this will give explicit close icon in CCP dialog
     *
     * @param showCloseIcon
     */
    fun showCloseIcon(showCloseIcon: Boolean) {
        isShowCloseIcon = showCloseIcon
    }

    @JvmName("getEditTextRegisteredCarrierNumber1")
    fun getEditTextRegisteredCarrierNumber(): EditText? {
//        Log.d(TAG, "getEditText_registeredCarrierNumber");
        return edittextRegisteredCarrierNumber
    }

    /**
     * this will register editText and will apply required text watchers
     *
     * @param edittextRegisteredCarrierNumber
     */
    @JvmName("setEditTextRegisteredCarrierNumber1")
    fun setEditTextRegisteredCarrierNumber(edittextRegisteredCarrierNumber: EditText?) {
        this.edittextRegisteredCarrierNumber = edittextRegisteredCarrierNumber
        if (this.edittextRegisteredCarrierNumber!!.hint != null) {
            originalHint = this.edittextRegisteredCarrierNumber!!.hint.toString()
        }
        updateValidityTextWatcher()
        updateFormattingTextWatcher()
        updateHint()
    }

    /**
     * This function will
     * - remove existing, if any, validityTextWatcher
     * - prepare new validityTextWatcher
     * - attach validityTextWatcher
     * - do initial reporting to watcher
     */
    private fun updateValidityTextWatcher() {
        try {
            edittextRegisteredCarrierNumber!!.removeTextChangedListener(validityTextWatcher)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //initial REPORTING
        reportedValidity = isValidFullNumber
        if (phoneNumberValidityChangeListener != null) {
            phoneNumberValidityChangeListener!!.onValidityChanged(reportedValidity)
        }
        validityTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (phoneNumberValidityChangeListener != null) {
                    val currentValidity: Boolean = isValidFullNumber
                    if (currentValidity != reportedValidity) {
                        reportedValidity = currentValidity
                        phoneNumberValidityChangeListener!!.onValidityChanged(reportedValidity)
                    }
                }
            }
        }
        edittextRegisteredCarrierNumber!!.addTextChangedListener(validityTextWatcher)
    }

    private fun getInflater(): LayoutInflater {
        return mInflater
    }

    /**
     * This will be color of dialog background
     *
     * @param dialogBackgroundResId
     */
    fun setDialogBackground(
        @IdRes
        dialogBackgroundResId: Int
    ) {
        this.dialogBackgroundResId = dialogBackgroundResId
    }

    /**
     * Publicly available functions from library
     */
    @JvmName("getDialogTypeFace1")
    fun getDialogTypeFace(): Typeface? {
        return dialogTypeFace
    }

    /**
     * To change font of ccp views
     *
     * @param typeFace
     */
    @JvmName("setDialogTypeFace1")
    fun setDialogTypeFace(typeFace: Typeface?) {
        try {
            dialogTypeFace = typeFace
            dialogTypeFaceStyle = DEFAULT_UNSET
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * this will load preferredCountries based on countryPreference
     */
    fun refreshPreferredCountries() {
        preferredCountries = if (countryPreference == null || countryPreference!!.isEmpty()) {
            null
        } else {
            val localCCPCountryList: MutableList<CCPCountry?> = ArrayList()
            for (nameCode in countryPreference!!.split(",").toTypedArray()) {
                val ccpCountry: CCPCountry? = CCPCountry.getCountryForNameCodeFromCustomMasterList(
                    context,
                    customMasterCountriesList,
                    getLanguageToApply(),
                    nameCode
                )
                if (ccpCountry != null) {
                    if (!isAlreadyInList(
                            ccpCountry,
                            localCCPCountryList
                        )) { //to avoid duplicate entry of country
                        localCCPCountryList.add(ccpCountry)
                    }
                }
            }
            if (localCCPCountryList.isEmpty()) {
                null
            } else {
                localCCPCountryList
            }
        }
        if (preferredCountries != null) {
            //            Log.d("preference list", preferredCountries.size() + " countries");
            for (preferredCountry in preferredCountries!!) {
                preferredCountry!!.log()
            }
        } else {
            //            Log.d("preference list", " has no country");
        }
    }

    /**
     * this will load preferredCountries based on countryPreference
     */
    fun refreshCustomMasterList() {
        //if no custom list specified then check for exclude list
        if (customMasterCountriesParam == null || customMasterCountriesParam!!.isEmpty()) {
            //if excluded param is also blank, then do nothing
            if (excludedCountriesParam != null && excludedCountriesParam!!.isNotEmpty()) {
                excludedCountriesParam = excludedCountriesParam!!.lowercase(Locale.getDefault())
                val libraryMasterList: List<CCPCountry?>? = CCPCountry.getLibraryMasterCountryList(
                    mContext,
                    getLanguageToApply()
                )
                val localCCPCountryList: MutableList<CCPCountry?> = ArrayList()
                for (ccpCountry in libraryMasterList!!) {
                    //if the country name code is in the excluded list, avoid it.
                    if (!excludedCountriesParam!!.contains(
                            ccpCountry!!.nameCode!!.lowercase(Locale.getDefault())
                        )
                    ) {
                        localCCPCountryList.add(ccpCountry)
                    }
                }
                customMasterCountriesList = localCCPCountryList.ifEmpty {
                    null
                }
            } else {
                customMasterCountriesList = null
            }
        } else {
            //else add custom list
            val localCCPCountryList: MutableList<CCPCountry?> = ArrayList()
            for (nameCode in customMasterCountriesParam!!.split(",").toTypedArray()) {
                val ccpCountry: CCPCountry? = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                    context,
                    getLanguageToApply(),
                    nameCode
                )
                if (ccpCountry != null) {
                    if (!isAlreadyInList(
                            ccpCountry,
                            localCCPCountryList
                        )) { //to avoid duplicate entry of country
                        localCCPCountryList.add(ccpCountry)
                    }
                }
            }
            customMasterCountriesList = if (localCCPCountryList.isEmpty()) {
                null
            } else {
                localCCPCountryList
            }
        }
        if (customMasterCountriesList != null) {
            //            Log.d("custom master list:", customMasterCountriesList.size() + " countries");
            for (ccpCountry in customMasterCountriesList!!) {
                ccpCountry!!.log()
            }
        } else {
            //            Log.d("custom master list", " has no country");
        }
    }

    /**
     * To provide definite set of countries when selection dialog is opened.
     * Only custom master countries, if defined, will be there is selection dialog to select from.
     * To set any country in preference, it must be included in custom master countries, if defined
     * When not defined or null or blank is set, it will use library's default master list
     * Custom master list will only limit the visibility of irrelevant country from selection dialog. But all other functions like setCountryForCodeName() or setFullNumber() will consider all the countries.
     *
     * @param customMasterCountriesParam is country name codes separated by comma. e.g. "us,in,nz"
     * if null or "" , will remove custom countries and library default will be used.
     */
    fun setCustomMasterCountries(customMasterCountriesParam: String?) {
        this.customMasterCountriesParam = customMasterCountriesParam
    }

    /**
     * This can be used to remove certain countries from the list by keeping all the others.
     * This will be ignored if you have specified your own country master list.
     *
     * @param excludedCountries is country name codes separated by comma. e.g. "us,in,nz"
     * null or "" means no country is excluded.
     */
    fun setExcludedCountries(excludedCountries: String?) {
        excludedCountriesParam = excludedCountries
        refreshCustomMasterList()
    }

    /**
     * @return true if ccp is enabled for click
     */
    fun isCcpClickable(): Boolean {
        return ccpClickable
    }

    /**
     * Allow click and open dialog
     *
     * @param ccpClickable
     */
    @JvmName("setCcpClickable1")
    fun setCcpClickable(ccpClickable: Boolean) {
        this.ccpClickable = ccpClickable
        if (!ccpClickable) {
            relativeClickConsumer!!.setOnClickListener(null)
            relativeClickConsumer!!.isClickable = false
            relativeClickConsumer!!.isEnabled = false
        } else {
            relativeClickConsumer!!.setOnClickListener(countryCodeHolderClickListener)
            relativeClickConsumer!!.isClickable = true
            relativeClickConsumer!!.isEnabled = true
        }
    }

    /**
     * This will match name code of all countries of list against the country's name code.
     *
     * @param countryToCheck
     * @param existingCountries list of countries against which country will be checked.
     * @return if country name code is found in list, returns true else return false
     */
    private fun isAlreadyInList(
        countryToCheck: CCPCountry?,
        existingCountries: List<CCPCountry?>?
    ): Boolean {
        if (countryToCheck != null && existingCountries != null) {
            for (iterationCCPCountry in existingCountries) {
                if (iterationCCPCountry!!.nameCode
                        .equals(countryToCheck.nameCode, ignoreCase = true)
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * This function removes possible country code from fullNumber and set rest of the number as carrier number.
     *
     * @param fullNumber combination of country code and carrier number.
     * @param selectedCountry selected country in CCP to detect country code part.
     */
    private fun detectCarrierNumber(fullNumber: String?, selectedCountry: CCPCountry?): String? {
        val carrierNumber: String? = if (selectedCountry == null || fullNumber == null || fullNumber.isEmpty()) {
            fullNumber
        } else {
            val indexOfCode = selectedCountry.phoneCode?.let { fullNumber.indexOf(it) }
            if (indexOfCode == -1) {
                fullNumber
            } else {
                fullNumber.substring(indexOfCode!! + selectedCountry.phoneCode!!.length)
            }
        }
        return carrierNumber
    }

    /**
     * Related to selected country
     */
    //add entry here
    private fun getLanguageEnum(index: Int): Language {
        return if (index < Language.entries.size) {
            Language.entries[index]
        } else {
            Language.ENGLISH
        }
    }

    /**
     * @return If custom text provider is registered, it will return value from provider else default.
     */
    val dialogTitle: String?
        get() {
            val defaultTitle: String? = CCPCountry.getDialogTitle(mContext, getLanguageToApply())
            return if (customDialogTextProvider != null) {
                customDialogTextProvider!!.getCCPDialogTitle(getLanguageToApply(), defaultTitle)
            } else {
                defaultTitle
            }
        }

    /**
     * @return If custom text provider is registered, it will return value from provider else default.
     */
    val searchHintText: String?
        get() {
            val defaultHint: String? = CCPCountry.getSearchHintMessage(
                mContext,
                getLanguageToApply()
            )
            return if (customDialogTextProvider != null) {
                customDialogTextProvider!!.getCCPDialogSearchHintText(
                    getLanguageToApply(),
                    defaultHint
                )
            } else {
                defaultHint
            }
        }

    /**
     * @return If custom text provider is registered, it will return value from provider else default.
     */
    val noResultACK: String?
        get() {
            val defaultNoResultACK: String? = CCPCountry.getNoResultFoundAckMessage(
                mContext,
                getLanguageToApply()
            )
            return if (customDialogTextProvider != null) {
                customDialogTextProvider!!.getCCPDialogNoResultACK(
                    getLanguageToApply(),
                    defaultNoResultACK
                )
            } else {
                defaultNoResultACK
            }
        }

    /**
     * This method is not encouraged because this might set some other country which have same country code as of yours. e.g 1 is common for US and canada.
     * If you are trying to set US ( and countryPreference is not set) and you pass 1 as @param defaultCountryCode, it will set canada (prior in list due to alphabetical order)
     * Rather use @function setDefaultCountryUsingNameCode("us"); or setDefaultCountryUsingNameCode("US");
     *
     *
     * Default country code defines your default country.
     * Whenever invalid / improper number is found in setCountryForPhoneCode() /  setFullNumber(), it CCP will set to default country.
     * This function will not set default country as selected in CCP. To set default country in CCP call resetToDefaultCountry() right after this call.
     * If invalid defaultCountryCode is applied, it won't be changed.
     *
     * @param defaultCountryCode code of your default country
     * if you want to set IN +91(India) as default country, defaultCountryCode =  91
     * if you want to set JP +81(Japan) as default country, defaultCountryCode =  81
     */
    @Deprecated("")
    fun setDefaultCountryUsingPhoneCode(defaultCountryCode: Int) {
        val defaultCCPCountry: CCPCountry? = CCPCountry.getCountryForCode(
            context,
            getLanguageToApply(),
            preferredCountries,
            defaultCountryCode
        ) //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCCPCountry == null) { //if no correct country is found
            //            Log.d(TAG, "No country for code " + defaultCountryCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryCode = defaultCountryCode
        }
    }

    /**
     * Default country name code defines your default country.
     * Whenever invalid / improper name code is found in setCountryForNameCode(), CCP will set to default country.
     * This function will not set default country as selected in CCP. To set default country in CCP call resetToDefaultCountry() right after this call.
     * If invalid defaultCountryCode is applied, it won't be changed.
     *
     * @param defaultCountryNameCode code of your default country
     * if you want to set IN +91(India) as default country, defaultCountryCode =  "IN" or "in"
     * if you want to set JP +81(Japan) as default country, defaultCountryCode =  "JP" or "jp"
     */
    fun setDefaultCountryUsingNameCode(defaultCountryNameCode: String?) {
        val defaultCCPCountry: CCPCountry? = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
            context,
            getLanguageToApply(),
            defaultCountryNameCode
        ) //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCCPCountry == null) { //if no correct country is found
            //            Log.d(TAG, "No country for nameCode " + defaultCountryNameCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryNameCode = defaultCCPCountry.nameCode
        }
    }

    /**
     * @return: Country Code of default country
     * i.e if default country is IN +91(India)  returns: "91"
     * if default country is JP +81(Japan) returns: "81"
     */
    fun getDefaultCountryCode(): String? {
        return defaultCountry!!.phoneCode
    }

    /**
     * * To get code of default country as Integer.
     *
     * @return integer value of default country code in CCP
     * i.e if default country is IN +91(India)  returns: 91
     * if default country is JP +81(Japan) returns: 81
     */
    val defaultCountryCodeAsInt: Int
        get() {
            var code = 0
            try {
                code = getDefaultCountryCode()!!.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return code
        }

    /**
     * To get code of default country with prefix "+".
     *
     * @return String value of default country code in CCP with prefix "+"
     * i.e if default country is IN +91(India)  returns: "+91"
     * if default country is JP +81(Japan) returns: "+81"
     */
    val defaultCountryCodeWithPlus: String
        get() = "+" + getDefaultCountryCode()

    /**
     * To get name of default country.
     *
     * @return String value of country name, default in CCP
     * i.e if default country is IN +91(India)  returns: "India"
     * if default country is JP +81(Japan) returns: "Japan"
     */
    val defaultCountryName: String?
        get() {
            val dc = defaultCountry ?: return ""
            return dc.name
        }

    /**
     * To get name code of default country.
     *
     * @return String value of country name, default in CCP
     * i.e if default country is IN +91(India)  returns: "IN"
     * if default country is JP +81(Japan) returns: "JP"
     */
    @JvmName("getDefaultCountryNameCode1")
    fun getDefaultCountryNameCode(): String {
        val dc = defaultCountry ?: return ""
        return dc.nameCode!!.uppercase(Locale.getDefault())
    }

    /**
     * reset the default country as selected country.
     */
    fun resetToDefaultCountry() {
        defaultCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
            context,
            getLanguageToApply(),
            getDefaultCountryNameCode()
        )
        selectedCountry = defaultCountry
    }

    /**
     * To get code of selected country.
     *
     * @return String value of selected country code in CCP
     * i.e if selected country is IN +91(India)  returns: "91"
     * if selected country is JP +81(Japan) returns: "81"
     */
    private val selectedCountryCode: String?
        get() = selectedCountry!!.phoneCode

    /**
     * To get code of selected country with prefix "+".
     *
     * @return String value of selected country code in CCP with prefix "+"
     * i.e if selected country is IN +91(India)  returns: "+91"
     * if selected country is JP +81(Japan) returns: "+81"
     */
    val selectedCountryCodeWithPlus: String
        get() = "+$selectedCountryCode"

    /**
     * * To get code of selected country as Integer.
     *
     * @return integer value of selected country code in CCP
     * i.e if selected country is IN +91(India)  returns: 91
     * if selected country is JP +81(Japan) returns: 81
     */
    val selectedCountryCodeAsInt: Int
        get() {
            var code = 0
            try {
                code = selectedCountryCode!!.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return code
        }

    /**
     * To get name of selected country.
     *
     * @return String value of country name, selected in CCP
     * i.e if selected country is IN +91(India)  returns: "India"
     * if selected country is JP +81(Japan) returns: "Japan"
     */
    val selectedCountryName: String?
        get() = selectedCountry!!.name

    /**
     * To get name of selected country in English.
     *
     * @return String value of country name in English language, selected in CCP
     * i.e if selected country is IN +91(India)  returns: "India" no matter what language is currently selected.
     * if selected country is JP +81(Japan) returns: "Japan"
     */
    val selectedCountryEnglishName: String?
        get() = selectedCountry!!.englishName

    /**
     * To get name code of selected country.
     *
     * @return String value of country name, selected in CCP
     * i.e if selected country is IN +91(India)  returns: "IN"
     * if selected country is JP +81(Japan) returns: "JP"
     */
    val selectedCountryNameCode: String
        get() = selectedCountry!!.nameCode!!.uppercase(Locale.getDefault())

    /**
     * To get selected country image resource id
     *
     * @return integer value of the selected country flag resource.
     * For example for georgia it returns R.drawable.flag_georgia
     */
    /*val selectedCountryFlagResourceId: Drawable?
        get() = selectedCountry!!.flagResID*/

    /**
     * This will set country with @param countryCode as country code, in CCP
     *
     * @param countryCode a valid country code.
     * If you want to set IN +91(India), countryCode= 91
     * If you want to set JP +81(Japan), countryCode= 81
     */
    fun setCountryForPhoneCode(countryCode: Int) {
        val ccpCountry: CCPCountry? = CCPCountry.getCountryForCode(
            context,
            getLanguageToApply(),
            preferredCountries,
            countryCode
        ) //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (ccpCountry == null) {
            if (defaultCountry == null) {
                defaultCountry = CCPCountry.getCountryForCode(
                    context,
                    getLanguageToApply(),
                    preferredCountries,
                    defaultCountryCode
                )
            }
            selectedCountry = defaultCountry
        } else {
            selectedCountry = ccpCountry
        }
    }

    /**
     * This will set country with @param countryNameCode as country name code, in CCP
     *
     * @param countryNameCode a valid country name code.
     * If you want to set IN +91(India), countryCode= IN
     * If you want to set JP +81(Japan), countryCode= JP
     */
    fun setCountryForNameCode(countryNameCode: String?) {
        val country: CCPCountry? = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
            context,
            getLanguageToApply(),
            countryNameCode
        ) //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (country == null) {
            if (defaultCountry == null) {
                defaultCountry = CCPCountry.getCountryForCode(
                    context,
                    getLanguageToApply(),
                    preferredCountries,
                    defaultCountryCode
                )
            }
            selectedCountry = defaultCountry
        } else {
            selectedCountry = country
        }
    }

    /**
     * All functions that work with fullNumber need an editText to write and read carrier number of full number.
     * An editText for carrier number must be registered in order to use functions like setFullNumber() and getFullNumber().
     *
     * @param editTextCarrierNumber - an editText where user types carrier number ( the part of full number other than country code).
     */
    fun registerCarrierNumberEditText(editTextCarrierNumber: EditText?) {
        setEditTextRegisteredCarrierNumber(editTextCarrierNumber)
    }

    /**
     * If edittext was already registered, this will remove attached textWatchers and set
     * editText to null
     */
    fun deregisterCarrierNumberEditText() {
        if (edittextRegisteredCarrierNumber != null) {
            // remove validity listener
            try {
                edittextRegisteredCarrierNumber!!.removeTextChangedListener(validityTextWatcher)
            } catch (_: Exception) {
            }

            // if possible, remove formatting textWatcher
            try {
                edittextRegisteredCarrierNumber!!.removeTextChangedListener(formattingTextWatcher)
            } catch (_: Exception) {
            }
            edittextRegisteredCarrierNumber!!.hint = ""
            edittextRegisteredCarrierNumber = null
        }
    }

    @get:Throws(NumberParseException::class)
    private val enteredPhoneNumber: PhoneNumber
        get() {
            var carrierNumber: String? = ""
            if (edittextRegisteredCarrierNumber != null) {
                carrierNumber = PhoneNumberUtil.normalizeDigitsOnly(edittextRegisteredCarrierNumber!!.text.toString())
            }
            return getPhoneUtil()!!.parse(carrierNumber, selectedCountryNameCode)
        }
    /**
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "918866667722"
     */
    /**
     * Separate out country code and carrier number from fullNumber.
     * Sets country of separated country code in CCP and carrier number as text of editTextCarrierNumber
     * If no valid country code is found from full number, CCP will be set to default country code and full number will be set as carrier number to editTextCarrierNumber.
     *
     * @param fullNumber is combination of country code and carrier number, (country_code+carrier_number) for example if country is India (+91) and carrier/mobile number is 8866667722 then full number will be 9188666667722 or +918866667722. "+" in starting of number is optional.
     */
    var fullNumber: String?
        get() = try {
            val phoneNumber = enteredPhoneNumber
            getPhoneUtil()!!.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
                .substring(1)
        } catch (_: NumberParseException) {
            Log.e(TAG, "getFullNumber: Could not parse number")
            selectedCountryCode + PhoneNumberUtil.normalizeDigitsOnly(
                edittextRegisteredCarrierNumber!!.text.toString()
            )
        }
        set(fullNumber) {
            var country: CCPCountry? = CCPCountry.getCountryForNumber(
                context,
                getLanguageToApply(),
                preferredCountries,
                fullNumber
            )
            if (country == null) country = defaultCountry
            selectedCountry = country
            val carrierNumber = detectCarrierNumber(fullNumber, country)
            if (getEditTextRegisteredCarrierNumber() != null) {
                getEditTextRegisteredCarrierNumber()!!.setText(carrierNumber)
                updateFormattingTextWatcher()
            } else {
                Log.w(
                    TAG,
                    "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber()."
                )
            }
        }

    /**
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber
     * This will return formatted number.
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "918866667722"
     */
    val formattedFullNumber: String
        get() = try {
            val phoneNumber = enteredPhoneNumber
            "+" + getPhoneUtil()!!.format(
                phoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            )
                .substring(1)
        } catch (_: NumberParseException) {
            Log.e(TAG, "getFullNumber: Could not parse number")
            fullNumberWithPlus
        }

    /**
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber and prefix "+"
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "+918866667722"
     */
    val fullNumberWithPlus: String
        get() = "+$fullNumber"

    /**
     * @return content color of Country Code Picker's text and small downward arrow.
     */
    @JvmName("getContentColor1")
    fun getContentColor(): Int {
        return contentColor
    }

    /**
     * Sets text and small down arrow color of CCP.
     *
     * @param contentColor color to apply to text and down arrow
     */
    @JvmName("setContentColor1")
    fun setContentColor(contentColor: Int) {
        this.contentColor = contentColor
        textviewSelectedCountry!!.setTextColor(this.contentColor)

        //change arrow color only if explicit arrow color is not specified.
        if (arrowColor == DEFAULT_UNSET) {
            imageViewArrow!!.setColorFilter(this.contentColor, PorterDuff.Mode.SRC_IN)
        }
    }

    /**
     * set small down arrow color of CCP.
     *
     * @param arrowColor color to apply to text and down arrow
     */
    @JvmName("setArrowColor1")
    fun setArrowColor(arrowColor: Int) {
        this.arrowColor = arrowColor
        if (this.arrowColor == DEFAULT_UNSET) {
            if (contentColor != DEFAULT_UNSET) {
                imageViewArrow!!.setColorFilter(contentColor, PorterDuff.Mode.SRC_IN)
            }
        } else {
            imageViewArrow!!.setColorFilter(this.arrowColor, PorterDuff.Mode.SRC_IN)
        }
    }

    /**
     * Sets flag border color of CCP.
     *
     * @param borderFlagColor color to apply to flag border
     */
    fun setFlagBorderColor(borderFlagColor: Int) {
        this.borderFlagColor = borderFlagColor
//        linearFlagBorder!!.setBackgroundColor(this.borderFlagColor)
    }

    /**
     * Modifies size of text in side CCP view.
     *
     * @param textSize size of text in pixels
     */
    fun setTextSize(textSize: Int) {
        if (textSize > 0) {
            textviewSelectedCountry!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            setArrowSize(textSize)
            setFlagSize(textSize)
        }
    }

    /**
     * Modifies size of downArrow in CCP view
     *
     * @param arrowSize size in pixels
     */
    fun setArrowSize(arrowSize: Int) {
        if (arrowSize > 0) {
            val params = imageViewArrow!!.layoutParams as LayoutParams
            params.width = arrowSize
            params.height = arrowSize
            imageViewArrow!!.layoutParams = params
        }
    }

    /**
     * If nameCode of country in CCP view is not required use this to show/hide country name code of ccp view.
     *
     * @param showNameCode true will show country name code in ccp view, it will result " (IN) +91 "
     * false will remove country name code from ccp view, it will result  " +91 "
     */
    fun showNameCode(showNameCode: Boolean) {
        this.showNameCode = showNameCode
        selectedCountry = selectedCCPCountry
    }

    /**
     * This can change visibility of arrow.
     *
     * @param showArrow true will show arrow and false will hide arrow from there.
     */
    fun showArrow(showArrow: Boolean) {
        this.showArrow = showArrow
        refreshArrowViewVisibility()
    }

    /**
     * This will set preferred countries using their name code. Prior preferred countries will be replaced by these countries.
     * Preferred countries will be at top of country selection box.
     * If more than one countries have same country code, country in preferred list will have higher priory than others. e.g. Canada and US have +1 as their country code. If "us" is set as preferred country then US will be selected whenever setCountryForPhoneCode(1); or setFullNumber("+1xxxxxxxxx"); is called.
     *
     * @param countryPreference is country name codes separated by comma. e.g. "us,in,nz"
     */
    @JvmName("setCountryPreference1")
    fun setCountryPreference(countryPreference: String?) {
        this.countryPreference = countryPreference
    }

    /**
     * Language will be applied to country select dialog
     * If autoDetectCountry is true, ccp will try to detect language from locale.
     * Detected language is supported If no language is detected or detected language is not supported by ccp, it will set default language as set.
     *
     * @param language
     */
    fun changeDefaultLanguage(language: Language) {
        setCustomDefaultLanguage(language)
    }

    /**
     * To change font of ccp views
     *
     * @param typeFace
     */
    fun setTypeFace(typeFace: Typeface?) {
        try {
            textviewSelectedCountry!!.typeface = typeFace
            setDialogTypeFace(typeFace)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * To change font of ccp views along with style.
     *
     * @param typeFace
     * @param style
     */
    fun setDialogTypeFace(typeFace: Typeface?, style: Int) {
        var style = style
        try {
            dialogTypeFace = typeFace
            if (dialogTypeFace == null) {
                style = DEFAULT_UNSET
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * To change font of ccp views along with style.
     *
     * @param typeFace
     * @param style
     */
    fun setTypeFace(typeFace: Typeface?, style: Int) {
        try {
            textviewSelectedCountry!!.setTypeface(typeFace, style)
            setDialogTypeFace(typeFace, style)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * To get call back on country selection a onCountryChangeListener must be registered.
     *
     * @param onCountryChangeListener
     */
    fun setOnCountryChangeListener(onCountryChangeListener: OnCountryChangeListener?) {
        this.onCountryChangeListener = onCountryChangeListener
    }

    /**
     * Modifies size of flag in CCP view
     *
     * @param flagSize size in pixels
     */
    fun setFlagSize(flagSize: Int) {
        imageViewFlag!!.layoutParams.height = flagSize
        imageViewFlag!!.requestLayout()
    }

    fun showFlag(showFlag: Boolean) {
        this.showFlag = showFlag
        refreshFlagVisibility()
        if (!isInEditMode) selectedCountry = selectedCCPCountry
    }

    private fun refreshFlagVisibility() {
        if (showFlag) {
            if (ccpUseEmoji) {
                linearFlagHolder!!.visibility = GONE
            } else {
                linearFlagHolder!!.visibility = VISIBLE
            }
        } else {
            linearFlagHolder!!.visibility = GONE
        }
    }

    fun useFlagEmoji(useFlagEmoji: Boolean) {
        ccpUseEmoji = useFlagEmoji
        refreshFlagVisibility()
        selectedCountry = selectedCCPCountry
    }

    fun showFullName(showFullName: Boolean) {
        this.showFullName = showFullName
        selectedCountry = selectedCCPCountry
    }

    /**
     * Sets validity change listener.
     * First call back will be sent right away.
     *
     * @param phoneNumberValidityChangeListener
     */
    fun setPhoneNumberValidityChangeListener(phoneNumberValidityChangeListener: PhoneNumberValidityChangeListener?) {
        this.phoneNumberValidityChangeListener = phoneNumberValidityChangeListener
        if (edittextRegisteredCarrierNumber != null && phoneNumberValidityChangeListener != null) {
            reportedValidity = isValidFullNumber
            phoneNumberValidityChangeListener.onValidityChanged(reportedValidity)
        }
    }

    /**
     * Sets failure listener.
     *
     * @param failureListener
     */
    fun setAutoDetectionFailureListener(failureListener: FailureListener?) {
        this.failureListener = failureListener
    }

    /**
     * If developer wants to change CCP Dialog's Title, Search Hint text or no result ACK,
     * a custom dialog text provider should be set.
     *
     * @param customDialogTextProvider
     */
    fun setCustomDialogTextProvider(customDialogTextProvider: CustomDialogTextProvider?) {
        this.customDialogTextProvider = customDialogTextProvider
    }
    /**
     * Manually trigger selection dialog and set
     * scroll position to specified country.
     */
    /**
     * Opens country selection dialog.
     * By default this is called from ccp click.
     * Developer can use this to trigger manually.
     */
    @JvmOverloads
    fun launchCountrySelectionDialog(countryNameCode: String? = null) {
        countryCodeBottomSheet = CountryCodeBottomSheet(codePicker!!, countryNameCode)
        countryCodeBottomSheet.apply {
            show(manager, tag)
        }
//        CountryCodeDialog.openCountryCodeDialog(codePicker, countryNameCode)
    }//            when number could not be parsed, its not valid

    /**
     * This function will check the validity of entered number.
     * It will use PhoneNumberUtil to check validity
     *
     * @return true if entered carrier number is valid else false
     */
    val isValidFullNumber: Boolean
        get() = try {
            if (getEditTextRegisteredCarrierNumber() != null && getEditTextRegisteredCarrierNumber()!!.text.isNotEmpty()) {
                val phoneNumber = getPhoneUtil()!!.parse(
                    "+" + selectedCCPCountry!!.phoneCode + getEditTextRegisteredCarrierNumber()!!.text.toString(),
                    selectedCCPCountry!!.nameCode
                )
                getPhoneUtil()!!.isValidNumber(phoneNumber)
            } else if (getEditTextRegisteredCarrierNumber() == null) {
                Toast.makeText(
                    mContext,
                    "No editText for Carrier number found.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                false
            } else {
                false
            }
        } catch (_: NumberParseException) {
            //            when number could not be parsed, its not valid
            false
        }

    @JvmName("getPhoneUtil1")
    private fun getPhoneUtil(): PhoneNumberUtil? {
        if (phoneUtil == null) {
            phoneUtil = PhoneNumberUtil.createInstance(mContext)
        }
        return phoneUtil
    }

    /**
     * loads current country in ccp using locale and telephony manager
     * this will follow specified order in countryAutoDetectionPref
     *
     * @param loadDefaultWhenFails: if all of pref methods fail to detect country then should this
     * function load default country or not is decided with this flag
     */
    private fun setAutoDetectedCountry(loadDefaultWhenFails: Boolean) {
        try {
            var successfullyDetected = false
            for (i in 0 until selectedAutoDetectionPref.representation.length) {
                when (selectedAutoDetectionPref.representation[i]) {
                    '1' -> successfullyDetected = detectSIMCountry(false)
                    '2' -> successfullyDetected = detectNetworkCountry(false)
                    '3' -> successfullyDetected = detectLocaleCountry(false)
                }
                if (successfullyDetected) {
                    break
                } else {
                    if (failureListener != null) {
                        failureListener!!.onCountryAutoDetectionFailed()
                    }
                }
            }
            if (!successfullyDetected && loadDefaultWhenFails) {
                resetToDefaultCountry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w(TAG, "setAutoDetectCountry: Exception" + e.message)
            if (loadDefaultWhenFails) {
                resetToDefaultCountry()
            }
        }
    }

    /**
     * This will detect country from SIM info and then load it into CCP.
     *
     * @param loadDefaultWhenFails true if want to reset to default country when sim country cannot be detected. if false, then it
     * will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    fun detectSIMCountry(loadDefaultWhenFails: Boolean): Boolean {
        return try {
            val telephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountryISO = telephonyManager.simCountryIso
            if (simCountryISO == null || simCountryISO.isEmpty() || !isNameCodeInCustomMasterList(
                    simCountryISO
                )) {
                if (loadDefaultWhenFails) {
                    resetToDefaultCountry()
                }
                return false
            }
            selectedCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                context,
                getLanguageToApply(),
                simCountryISO
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (loadDefaultWhenFails) {
                resetToDefaultCountry()
            }
            false
        }
    }

    private fun isNameCodeInCustomMasterList(nameCode: String): Boolean {
        val allowedList: List<CCPCountry?> = CCPCountry.getCustomMasterCountryList(mContext, this)!!
        for (country in allowedList) {
            if (country!!.nameCode.equals(nameCode, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    /**
     * This will detect country from NETWORK info and then load it into CCP.
     *
     * @param loadDefaultWhenFails true if want to reset to default country when network country cannot be detected. if false, then it
     * will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    fun detectNetworkCountry(loadDefaultWhenFails: Boolean): Boolean {
        return try {
            val telephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkCountryISO = telephonyManager.networkCountryIso
            if (networkCountryISO == null || networkCountryISO.isEmpty() || !isNameCodeInCustomMasterList(
                    networkCountryISO
                )) {
                if (loadDefaultWhenFails) {
                    resetToDefaultCountry()
                }
                return false
            }
            selectedCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                context,
                getLanguageToApply(),
                networkCountryISO
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (loadDefaultWhenFails) {
                resetToDefaultCountry()
            }
            false
        }
    }

    /**
     * This will detect country from LOCALE info and then load it into CCP.
     *
     * @param loadDefaultWhenFails true if want to reset to default country when locale country cannot be detected. if false, then it
     * will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    fun detectLocaleCountry(loadDefaultWhenFails: Boolean): Boolean {
        return try {
            val localeCountryISO = mContext.resources.configuration.locales[0].country
            if (localeCountryISO.isEmpty() || !isNameCodeInCustomMasterList(localeCountryISO)) {
                if (loadDefaultWhenFails) {
                    resetToDefaultCountry()
                }
                return false
            }
            selectedCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(
                context,
                getLanguageToApply(),
                localeCountryISO
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (loadDefaultWhenFails) {
                resetToDefaultCountry()
            }
            false
        }
    }

    /**
     * This will update the pref for country auto detection.
     * Remember, this will not call setAutoDetectedCountry() to update country. This must be called separately.
     *
     * @param selectedAutoDetectionPref new detection pref
     */
    fun setCountryAutoDetectionPref(selectedAutoDetectionPref: AutoDetectionPref) {
        this.selectedAutoDetectionPref = selectedAutoDetectionPref
    }

    fun onUserTappedCountry(selectedCountry: CCPCountry?) {
        if (codePicker!!.rememberLastSelection) {
            codePicker!!.storeSelectedCountryNameCode(selectedCountry!!.nameCode)
        }
        this@CountryCodePicker.selectedCountry = selectedCountry
    }

    @JvmName("setDetectCountryWithAreaCode1")
    fun setDetectCountryWithAreaCode(detectCountryWithAreaCode: Boolean) {
        this.detectCountryWithAreaCode = detectCountryWithAreaCode
        updateFormattingTextWatcher()
    }

    @JvmName("setHintExampleNumberEnabled1")
    fun setHintExampleNumberEnabled(hintExampleNumberEnabled: Boolean) {
        this.hintExampleNumberEnabled = hintExampleNumberEnabled
        updateHint()
    }

    @JvmName("setHintExampleNumberType1")
    fun setHintExampleNumberType(hintExampleNumberType: PhoneNumberType) {
        this.hintExampleNumberType = hintExampleNumberType
        updateHint()
    }

    fun setTalkBackTextProvider(talkBackTextProvider: CCPTalkBackTextProvider?) {
        this.talkBackTextProvider = talkBackTextProvider
        selectedCountry = selectedCCPCountry
    }

    /**
     * This will decide initial scroll position of countries list in dialog.
     *
     * @param initialScrollToSelection : false -> show list without any scroll
     * true -> will scroll to the position of the selected country.
     * Note: if selected country is a preferred country,
     * then it will not scroll and show full preferred countries list.
     */
    fun enableDialogInitialScrollToSelection(initialScrollToSelection: Boolean) {
    }

    /**
     * To listen to the click handle action manually,
     * a custom clickListener must be set.
     * This will override the default click listener which opens the selection dialog.
     *
     * @param clickListener will start receiving click callbacks. If null then default click listener
     * will receive callback and selection dialog will be prompted.
     */
    fun overrideClickListener(clickListener: OnClickListener?) {
        customClickListener = clickListener
    }

    override fun onDetachedFromWindow() {
        if (::countryCodeBottomSheet.isInitialized)
            countryCodeBottomSheet.clear()
//        CountryCodeDialog.clear()
        super.onDetachedFromWindow()
    }

    /**
     * Update every time new language is supported #languageSupport
     */
    //add an entry for your language in attrs.xml's <attr name="language" format="enum"> enum.
    //add here so that language can be set programmatically
    enum class Language {
        AFRIKAANS("af"), ARABIC("ar"), BASQUE("eu"), BENGALI("bn"), CHINESE_SIMPLIFIED(
            "zh",
            "CN",
            "Hans"
        ),
        CHINESE_TRADITIONAL(
            "zh",
            "TW",
            "Hant"
        ),
        CZECH("cs"), DANISH("da"), DUTCH("nl"), ENGLISH("en"), FARSI("fa"), FRENCH("fr"), GERMAN("de"), GREEK(
            "el"
        ),
        GUJARATI("gu"), HEBREW("iw"), HINDI("hi"), HUNGARIAN("hu"), INDONESIA("in"), ITALIAN("it"), JAPANESE(
            "ja"
        ),
        KAZAKH("kk"), KOREAN("ko"), MARATHI("mr"), POLISH("pl"), PORTUGUESE("pt"), PUNJABI("pa"), RUSSIAN(
            "ru"
        ),
        SLOVAK("sk"), SLOVENIAN("si"), SPANISH("es"), SWEDISH("sv"), TAGALOG("tl"), THAI("th"), TURKISH(
            "tr"
        ),
        UKRAINIAN("uk"), URDU("ur"), UZBEK("uz"), VIETNAMESE("vi");

        var code: String
        var country: String? = null
        var script: String? = null

        constructor(code: String, country: String, script: String) {
            this.code = code
            this.country = country
            this.script = script
        }

        constructor(code: String) {
            this.code = code
        }

        companion object {
            fun forCountryNameCode(code: String): Language {
                var lang = ENGLISH
                for (language in entries) {
                    if (language.code == code) {
                        lang = language
                    }
                }
                return lang
            }
        }
    }

    enum class PhoneNumberType {
        MOBILE, FIXED_LINE,  // In some regions (e.g. the USA), it is impossible to distinguish between fixed-line and

        // mobile numbers by looking at the phone number itself.
        FIXED_LINE_OR_MOBILE,  // Freephone lines
        TOLL_FREE, PREMIUM_RATE,  // The cost of this call is shared between the caller and the recipient, and is hence typically

        // less than PREMIUM_RATE calls. See // http://en.wikipedia.org/wiki/Shared_Cost_Service for
        // more information.
        SHARED_COST,  // Voice over IP numbers. This includes TSoIP (Telephony Service over IP).
        VOIP,  // A personal number is associated with a particular person, and may be routed to either a

        // MOBILE or FIXED_LINE number. Some more information can be found here:
        // http://en.wikipedia.org/wiki/Personal_Numbers
        PERSONAL_NUMBER, PAGER,  // Used for "Universal Access Numbers" or "Company Numbers". They may be further routed to

        // specific offices, but allow one number to be used for a company.
        UAN,  // Used for "Voice Mail Access Numbers".
        VOICEMAIL,  // A phone number is of type UNKNOWN when it does not fit any of the known patterns for a

        // specific region.
        UNKNOWN
    }

    enum class AutoDetectionPref(var representation: String) {
        SIM_ONLY("1"), NETWORK_ONLY("2"), LOCALE_ONLY("3"), SIM_NETWORK("12"), NETWORK_SIM("21"), SIM_LOCALE(
            "13"
        ),
        LOCALE_SIM("31"), NETWORK_LOCALE("23"), LOCALE_NETWORK("32"), SIM_NETWORK_LOCALE("123"), SIM_LOCALE_NETWORK(
            "132"
        ),
        NETWORK_SIM_LOCALE("213"), NETWORK_LOCALE_SIM("231"), LOCALE_SIM_NETWORK("312"), LOCALE_NETWORK_SIM(
            "321"
        );

        companion object {
            fun getPrefForValue(value: String): AutoDetectionPref {
                for (autoDetectionPref in entries) {
                    if (autoDetectionPref.representation == value) {
                        return autoDetectionPref
                    }
                }
                return SIM_NETWORK_LOCALE
            }
        }
    }

    /**
     * When width is "match_parent", this gravity will decide the placement of text.
     */
    enum class TextGravity(var enumIndex: Int) {
        LEFT(-1), CENTER(0), RIGHT(1)
    }

    /**
     * interface to set change listener
     */
    interface OnCountryChangeListener {
        fun onCountrySelected()
    }

    /**
     * interface to listen to failure events
     */
    interface FailureListener {
        //when country auto detection failed.
        fun onCountryAutoDetectionFailed()
    }

    /**
     * Interface to check phone number validity change listener
     */
    interface PhoneNumberValidityChangeListener {
        fun onValidityChanged(isValidNumber: Boolean)
    }

    interface DialogEventsListener {
        fun onCcpDialogOpen(dialog: Dialog?)
        fun onCcpDialogDismiss(dialogInterface: DialogInterface?)
        fun onCcpDialogCancel(dialogInterface: DialogInterface?)
    }

    interface CustomDialogTextProvider {
        fun getCCPDialogTitle(language: Language?, defaultTitle: String?): String?
        fun getCCPDialogSearchHintText(language: Language?, defaultSearchHintText: String?): String?
        fun getCCPDialogNoResultACK(language: Language?, defaultNoResultACK: String?): String?
    }

    companion object {
        const val DEFAULT_UNSET = -99
        var TAG = "CCP"
        var LIB_DEFAULT_COUNTRY_CODE = 91
        private const val TEXT_GRAVITY_CENTER = 0
        private const val ANDROID_NAME_SPACE = "http://schemas.android.com/apk/res/android"
    }
}