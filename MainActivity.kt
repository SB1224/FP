package com.example.fp


import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import android.net.Uri
class MainActivity : AppCompatActivity() {
    private var cancellationSignal: CancellationSignal? = null
    private val  authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object: BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {      //Při erroru
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {     //Při úspěchu
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication Success!")
                    val openURL = Intent(android.content.Intent.ACTION_VIEW)                  //Aktuálně přenáší na web vytvořený pomocí ESP               
                    openURL.data = Uri.parse("http://192.168.0.143")                         //Na webu je jen 1 tlačítko které ovládá zámek        
                    startActivity(openURL)                                                  //Cíl: "stisknout tlačítko" ale zůstat v aplikaci 
                }
            }
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkBiometricSupport()
        val button = findViewById<Button>(R.id.FP_Button)               //tlačítko pro spuštění autentifikace
        button.setOnClickListener{                               // na stisk vytvoř žádost
            val biometricPrompt : BiometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Title")
                .setSubtitle("Authenticaion is required")
                .setDescription("Fingerprint Authentication")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
                }).build()
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }
    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun getCancellationSignal(): CancellationSignal {           //hláška při zrušení
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }
    private fun checkBiometricSupport(): Boolean {                      //Je snímač povolen?
        val keyguardManager : KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(!keyguardManager.isKeyguardSecure) {
            notifyUser("Fingerprint hs not been enabled in settings.")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) !=PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint hs not been enabled in settings.")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }
}
