package com.digitalmid.seograph_webmasters_tool

import android.content.Context
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.SearchView
import android.widget.ListView

/**
 * Created by dr_success on 9/4/2017.
 */

class Geo(val mcontext: Context) {


    //this will hold filtered list,
//also after filtered list item is clicked ,
//we ca retrive the data using the position
//from this list because the position would have been reaarranged and messed
//up
    var filteredList: MutableList<Country> = mutableListOf()

    //country picker
    fun countryPicker(onCountrySelected: (country: Country) -> Unit) {

        val dialog = AppCompatDialog(mcontext)

        //set content view
        dialog.setContentView(R.layout.country_picker_dialog)

        val listView = dialog.findViewById<ListView>(R.id.country_listview)

        val allCountryList = getAllCountries()

        //adapter
        val adapter = CountryPickerListAdapter(
                mcontext, allCountryList
        )//end adapter

        //set adapter
        listView!!.adapter = adapter

        //search view
        val searchView = dialog.findViewById<SearchView>(R.id.country_search)

        //listen to query text
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            /**
             * onQueryTextChange
             */
            override fun onQueryTextChange(newText: String?): Boolean {

                //filter the data
                filterCountryList(mcontext, newText, listView, allCountryList)

                return true
            }//end if

            /**
             * onQuertTextSubmit
             */
            override fun onQueryTextSubmit(query: String?): Boolean {

                //filter the data
                filterCountryList(mcontext, query, listView, allCountryList)

                return true
            }//end
        })


        //on item picker
        listView.setOnItemClickListener { listview, rowView, position, id ->

            var country: Country

            //lets get the country
            //if filteredList is not empty, we will get the country data
            //from there since the id after filtering will change
            if (filteredList.isNotEmpty()) {
                country = filteredList[position]
            } else {
                country = allCountryList[position]
            }

            //invoke  onCountrySelected
            onCountrySelected.invoke(country)

            //hide
            dialog.dismiss()
        }//end

        dialog.show()
    }//end

    /**
     * filter Data
     */
    fun filterCountryList(
            mcontext: Context,
            filterText: String?,
            listView: ListView,
            countryList: List<Country>
    ): Boolean {

        //if text is empty, we will set,the adapter to full data set
        if (filterText.isNullOrEmpty()) {
            listView.adapter = CountryPickerListAdapter(mcontext, countryList)

            //clear filteredList
            filteredList.clear()

            return true
        }//end if empty

        //lets convert it to charsquence required by .contains
        filterText as CharSequence

        //lets loop and find possible guess
        for (countryData in countryList) {

            var countryName = countryData.name.toLowerCase()

            var ISOAlhpa2 = countryData.isoAlpha2.toLowerCase()

            val countryNameMatched = countryName?.startsWith(filterText, true)

            val isoCodeMatched = ISOAlhpa2.startsWith(filterText, true)

            //if we have our search query
            if (countryNameMatched or isoCodeMatched) {
                //add to filteredList data set
                filteredList.add(0, countryData)
            }//end if

        }//end loop

        //update
        listView.adapter = CountryPickerListAdapter(mcontext, filteredList)


        return true
    }//end if

    //get countries
    fun getAllCountries(): List<Country> {

        //lets create list
        return listOf<Country>(
                Country("Afghanistan", "AF", "AFG", R.drawable.ic_flag_af),
                Country("Åland Islands", "AX", "ALA", R.drawable.ic_flag_ax),
                Country("Albania", "AL", "ALB", R.drawable.ic_flag_al),
                Country("Algeria", "DZ", "DZA", R.drawable.ic_flag_dz),
                Country("American Samoa", "AS", "ASM", R.drawable.ic_flag_as),
                Country("Andorra", "AD", "AND", R.drawable.ic_flag_ad),
                Country("Angola", "AO", "AGO", R.drawable.ic_flag_ao),
                Country("Anguilla", "AI", "AIA", R.drawable.ic_flag_ai),
                Country("Antarctica", "AQ", "ATA", R.drawable.ic_flag_aq),
                Country("Antigua and Barbuda", "AG", "ATG", R.drawable.ic_flag_ag),
                Country("Argentina", "AR", "ARG", R.drawable.ic_flag_ar),
                Country("Armenia", "AM", "ARM", R.drawable.ic_flag_am),
                Country("Aruba", "AW", "ABW", R.drawable.ic_flag_aw),
                Country("Australia", "AU", "AUS", R.drawable.ic_flag_au),
                Country("Austria", "AT", "AUT", R.drawable.ic_flag_at),
                Country("Azerbaijan", "AZ", "AZE", R.drawable.ic_flag_az),
                Country("Bahamas", "BS", "BHS", R.drawable.ic_flag_bs),
                Country("Bahrain", "BH", "BHR", R.drawable.ic_flag_bh),
                Country("Bangladesh", "BD", "BGD", R.drawable.ic_flag_bd),
                Country("Barbados", "BB", "BRB", R.drawable.ic_flag_bb),
                Country("Belarus", "BY", "BLR", R.drawable.ic_flag_by),
                Country("Belgium", "BE", "BEL", R.drawable.ic_flag_be),
                Country("Belize", "BZ", "BLZ", R.drawable.ic_flag_bz),
                Country("Benin", "BJ", "BEN", R.drawable.ic_flag_bj),
                Country("Bermuda", "BM", "BMU", R.drawable.ic_flag_bm),
                Country("Bhutan", "BT", "BTN", R.drawable.ic_flag_bt),
                Country("Bolivia", "BO", "BOL", R.drawable.ic_flag_bo),
                Country("Bonaire, Saint Eustatius And Saba", "BQ", "BES", R.drawable.ic_flag_bq),
                Country("Bosnia and Herzegovina", "BA", "BIH", R.drawable.ic_flag_ba),
                Country("Botswana", "BW", "BWA", R.drawable.ic_flag_bw),
                Country("Bouvet Island", "BV", "BVT", R.drawable.ic_flag_bv),
                Country("Brazil", "BR", "BRA", R.drawable.ic_flag_br),
                Country("British Indian Ocean Territory", "IO", "IOT", R.drawable.ic_flag_io),
                Country("Brunei", "BN", "BRN", R.drawable.ic_flag_bn),
                Country("Bulgaria", "BG", "BGR", R.drawable.ic_flag_bg),
                Country("Burkina Faso", "BF", "BFA", R.drawable.ic_flag_bf),
                Country("Burundi", "BI", "BDI", R.drawable.ic_flag_bi),
                Country("Cabo Verde", "CV", "CPV", R.drawable.ic_flag_cv),
                Country("Cambodia", "KH", "KHM", R.drawable.ic_flag_kh),
                Country("Cameroon", "CM", "CMR", R.drawable.ic_flag_cm),
                Country("Canada", "CA", "CAN", R.drawable.ic_flag_ca),
                Country("Cayman Islands", "KY", "CYM", R.drawable.ic_flag_ky),
                Country("Central African Republic", "CF", "CAF", R.drawable.ic_flag_cf),
                Country("Chad", "TD", "TCD", R.drawable.ic_flag_td),
                Country("Chile", "CL", "CHL", R.drawable.ic_flag_cl),
                Country("China", "CN", "CHN", R.drawable.ic_flag_cn),
                Country("Christmas Island", "CX", "CXR", R.drawable.ic_flag_cx),
                Country("Cocos (Keeling) Islands", "CC", "CCK", R.drawable.ic_flag_cc),
                Country("Colombia", "CO", "COL", R.drawable.ic_flag_co),
                Country("Comoros", "KM", "COM", R.drawable.ic_flag_km),
                Country("Congo", "CG", "COG", R.drawable.ic_flag_cg),
                Country("Cook Islands", "CK", "COK", R.drawable.ic_flag_ck),
                Country("Costa Rica", "CR", "CRI", R.drawable.ic_flag_cr),
                Country("Côte d'Ivoire", "CI", "CIV", R.drawable.ic_flag_ci),
                Country("Croatia", "HR", "HRV", R.drawable.ic_flag_hr),
                Country("Cuba", "CU", "CUB", R.drawable.ic_flag_cu),
                Country("Curaçao", "CW", "CUW", R.drawable.ic_flag_cw),
                Country("Cyprus", "CY", "CYP", R.drawable.ic_flag_cy),
                Country("Czech Republic", "CZ", "CZE", R.drawable.ic_flag_cz),
                Country("Democratic Republic of the Congo", "CD", "ZAR", R.drawable.ic_flag_cd),
                Country("Denmark", "DK", "DNK", R.drawable.ic_flag_dk),
                Country("Djibouti", "DJ", "DJI", R.drawable.ic_flag_dj),
                Country("Dominica", "DM", "DMA", R.drawable.ic_flag_dm),
                Country("Dominican Republic", "DO", "DOM", R.drawable.ic_flag_do),
                Country("Ecuador", "EC", "ECU", R.drawable.ic_flag_ec),
                Country("Egypt", "EG", "EGY", R.drawable.ic_flag_eg),
                Country("El Salvador", "SV", "SLV", R.drawable.ic_flag_sv),
                Country("Equatorial Guinea", "GQ", "GNQ", R.drawable.ic_flag_gq),
                Country("Eritrea", "ER", "ERI", R.drawable.ic_flag_er),
                Country("Estonia", "EE", "EST", R.drawable.ic_flag_ee),
                Country("Ethiopia", "ET", "ETH", R.drawable.ic_flag_et),
                Country("European Union", "EU", "EUR", R.drawable.ic_flag_eu),
                Country("Falkland Islands (Malvinas)", "FK", "FLK", R.drawable.ic_flag_fk),
                Country("Faroe Islands", "FO", "FRO", R.drawable.ic_flag_fo),
                Country("Fiji", "FJ", "FJI", R.drawable.ic_flag_fj),
                Country("Finland", "FI", "FIN", R.drawable.ic_flag_fi),
                Country("France", "FR", "FRA", R.drawable.ic_flag_fr),
                Country("French Guiana", "GF", "GUF", R.drawable.ic_flag_gf),
                Country("French Polynesia", "PF", "PYF", R.drawable.ic_flag_pf),
                Country("French Southern and Antarctic Lands", "TF", "ATF", R.drawable.ic_flag_tf),
                Country("Gabon", "GA", "GAB", R.drawable.ic_flag_ga),
                Country("Georgia", "GE", "GEO", R.drawable.ic_flag_ge),
                Country("Germany", "DE", "DEU", R.drawable.ic_flag_de),
                Country("Ghana", "GH", "GHA", R.drawable.ic_flag_gh),
                Country("Gibraltar", "GI", "GIB", R.drawable.ic_flag_gi),
                Country("Greece", "GR", "GRC", R.drawable.ic_flag_gr),
                Country("Greenland", "GL", "GRL", R.drawable.ic_flag_gl),
                Country("Grenada", "GD", "GRD", R.drawable.ic_flag_gd),
                Country("Guadeloupe", "GP", "GLP", R.drawable.ic_flag_gp),
                Country("Guam", "GU", "GUM", R.drawable.ic_flag_gu),
                Country("Guatemala", "GT", "GTM", R.drawable.ic_flag_gt),
                Country("Guernsey", "GG", "GGY", R.drawable.ic_flag_gg),
                Country("Guinea", "GN", "GIN", R.drawable.ic_flag_gn),
                Country("Guinea-Bissau", "GW", "GNB", R.drawable.ic_flag_gw),
                Country("Guyana", "GY", "GUY", R.drawable.ic_flag_gy),
                Country("Haiti", "HT", "HTI", R.drawable.ic_flag_ht),
                Country("Heard Island And McDonald Islands", "HM", "HMD", R.drawable.ic_flag_hm),
                Country("Honduras", "HN", "HND", R.drawable.ic_flag_hn),
                Country("Hong Kong", "HK", "HKG", R.drawable.ic_flag_hk),
                Country("Hungary", "HU", "HUN", R.drawable.ic_flag_hu),
                Country("Iceland", "IS", "ISL", R.drawable.ic_flag_is),
                Country("India", "IN", "IND", R.drawable.ic_flag_in),
                Country("Indonesia", "ID", "IDN", R.drawable.ic_flag_id),
                Country("Iran", "IR", "IRN", R.drawable.ic_flag_ir),
                Country("Iraq", "IQ", "IRQ", R.drawable.ic_flag_iq),
                Country("Ireland", "IE", "IRL", R.drawable.ic_flag_ie),
                Country("Isle of Man", "IM", "IMN", R.drawable.ic_flag_im),
                Country("Israel", "IL", "ISR", R.drawable.ic_flag_il),
                Country("Italy", "IT", "ITA", R.drawable.ic_flag_it),
                Country("Jamaica", "JM", "JAM", R.drawable.ic_flag_jm),
                Country("Japan", "JP", "JPN", R.drawable.ic_flag_jp),
                Country("Jersey", "JE", "JEY", R.drawable.ic_flag_je),
                Country("Jordan", "JO", "JOR", R.drawable.ic_flag_jo),
                Country("Kazakhstan", "KZ", "KAZ", R.drawable.ic_flag_kz),
                Country("Kenya", "KE", "KEN", R.drawable.ic_flag_ke),
                Country("Kiribati", "KI", "KIR", R.drawable.ic_flag_ki),
                Country("Kuwait", "KW", "KWT", R.drawable.ic_flag_kw),
                Country("Kyrgyzstan", "KG", "KGZ", R.drawable.ic_flag_kg),
                Country("Laos", "LA", "LAO", R.drawable.ic_flag_la),
                Country("Latvia", "LV", "LVA", R.drawable.ic_flag_lv),
                Country("Lebanon", "LB", "LBN", R.drawable.ic_flag_lb),
                Country("Lesotho", "LS", "LSO", R.drawable.ic_flag_ls),
                Country("Liberia", "LR", "LBR", R.drawable.ic_flag_lr),
                Country("Libya", "LY", "LBY", R.drawable.ic_flag_ly),
                Country("Liechtenstein", "LI", "LIE", R.drawable.ic_flag_li),
                Country("Lithuania", "LT", "LTU", R.drawable.ic_flag_lt),
                Country("Luxembourg", "LU", "LUX", R.drawable.ic_flag_lu),
                Country("Macao", "MO", "MAC", R.drawable.ic_flag_mo),
                Country("Macedonia", "MK", "MKD", R.drawable.ic_flag_mk),
                Country("Madagascar", "MG", "MDG", R.drawable.ic_flag_mg),
                Country("Malawi", "MW", "MWI", R.drawable.ic_flag_mw),
                Country("Malaysia", "MY", "MYS", R.drawable.ic_flag_my),
                Country("Maldives", "MV", "MDV", R.drawable.ic_flag_mv),
                Country("Mali", "ML", "MLI", R.drawable.ic_flag_ml),
                Country("Malta", "MT", "MLT", R.drawable.ic_flag_mt),
                Country("Marshall Islands", "MH", "MHL", R.drawable.ic_flag_mh),
                Country("Martinique", "MQ", "MTQ", R.drawable.ic_flag_mq),
                Country("Mauritania", "MR", "MRT", R.drawable.ic_flag_mr),
                Country("Mauritius", "MU", "MUS", R.drawable.ic_flag_mu),
                Country("Mayotte", "YT", "MYT", R.drawable.ic_flag_yt),
                Country("Mexico", "MX", "MEX", R.drawable.ic_flag_mx),
                Country("Micronesia", "FM", "FSM", R.drawable.ic_flag_fm),
                Country("Moldova", "MD", "MDA", R.drawable.ic_flag_md),
                Country("Monaco", "MC", "MCO", R.drawable.ic_flag_mc),
                Country("Mongolia", "MN", "MNG", R.drawable.ic_flag_mn),
                Country("Montenegro", "ME", "MNE", R.drawable.ic_flag_me),
                Country("Montserrat", "MS", "MSR", R.drawable.ic_flag_ms),
                Country("Morocco", "MA", "MAR", R.drawable.ic_flag_ma),
                Country("Mozambique", "MZ", "MOZ", R.drawable.ic_flag_mz),
                Country("Myanmar", "MM", "MMR", R.drawable.ic_flag_mm),
                Country("Namibia", "NA", "NAM", R.drawable.ic_flag_na),
                Country("Nauru", "NR", "NRU", R.drawable.ic_flag_nr),
                Country("Nepal", "NP", "NPL", R.drawable.ic_flag_np),
                Country("Netherlands", "NL", "NLD", R.drawable.ic_flag_nl),
                Country("New Caledonia", "NC", "NCL", R.drawable.ic_flag_nc),
                Country("New Zealand", "NZ", "NZL", R.drawable.ic_flag_nz),
                Country("Nicaragua", "NI", "NIC", R.drawable.ic_flag_ni),
                Country("Niger", "NE", "NER", R.drawable.ic_flag_ne),
                Country("Nigeria", "NG", "NGA", R.drawable.ic_flag_ng),
                Country("Niue", "NU", "NIU", R.drawable.ic_flag_nu),
                Country("Norfolk Island", "NF", "NFK", R.drawable.ic_flag_nf),
                Country("North Korea", "KP", "PRK", R.drawable.ic_flag_kp),
                Country("Northern Mariana Islands", "MP", "MNP", R.drawable.ic_flag_mp),
                Country("Norway", "NO", "NOR", R.drawable.ic_flag_no),
                Country("Oman", "OM", "OMN", R.drawable.ic_flag_om),
                Country("Pakistan", "PK", "PAK", R.drawable.ic_flag_pk),
                Country("Palau", "PW", "PLW", R.drawable.ic_flag_pw),
                Country("Palestinian Territory, Occupied", "PS", "PSE", R.drawable.ic_flag_ps),
                Country("Panama", "PA", "PAN", R.drawable.ic_flag_pa),
                Country("Papua New Guinea", "PG", "PNG", R.drawable.ic_flag_pg),
                Country("Paraguay", "PY", "PRY", R.drawable.ic_flag_py),
                Country("Peru", "PE", "PER", R.drawable.ic_flag_pe),
                Country("Philippines", "PH", "PHL", R.drawable.ic_flag_ph),
                Country("Pitcairn Islands", "PN", "PCN", R.drawable.ic_flag_pn),
                Country("Poland", "PL", "POL", R.drawable.ic_flag_pl),
                Country("Portugal", "PT", "PRT", R.drawable.ic_flag_pt),
                Country("Puerto Rico", "PR", "PRI", R.drawable.ic_flag_pr),
                Country("Qatar", "QA", "QAT", R.drawable.ic_flag_qa),
                Country("Réunion", "RE", "REU", R.drawable.ic_flag_re),
                Country("Romania", "RO", "ROU", R.drawable.ic_flag_ro),
                Country("Russia", "RU", "RUS", R.drawable.ic_flag_ru),
                Country("Rwanda", "RW", "RWA", R.drawable.ic_flag_rw),
                Country("Saint Barthélemy", "BL", "BLM", R.drawable.ic_flag_bl),
                Country("Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", R.drawable.ic_flag_sh),
                Country("Saint Kitts and Nevis", "KN", "KNA", R.drawable.ic_flag_kn),
                Country("Saint Lucia", "LC", "LCA", R.drawable.ic_flag_lc),
                Country("Saint Martin", "MF", "MAF", R.drawable.ic_flag_mf),
                Country("Saint Pierre and Miquelon", "PM", "SPM", R.drawable.ic_flag_pm),
                Country("Saint Vincent and the Grenadines", "VC", "VCT", R.drawable.ic_flag_vc),
                Country("Samoa", "WS", "WSM", R.drawable.ic_flag_ws),
                Country("San Marino", "SM", "SMR", R.drawable.ic_flag_sm),
                Country("Sao Tome and Principe", "ST", "STP", R.drawable.ic_flag_st),
                Country("Saudi Arabia", "SA", "SAU", R.drawable.ic_flag_sa),
                Country("Senegal", "SN", "SEN", R.drawable.ic_flag_sn),
                Country("Serbia", "RS", "SRB", R.drawable.ic_flag_rs),
                Country("Seychelles", "SC", "SYC", R.drawable.ic_flag_sc),
                Country("Sierra Leone", "SL", "SLE", R.drawable.ic_flag_sl),
                Country("Singapore", "SG", "SGP", R.drawable.ic_flag_sg),
                Country("Sint Maarten", "SX", "SXM", R.drawable.ic_flag_sx),
                Country("Slovakia", "SK", "SVK", R.drawable.ic_flag_sk),
                Country("Slovenia", "SI", "SVN", R.drawable.ic_flag_si),
                Country("Solomon Islands", "SB", "SLB", R.drawable.ic_flag_sb),
                Country("Somalia", "SO", "SOM", R.drawable.ic_flag_so),
                Country("South Africa", "ZA", "ZAF", R.drawable.ic_flag_za),
                Country("South Georgia and the South Sandwich Islands", "GS", "SGS", R.drawable.ic_flag_gs),
                Country("South Korea", "KR", "KOR", R.drawable.ic_flag_kr),
                Country("South Sudan", "SS", "SSD", R.drawable.ic_flag_ss),
                Country("Spain", "ES", "ESP", R.drawable.ic_flag_es),
                Country("Sri Lanka", "LK", "LKA", R.drawable.ic_flag_lk),
                Country("Sudan", "SD", "SDN", R.drawable.ic_flag_sd),
                Country("Suriname", "SR", "SUR", R.drawable.ic_flag_sr),
                Country("Svalbard and Jan Mayen", "SJ", "SJM", R.drawable.ic_flag_sj),
                Country("Swaziland", "SZ", "SWZ", R.drawable.ic_flag_sz),
                Country("Sweden", "SE", "SWE", R.drawable.ic_flag_se),
                Country("Switzerland", "CH", "CHE", R.drawable.ic_flag_ch),
                Country("Syria", "SY", "SYR", R.drawable.ic_flag_sy),
                Country("Taiwan", "TW", "TWN", R.drawable.ic_flag_tw),
                Country("Tajikistan", "TJ", "TJK", R.drawable.ic_flag_tj),
                Country("Tanzania", "TZ", "TZA", R.drawable.ic_flag_tz),
                Country("Thailand", "TH", "THA", R.drawable.ic_flag_th),
                Country("The Gambia", "GM", "GMB", R.drawable.ic_flag_gm),
                Country("Timor-Leste", "TL", "TLS", R.drawable.ic_flag_tl),
                Country("Togo", "TG", "TGO", R.drawable.ic_flag_tg),
                Country("Tokelau", "TK", "TKL", R.drawable.ic_flag_tk),
                Country("Tonga", "TO", "TON", R.drawable.ic_flag_to),
                Country("Trinidad and Tobago", "TT", "TTO", R.drawable.ic_flag_tt),
                Country("Tunisia", "TN", "TUN", R.drawable.ic_flag_tn),
                Country("Turkey", "TR", "TUR", R.drawable.ic_flag_tr),
                Country("Turkmenistan", "TM", "TKM", R.drawable.ic_flag_tm),
                Country("Turks and Caicos Islands", "TC", "TCA", R.drawable.ic_flag_tc),
                Country("Tuvalu", "TV", "TUV", R.drawable.ic_flag_tv),
                Country("Uganda", "UG", "UGA", R.drawable.ic_flag_ug),
                Country("Ukraine", "UA", "UKR", R.drawable.ic_flag_ua),
                Country("United Arab Emirates", "AE", "ARE", R.drawable.ic_flag_ae),
                Country("United Kingdom", "GB", "GBR", R.drawable.ic_flag_gb),
                Country("United States", "US", "USA", R.drawable.ic_flag_us),
                Country("United States Minor Outlying Islands", "UM", "UMI", R.drawable.ic_flag_um),
                Country("United States Virgin Islands", "VI", "VIR", R.drawable.ic_flag_vi),
                Country("Uruguay", "UY", "URY", R.drawable.ic_flag_uy),
                Country("Uzbekistan", "UZ", "UZB", R.drawable.ic_flag_uz),
                Country("Vanuatu", "VU", "VUT", R.drawable.ic_flag_vu),
                Country("Vatican City State", "VA", "VAT", R.drawable.ic_flag_va),
                Country("Venezuela", "VE", "VEN", R.drawable.ic_flag_ve),
                Country("Viet Nam", "VN", "VNM", R.drawable.ic_flag_vn),
                Country("Virgin Islands", "VG", "VGB", R.drawable.ic_flag_vg),
                Country("Wallis and Futuna", "WF", "WLF", R.drawable.ic_flag_wf),
                Country("Western Sahara", "EH", "ESH", R.drawable.ic_flag_eh),
                Country("Yemen", "YE", "YEM", R.drawable.ic_flag_ye),
                Country("Zambia", "ZM", "ZMB", R.drawable.ic_flag_zm),
                Country("Zimbabwe", "ZW", "ZWE", R.drawable.ic_flag_zw)

        )//end list
    }//end

}//end class