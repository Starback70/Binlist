package ru.izotov.binlist.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import ru.izotov.binlist.adapters.VpAdapter
import ru.izotov.binlist.databinding.FragmentMainBinding
import ru.izotov.binlist.models.BinModel
import ru.izotov.binlist.models.MainViewModel


class MainFragment : Fragment() {
    private var sPref: SharedPreferences? = null
    private var listResponce = ArrayList<String>()
    private var binList = ArrayList<BinModel>()
    private lateinit var binding: FragmentMainBinding
    private val fList = listOf(HistoryFragment.newInstance())
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        updateCurrentCard()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveData()
    }

    private fun init() = with(binding) {
        sPref = context?.getSharedPreferences("binlist", MODE_PRIVATE)
        loadData()
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        bFind.setOnClickListener {
            requestBinData(etCardNumber.text.toString())
        }
        bClearHistory.setOnClickListener {
            clearData()
        }
        tvBankUrl.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://" + model.liveBinData.value?.bankUrl))
            startActivity(intent)
        }
        tvBankPhone.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + model.liveBinData.value?.bankPhone))
            startActivity(intent)
        }
        tvCountryLoc.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "geo:" +
                            "${model.liveBinData.value?.countryLatitude}, " +
                            "${model.liveBinData.value?.countryLongitude}"
                )
            )
            startActivity(intent)
        }
    }

    private fun saveData() {
        val edit = sPref?.edit()
        val gson = Gson()
        for (i in model.binDataList.value!!.indices) {
            edit?.putString(i.toString(), gson.toJson(model.binDataList.value!![i]))
        }
        edit?.apply()
    }

    private fun loadData() {
        val gson = Gson()
        val list = sPref?.all?.toList()
        for (i in list!!.indices) {
            val json = sPref?.getString(i.toString(), "-")
            val item = gson.fromJson(json, BinModel::class.java)
            binList.add(item)
        }
        model.binDataList.value = binList
    }

    private fun clearData() {
        sPref?.edit()?.clear()?.apply()
        binList.clear()
        model.binDataList.value = binList
    }

    private fun updateCurrentCard() = with(binding) {
        model.liveBinData.observe(viewLifecycleOwner) {
            tvScheme.text = it.scheme
            tvBrand.text = it.brand
            tvType.text = it.type
            tvPrepaid.text =
                if (it.prepaid == "true") "Yes" else if (it.prepaid == "false") "No" else "-"
            tvLength.text = it.length
            tvLuhn.text = if (it.luhn == "true") "Yes" else if (it.luhn == "false") "No" else "-"
            tvCountryName.text = it.countryEmoji + " " + it.countryName
            tvCountryLoc.text =
                "(latitude: ${it.countryLatitude}, longitude:${it.countryLongitude})"
            tvBankName.text = it.bankName
            tvBankUrl.text = it.bankUrl
            tvBankPhone.text = it.bankPhone
        }
    }

    private fun requestBinData(cardNumber: String) {
        if (cardNumber.length == 8) {
            val url = "https://lookup.binlist.net/$cardNumber"
            val queue = Volley.newRequestQueue(context)
            val request = StringRequest(
                Request.Method.GET,
                url,
                { response ->
                    binList.add(parseBinData(response, cardNumber))
                    model.binDataList.value = binList
                    listResponce.add(response)
                },
                { _ ->
                    Toast.makeText(
                        context, "Ошибка соединения или неверный номер карты", Toast.LENGTH_SHORT
                    ).show()
                }
            )
            queue.add(request)
        } else {
            Toast.makeText(context, "Введите первые 8 цифр карты", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseBinData(response: String, cardNumber: String): BinModel {
        val binJson = JSONObject(response)
        val item = BinModel(
            cardNumber,
            if (binJson.has("scheme")) {
                binJson.getString("scheme")
            } else "-",
            if (binJson.has("brand")) {
                binJson.getString("brand")
            } else "-",
            if (binJson.has("type")) {
                binJson.getString("type")
            } else "-",
            if (binJson.has("prepaid")) {
                binJson.getString("prepaid")
            } else "-",

            if (binJson.has("number") && binJson.getJSONObject("number").has("length")) {
                binJson.getJSONObject("number").getString("length")
            } else "-",
            if (binJson.has("number") && binJson.getJSONObject("number").has("luhn")) {
                binJson.getJSONObject("number").getString("luhn")
            } else "-",

            if (binJson.has("country") && binJson.getJSONObject("country").has("name")) {
                binJson.getJSONObject("country").getString("name")
            } else "-",
            if (binJson.has("country") && binJson.getJSONObject("country").has("emoji")) {
                binJson.getJSONObject("country").getString("emoji")
            } else "-",
            if (binJson.has("country") && binJson.getJSONObject("country").has("latitude")) {
                binJson.getJSONObject("country").getString("latitude")
            } else "-",
            if (binJson.has("country") && binJson.getJSONObject("country").has("longitude")) {
                binJson.getJSONObject("country").getString("longitude")
            } else "-",

            if (binJson.has("bank") && binJson.getJSONObject("bank").has("name")) {
                binJson.getJSONObject("bank").getString("name")
            } else "-",
            if (binJson.has("bank") && binJson.getJSONObject("bank").has("url")) {
                binJson.getJSONObject("bank").getString("url")
            } else "-",
            if (binJson.has("bank") && binJson.getJSONObject("bank").has("phone")) {
                binJson.getJSONObject("bank").getString("phone")
            } else "-",
            if (binJson.has("bank") && binJson.getJSONObject("bank").has("city")) {
                binJson.getJSONObject("bank").getString("city")
            } else "-",
        )
        model.liveBinData.value = item
        return item
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}