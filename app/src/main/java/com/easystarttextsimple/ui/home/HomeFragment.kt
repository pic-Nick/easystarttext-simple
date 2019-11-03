package com.easystarttextsimple.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.easystarttextsimple.R

class HomeFragment : Fragment(), View.OnClickListener {

    interface OnHomeFragmentEventListener {
        fun startButtonEvent()
        fun stopButtonEvent()
        fun statusButtonEvent()
    }

//    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeFragmentEventListener: OnHomeFragmentEventListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHomeFragmentEventListener)
            homeFragmentEventListener = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.findViewById<Button>(R.id.startButton).setOnClickListener(this)
        root.findViewById<Button>(R.id.stopButton).setOnClickListener(this)
        root.findViewById<Button>(R.id.statusButton).setOnClickListener(this)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.startButton -> homeFragmentEventListener.startButtonEvent()
            R.id.stopButton -> homeFragmentEventListener.stopButtonEvent()
            R.id.statusButton -> homeFragmentEventListener.statusButtonEvent()
        }
    }
}