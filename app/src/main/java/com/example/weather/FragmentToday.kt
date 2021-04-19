package com.example.weather

import android.os.Bundle
import androidx.fragment.app.Fragment

class FragmentToday : Fragment(R.layout.fragment_weather_today) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
    }
/*
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_today, container, false)
    }*/

    companion object {
        /*
        fun newInstance(param1: String, param2: String) =
                FragmentToday().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }*/
    }
}