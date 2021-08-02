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
                MainActivity.editor.editorConfig.fontSize.toString()
            )
            toggleSyntaxHighlighting.isChecked = MainActivity.editor.editorConfig.highlighterEnabled
            editGlobalLibsDirectoryPath.setText(MainActivity.ideConfig.globalDirPath)
            
            saveSettingsButton.setOnClickListener {
                onSaveSettingsButtonClicked()
            }
        }
    }
    
    private fun onSaveSettingsButtonClicked() {
        with(binding) {
            MainActivity.editor.setFontSize(
                editEditorFontSize.text.toString().toInt()
            )
            MainActivity.editor.setHighlighterEnabled(toggleSyntaxHighlighting.isChecked)
    
            var path = editGlobalLibsDirectoryPath.text.toString()
    
            if (!path.endsWith("/")) {
                path += "/"
            }
    
            MainActivity.ideConfig.globalDirPath = path
    
            finish()
        }
    }
}