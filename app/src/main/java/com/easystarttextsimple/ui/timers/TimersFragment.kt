package com.easystarttextsimple.ui.timers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.easystarttextsimple.R

class TimersFragment : Fragment() {

    private lateinit var timersViewModel: TimersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        timersViewModel =
            ViewModelProviders.of(this).get(TimersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_timers, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        timersViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}