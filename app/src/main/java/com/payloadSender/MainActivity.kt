package com.payloadSender

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var etIpAddress: EditText
    private lateinit var etPort: EditText
    private lateinit var etPayloadPath: EditText
    private lateinit var btnBrowse: Button
    private lateinit var btnInject: Button
    private lateinit var btnSave: Button
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvPresets: RecyclerView
    private lateinit var tvPresetsLabel: TextView
    private lateinit var presetAdapter: PresetAdapter

    private var selectedFileUri: Uri? = null
    private val PICK_FILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etIpAddress   = findViewById(R.id.etIpAddress)
        etPort        = findViewById(R.id.etPort)
        etPayloadPath = findViewById(R.id.etPayloadPath)
        btnBrowse     = findViewById(R.id.btnBrowse)
        btnInject     = findViewById(R.id.btnInject)
        btnSave       = findViewById(R.id.btnSave)
        tvStatus      = findViewById(R.id.tvStatus)
        progressBar   = findViewById(R.id.progressBar)
        rvPresets     = findViewById(R.id.rvPresets)
        tvPresetsLabel = findViewById(R.id.tvPresetsLabel)

        btnBrowse.setOnClickListener { openFilePicker() }
        btnInject.setOnClickListener { injectPayload() }
        btnSave.setOnClickListener { savePreset() }

        setupPresetsList()
        loadPresets()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Seleccionar payload"), PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    // Persist the URI permission so it works after app restart
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                } catch (e: Exception) {
                    // Some files don't support persistent permissions, that's OK
                }
                
                selectedFileUri = uri
                val fileName = getFileName(uri)
                etPayloadPath.setText(fileName)
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "archivo_seleccionado"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && idx >= 0) {
                name = cursor.getString(idx)
            }
        }
        return name
    }

    private fun injectPayload() {
        val ip   = etIpAddress.text.toString().trim()
        val port = etPort.text.toString().trim().toIntOrNull()
        val uri  = selectedFileUri

        if (ip.isEmpty()) {
            showStatus("⚠ Ingresa una dirección IP", StatusType.ERROR); return
        }
        if (port == null || port !in 1..65535) {
            showStatus("⚠ Puerto inválido (1–65535)", StatusType.ERROR); return
        }
        if (uri == null) {
            showStatus("⚠ Selecciona un archivo payload", StatusType.ERROR); return
        }

        val bytes = try {
            contentResolver.openInputStream(uri)?.readBytes()
        } catch (e: Exception) {
            showStatus("✗ Error: ${e.localizedMessage}", StatusType.ERROR)
            return
        }

        if (bytes == null || bytes.isEmpty()) {
            showStatus("✗ El archivo está vacío", StatusType.ERROR); return
        }

        sendPayload(ip, port, bytes)
    }

    private fun sendPayload(ip: String, port: Int, data: ByteArray) {
        setUiEnabled(false)
        showStatus("Conectando a $ip:$port…", StatusType.PROGRESS)
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val result = PayloadSender.send(ip, port, data)
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                setUiEnabled(true)
                if (result.success) {
                    showStatus("✓ Payload enviado (${data.size} bytes)", StatusType.SUCCESS)
                } else {
                    showStatus("✗ Error: ${result.message}", StatusType.ERROR)
                }
            }
        }
    }

    private fun setUiEnabled(enabled: Boolean) {
        btnInject.isEnabled = enabled
        btnBrowse.isEnabled = enabled
        btnSave.isEnabled = enabled
        etIpAddress.isEnabled = enabled
        etPort.isEnabled = enabled
    }

    private fun setupPresetsList() {
        presetAdapter = PresetAdapter(
            mutableListOf(),
            onPresetClick = { preset ->
                val uri = try {
                    Uri.parse(preset.payloadUri)
                } catch (e: Exception) {
                    showStatus("✗ Error en preset: URI inválida", StatusType.ERROR)
                    return@PresetAdapter
                }

                try {
                    // Request persistent permission to keep access after app restart
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                } catch (e: Exception) {
                    // Permission might already exist or file might not support it
                }

                etIpAddress.setText(preset.ipAddress)
                etPort.setText(preset.port.toString())
                etPayloadPath.setText(preset.payloadFileName)
                selectedFileUri = uri
                showStatus("✓ Preset cargado: ${preset.name}", StatusType.SUCCESS)
            },
            onDeleteClick = { preset ->
                // Release persistent permission when deleting preset
                try {
                    val uri = Uri.parse(preset.payloadUri)
                    contentResolver.releasePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Permission might not exist, that's OK
                }
                
                PresetManager.deletePreset(this, preset.id)
                loadPresets()
                showStatus("✗ Preset eliminado", StatusType.ERROR)
            }
        )
        rvPresets.layoutManager = LinearLayoutManager(this)
        rvPresets.adapter = presetAdapter
    }

    private fun loadPresets() {
        val presets = PresetManager.getPresets(this)
        presetAdapter.updateList(presets)
        tvPresetsLabel.visibility = if (presets.isEmpty()) View.GONE else View.VISIBLE
        rvPresets.visibility = if (presets.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun savePreset() {
        val ip = etIpAddress.text.toString().trim()
        val port = etPort.text.toString().trim().toIntOrNull()
        val fileName = etPayloadPath.text.toString().trim()
        val uri = selectedFileUri

        if (ip.isEmpty()) {
            showStatus("⚠ Ingresa una dirección IP", StatusType.ERROR); return
        }
        if (port == null || port !in 1..65535) {
            showStatus("⚠ Puerto inválido (1–65535)", StatusType.ERROR); return
        }
        if (fileName.isEmpty()) {
            showStatus("⚠ Selecciona un archivo payload", StatusType.ERROR); return
        }

        val input = EditText(this).apply {
            hint = "Nombre del preset"
        }

        AlertDialog.Builder(this)
            .setTitle("Guardar preset")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val presetName = input.text.toString().trim()
                if (presetName.isNotEmpty() && uri != null) {
                    val preset = Preset(
                        name = presetName,
                        ipAddress = ip,
                        port = port,
                        payloadFileName = fileName,
                        payloadUri = uri.toString()
                    )
                    PresetManager.savePreset(this, preset)
                    loadPresets()
                    showStatus("✓ Preset guardado: $presetName", StatusType.SUCCESS)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showStatus(msg: String, type: StatusType) {
        tvStatus.text = msg
        tvStatus.setTextColor(
            ContextCompat.getColor(this, when (type) {
                StatusType.SUCCESS  -> R.color.statusSuccess
                StatusType.ERROR    -> R.color.statusError
                StatusType.PROGRESS -> R.color.statusProgress
            })
        )
    }

    enum class StatusType { SUCCESS, ERROR, PROGRESS }
}
