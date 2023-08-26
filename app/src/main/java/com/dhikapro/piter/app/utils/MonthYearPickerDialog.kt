package com.dhikapro.piter.app.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dhikapro.piter.databinding.DialogMonthYearPickerBinding
import java.util.*

class MonthYearPickerDialog(val date: Date = Date()) : DialogFragment() {

    companion object {
        private const val MAX_YEAR = 2099
    }

    private lateinit var binding: DialogMonthYearPickerBinding

    private var listener: DatePickerDialog.OnDateSetListener? = null

    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMonthYearPickerBinding.inflate(requireActivity().layoutInflater)
        val cal: Calendar = Calendar.getInstance().apply { time = date }

        binding.pickerMonth.run {
            minValue = 0
            maxValue = 11
            value = cal.get(Calendar.MONTH)
            displayedValues = arrayOf("Jan","Feb","Mar","Apr","May","June","July",
                "Aug","Sep","Oct","Nov","Dec")
        }

        binding.pickerYear.run {
            val year = cal.get(Calendar.YEAR)
            minValue = year
            maxValue = MAX_YEAR
            value = year
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Pilih Bulan")
            .setView(binding.root)
            .setPositiveButton("Ok") { _, _ -> listener?.onDateSet(null, binding.pickerYear.value, binding.pickerMonth.value, 1) }
            .setNegativeButton("Cancel") { _, _ -> dialog?.cancel() }
            .create()
    }
}

fun convertToHumanMonth(year:Int, month:Int):String{
    var monthHuman = ""
    val monthList = arrayOf(
        "Januari","Februari","Maret","April",
        "Mei","Juni","July","Agustus","September",
        "Oktober","November", "Desember"
    )
    when(month) {
        0->{
            monthHuman = monthList[0]
        }
        1->{
            monthHuman = monthList[1]
        }
        2->{
            monthHuman = monthList[2]
        }
        3->{
            monthHuman = monthList[3]
        }
        4->{
            monthHuman = monthList[4]
        }
        5->{
            monthHuman = monthList[5]
        }
        6->{
            monthHuman = monthList[6]
        }
        7->{
            monthHuman = monthList[7]
        }
        8->{
            monthHuman = monthList[8]
        }
        9->{
            monthHuman = monthList[9]
        }
        10->{
            monthHuman = monthList[10]
        }
        11->{
            monthHuman = monthList[11]
        }
    }
    return "$monthHuman $year"
}

fun convertToMonthFormat(year: Int, month: Int):String{
    var monthFormat = ""
    val monthRes = month+1
    if(monthRes.toString().length == 1){
        monthFormat = "0${monthRes.toString()}"
    }else{
        monthFormat = monthRes.toString()
    }
    return "$year-$monthFormat"
}