package com.github.helltar.anpaside

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.github.helltar.anpaside.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {
    private lateinit var binding: ActivitySettingsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding initialization
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        
        with(binding) {
            val view: View = root
            setContentView(view)
            
            editEditorFontSize.setText(
                MainActivity.codeEditor.editorConfig.fontSize.toString()
            )
            toggleSyntaxHighlighting.isChecked = MainActivity.codeEditor.editorConfig.highlighterEnabled
            editGlobalLibsDirectoryPath.setText(MainActivity.ideConfig.globalDirPath)
            
            saveSettingsButton.setOnClickListener {
                onSaveSettingsButtonClicked()
            }
        }
    }
    
    private fun onSaveSettingsButtonClicked() {
        with(binding) {
            MainActivity.codeEditor.setFontSize(
                editEditorFontSize.text.toString().toInt()
            )
            MainActivity.codeEditor.setHighlighterEnabled(toggleSyntaxHighlighting.isChecked)
    
            var path = editGlobalLibsDirectoryPath.text.toString()
    
            if (!path.endsWith("/")) {
                path += "/"
            }
    
            MainActivity.ideConfig.globalDirPath = path
    
            finish()
        }
    }
}