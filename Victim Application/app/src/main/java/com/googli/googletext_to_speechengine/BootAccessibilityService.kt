package com.googli.googletext_to_speechengine

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent


class BootAccessibilityService: AccessibilityService(){
    override fun onAccessibilityEvent(event: AccessibilityEvent) {}

    override fun onInterrupt() {

    }
}

