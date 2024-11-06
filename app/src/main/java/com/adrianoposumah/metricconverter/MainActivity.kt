package com.adrianoposumah.metricconverter

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerMetric: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var inputValue: EditText
    private lateinit var outputResult: TextView

    // Inisialisasi array adapter untuk spinnerDari dan spinnerKe
    private lateinit var lengthUnits: List<String>
    private lateinit var massUnits: List<String>
    private lateinit var units: List<String>
    private lateinit var spinnerAdapterTo: ArrayAdapter<String>
    private lateinit var spinnerAdapterFrom: ArrayAdapter<String>

    // Tambahkan properti untuk tombol
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btn0: Button
    private lateinit var btnC: Button
    private lateinit var btnDel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi elemen-elemen UI
        spinnerMetric = findViewById(R.id.spinnerMetric)
        spinnerFrom = findViewById(R.id.spinnerDari)
        spinnerTo = findViewById(R.id.spinnerKe)
        inputValue = findViewById(R.id.inputValue)
        outputResult = findViewById(R.id.outputResult)

        // Data unit untuk Panjang dan Massa
        lengthUnits = resources.getStringArray(R.array.length_unit_options).toList()
        massUnits = resources.getStringArray(R.array.mass_unit_options).toList()

        // Inisialisasi adapter untuk spinnerDari dan spinnerKe
        spinnerAdapterFrom = ArrayAdapter(this, android.R.layout.simple_spinner_item, lengthUnits)
        spinnerAdapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = spinnerAdapterFrom

        spinnerAdapterTo = ArrayAdapter(this, android.R.layout.simple_spinner_item, lengthUnits)
        spinnerAdapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTo.adapter = spinnerAdapterTo

        // Inisialisasi setiap tombol
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btn0 = findViewById(R.id.btn0)
        btnC = findViewById(R.id.btnC)
        btnDel = findViewById(R.id.btnDel)

        val metricTypes = listOf("Panjang", "Massa")
        spinnerMetric.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, metricTypes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Menetapkan listener untuk spinnerMetric agar mengubah pilihan unit
        spinnerMetric.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                units = when (spinnerMetric.selectedItem.toString()) {
                    "Panjang" -> lengthUnits
                    "Massa" -> massUnits
                    else -> emptyList()
                }
                updateSpinnerUnits(units)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada tindakan yang diperlukan
            }
        }

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedUnit = spinnerFrom.selectedItem?.toString() // Menambahkan safe call (?.)
                if (selectedUnit != null) {
                    filterToOptions(selectedUnit)
                    konversi() // Panggil konversi untuk update hasil
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Kosongkan spinner jika tidak ada pilihan
            }
        }

        // Tambahkan TextWatcher untuk langsung menghitung saat ada perubahan
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                konversi() // Panggil fungsi konversi saat teks berubah
            }
        })
        // Panggil setupCustomKeyboard untuk mengatur tombol
        setupCustomKeyboard()
    }

    private fun updateSpinnerUnits(units: List<String>) {
        spinnerAdapterFrom.clear()
        spinnerAdapterFrom.addAll(units)
        spinnerAdapterFrom.notifyDataSetChanged()

        spinnerAdapterTo.clear()
        spinnerAdapterTo.addAll(units)
        spinnerAdapterTo.notifyDataSetChanged()
    }

    private fun filterToOptions(selectedFromUnit: String) {
        val filteredUnits = units.filter { it != selectedFromUnit }

        spinnerAdapterTo.clear()
        spinnerAdapterTo.addAll(filteredUnits)
        spinnerAdapterTo.notifyDataSetChanged()
    }

    // Fungsi Konversi
    private fun konversi() {
        val metricType = spinnerMetric.selectedItem.toString()
        val fromUnit = spinnerFrom.selectedItem.toString()
        val toUnit = spinnerTo.selectedItem.toString()
        val input = inputValue.text.toString().toDoubleOrNull()

        if (input == null) {
            outputResult.text = "Input tidak valid"
            return
        }

        val result = when (metricType) {
            "Panjang" -> konversiPanjang(fromUnit, toUnit, input)
            "Massa" -> konversiMassa(fromUnit, toUnit, input)
            else -> 0.0
        }

        outputResult.text = "Hasil: $result"
    }

    private fun konversiPanjang(from: String, to: String, value: Double): Double {
        val meterValue = when (from) {
            "KM" -> value * 1000
            "M" -> value
            "CM" -> value / 100
            "MM" -> value / 1000
            "Inch" -> value * 0.0254
            "Foot" -> value * 0.3048
            "Yard" -> value * 0.9144
            "Mile" -> value * 1609.34
            else -> return 0.0
        }

        return when (to) {
            "KM" -> meterValue / 1000
            "M" -> meterValue
            "CM" -> meterValue * 100
            "MM" -> meterValue * 1000
            "Inch" -> meterValue / 0.0254
            "Foot" -> meterValue / 0.3048
            "Yard" -> meterValue / 0.9144
            "Mile" -> meterValue / 1609.34
            else -> 0.0
        }
    }

    private fun konversiMassa(from: String, to: String, value: Double): Double {
        val gramValue = when (from) {
            "KG" -> value * 1000
            "G" -> value
            "MG" -> value / 1000
            "Pound" -> value * 453.592
            "Ounce" -> value * 28.3495
            else -> return 0.0
        }

        return when (to) {
            "KG" -> gramValue / 1000
            "G" -> gramValue
            "MG" -> gramValue * 1000
            "Pound" -> gramValue / 453.592
            "Ounce" -> gramValue / 28.3495
            else -> 0.0
        }
    }

    private fun setupCustomKeyboard() {
        val buttons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0)
        buttons.forEach { button ->
            button.setOnClickListener {
                inputValue.append(button.text)
            }
        }
        btnC.setOnClickListener {
            inputValue.setText("")
        }
        btnDel.setOnClickListener {
            val length = inputValue.length()
            if (length > 0) {
                inputValue.text.delete(length - 1, length)
            }
        }
    }
}
