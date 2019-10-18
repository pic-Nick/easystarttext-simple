package com.easystarttextsimple

data class TimerData(val timerOn: Boolean, val timerDays: Set<String>?, val timerTime: String?)

data class Timers(val timer1: TimerData, val timer2: TimerData, val timer3: TimerData)