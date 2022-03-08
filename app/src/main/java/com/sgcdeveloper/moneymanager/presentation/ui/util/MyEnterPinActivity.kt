package com.sgcdeveloper.moneymanager.presentation.ui.util

import android.Manifest
import android.animation.ObjectAnimator
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import com.amirarcane.lockscreen.activity.EnterPinActivity.*
import com.amirarcane.lockscreen.andrognito.pinlockview.IndicatorDots
import com.amirarcane.lockscreen.andrognito.pinlockview.PinLockListener
import com.amirarcane.lockscreen.andrognito.pinlockview.PinLockView
import com.amirarcane.lockscreen.fingerprint.FingerPrintListener
import com.amirarcane.lockscreen.util.Animate
import com.amirarcane.lockscreen.util.Utils
import com.sgcdeveloper.moneymanager.R
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class MyEnterPinActivity : AppCompatActivity() {

    val TAG = "EnterPinActivity"

    val PIN_LENGTH = 4
    val FINGER_PRINT_KEY = "FingerPrintKey"

    val PREFERENCES = "com.amirarcane.lockscreen"
    val KEY_PIN = "pin"

    var mPinLockView: PinLockView? = null
    var mIndicatorDots: IndicatorDots? = null
    var mTextTitle: TextView? = null
    var mTextAttempts: TextView? = null
    var mTextFingerText: TextView? = null
    var mImageViewFingerView: AppCompatImageView? = null

    var mCipher: Cipher? = null
    var mKeyStore: KeyStore? = null
    var mKeyGenerator: KeyGenerator? = null
    var mCryptoObject: FingerprintManager.CryptoObject? = null
    var mFingerprintManager: FingerprintManager? = null
    var mKeyguardManager: KeyguardManager? = null
    var mSetPin = false
    var mFirstPin = ""
    private val cancellationSignal: CancellationSignal? = null

    //    private int mTryCount = 0;

    //    private int mTryCount = 0;
    var showFingerprint: AnimatedVectorDrawable? = null
    var fingerprintToTick: AnimatedVectorDrawable? = null
    var fingerprintToCross: AnimatedVectorDrawable? = null

    companion object {
        val RESULT_BACK_PRESSED: Int = RESULT_FIRST_USER
        //    public static final int RESULT_TOO_MANY_TRIES = RESULT_FIRST_USER + 1;
        val EXTRA_SET_PIN = "set_pin"
        val EXTRA_FONT_TEXT = "textFont"
        val EXTRA_FONT_NUM = "numFont"

        fun getIntent(context: Context?, setPin: Boolean): Intent? {
            val intent = Intent(context, MyEnterPinActivity::class.java)
            intent.putExtra(EXTRA_SET_PIN, setPin)
            return intent
        }

        fun getIntent(context: Context?, fontText: String?, fontNum: String?): Intent {
            val intent = Intent(context, MyEnterPinActivity::class.java)
            intent.putExtra(EXTRA_FONT_TEXT, fontText)
            intent.putExtra(EXTRA_FONT_NUM, fontNum)
            return intent
        }

        fun getIntent(context: Context?, setPin: Boolean, fontText: String?, fontNum: String?): Intent? {
            val intent = getIntent(context, fontText, fontNum)
            intent.putExtra(EXTRA_SET_PIN, setPin)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_enterpin)
        mTextAttempts = findViewById(R.id.attempts) as TextView?
        mTextTitle = findViewById(R.id.title) as TextView?
        mIndicatorDots = findViewById(R.id.indicator_dots) as IndicatorDots?
        mImageViewFingerView = findViewById(R.id.fingerView)
        mTextFingerText = findViewById(R.id.fingerText) as TextView?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFingerprint = getDrawable(R.drawable.show_fingerprint) as AnimatedVectorDrawable?
            fingerprintToTick = getDrawable(R.drawable.fingerprint_to_tick) as AnimatedVectorDrawable?
            fingerprintToCross = getDrawable(R.drawable.fingerprint_to_cross) as AnimatedVectorDrawable?
        }
        mSetPin = getIntent().getBooleanExtra(EXTRA_SET_PIN, false)
        if (mSetPin) {
            changeLayoutForSetPin()
        } else {
            val pin: String = getPinFromSharedPreferences()!!
            if (pin == "") {
                changeLayoutForSetPin()
                mSetPin = true
            } else {
                checkForFingerPrint()
            }
        }
        val pinLockListener: PinLockListener = object : PinLockListener {
            override fun onComplete(pin: String) {
                if (mSetPin) {
                    setPin(pin)
                } else {
                    checkPin(pin)
                }
            }

            override fun onEmpty() {
                Log.d(TAG, "Pin empty")
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {
                Log.d(TAG, "Pin changed, new length $pinLength with intermediate pin $intermediatePin")
            }
        }
        mPinLockView = findViewById(R.id.pinlockView) as PinLockView?
        mIndicatorDots = findViewById(R.id.indicator_dots) as IndicatorDots?
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)
        mPinLockView!!.setPinLockListener(pinLockListener)
        mPinLockView!!.pinLength = PIN_LENGTH
        mIndicatorDots!!.indicatorType = IndicatorDots.IndicatorType.FILL_WITH_ANIMATION
        checkForFont()
    }

    open fun checkForFont() {
        val intent: Intent = getIntent()
        if (intent.hasExtra(EXTRA_FONT_TEXT)) {
            val font = intent.getStringExtra(EXTRA_FONT_TEXT)
            setTextFont(font!!)
        }
        if (intent.hasExtra(EXTRA_FONT_NUM)) {
            val font = intent.getStringExtra(EXTRA_FONT_NUM)
            setNumFont(font!!)
        }
    }

    open fun setTextFont(font: String) {
        try {
            val typeface = Typeface.createFromAsset(getAssets(), font)
            mTextTitle!!.setTypeface(typeface)
            mTextAttempts!!.setTypeface(typeface)
            mTextFingerText!!.setTypeface(typeface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun setNumFont(font: String) {
        try {
            val typeface = Typeface.createFromAsset(getAssets(), font)
            mPinLockView!!.setTypeFace(typeface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    @Throws(FingerprintException::class)
    open fun generateKey() {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            mKeyStore = KeyStore.getInstance("AndroidKeyStore")

            //Generate the key//
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            //Initialize an empty KeyStore//
            mKeyStore!!.load(null)

            //Initialize the KeyGenerator//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mKeyGenerator!!.init(
                    KeyGenParameterSpec.Builder(
                        FINGER_PRINT_KEY,
                        KeyProperties.PURPOSE_ENCRYPT or
                                KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC) //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7
                        )
                        .build()
                )
            }

            //Generate the key//
            mKeyGenerator!!.generateKey()
        } catch (exc: KeyStoreException) {
            throw FingerprintException(exc)
        } catch (exc: NoSuchAlgorithmException) {
            throw FingerprintException(exc)
        } catch (exc: NoSuchProviderException) {
            throw FingerprintException(exc)
        } catch (exc: InvalidAlgorithmParameterException) {
            throw FingerprintException(exc)
        } catch (exc: CertificateException) {
            throw FingerprintException(exc)
        } catch (exc: IOException) {
            throw FingerprintException(exc)
        }
    }

    //Create a new method that we’ll use to initialize our mCipher//
    fun initCipher(): Boolean {
        try {
            //Obtain a mCipher instance and configure it with the properties required for fingerprint authentication//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7
                )
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Failed to get Cipher")
            return false
        } catch (e: NoSuchPaddingException) {
            Log.e(TAG, "Failed to get Cipher")
            return false
        }
        return try {
            mKeyStore!!.load(null)
            val key = mKeyStore!!.getKey(
                FINGER_PRINT_KEY,
                null
            ) as SecretKey
            mCipher!!.init(Cipher.ENCRYPT_MODE, key)
            //Return true if the mCipher has been initialized successfully//
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to init Cipher")
            false
        }
    }

    open fun writePinToSharedPreferences(pin: String) {
        val prefs: SharedPreferences = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PIN, Utils.sha256(pin)).apply()
    }

    open fun getPinFromSharedPreferences(): String? {
        val prefs: SharedPreferences = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN, "")
    }

    open fun setPin(pin: String) {
        if (mFirstPin == "") {
            mFirstPin = pin
            mTextTitle!!.setText(getString(R.string.pinlock_secondPin))
            mPinLockView!!.resetPinLockView()
        } else {
            if (pin == mFirstPin) {
                writePinToSharedPreferences(pin)
                setResult(RESULT_OK)
                finish()
            } else {
                shake()
                mTextTitle!!.setText(getString(R.string.pinlock_tryagain))
                mPinLockView!!.resetPinLockView()
                mFirstPin = ""
            }
        }
    }

    open fun checkPin(pin: String) {
        if (Utils.sha256(pin) == getPinFromSharedPreferences()) {
            setResult(RESULT_OK)
            finish()
            helper.cancellationSignal?.cancel()
        } else {
            shake()

//            mTryCount++;
            mTextAttempts!!.setText(getString(R.string.pinlock_wrongpin))
            mPinLockView!!.resetPinLockView()

//            if (mTryCount == 1) {
//                mTextAttempts.setText(getString(R.string.pinlock_firsttry));
//                mPinLockView.resetPinLockView();
//            } else if (mTryCount == 2) {
//                mTextAttempts.setText(getString(R.string.pinlock_secondtry));
//                mPinLockView.resetPinLockView();
//            } else if (mTryCount > 2) {
//                setResult(RESULT_TOO_MANY_TRIES);
//                finish();
//            }
        }
    }

    val helper = MyFingerprintHandler(this)

    open fun shake() {
        val objectAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(mPinLockView, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
                .setDuration(1000)
        objectAnimator.start()
    }

    open fun changeLayoutForSetPin() {
        mImageViewFingerView!!.setVisibility(View.GONE)
        mTextFingerText!!.visibility = View.GONE
        mTextAttempts!!.visibility = View.GONE
        mTextTitle!!.setText(getString(R.string.pinlock_settitle))
    }

    open fun checkForFingerPrint() {
        val fingerPrintListener: FingerPrintListener = object : FingerPrintListener {
            override fun onSuccess() {
                setResult(RESULT_OK)
                Animate.animate(mImageViewFingerView, fingerprintToTick)
                val handler = Handler()
                handler.postDelayed({ finish() }, 750)
            }

            override fun onFailed() {
                Animate.animate(mImageViewFingerView, fingerprintToCross)
                val handler = Handler()
                handler.postDelayed({ Animate.animate(mImageViewFingerView, showFingerprint) }, 750)
            }

            override fun onError(errorString: CharSequence) {
                Toast.makeText(this@MyEnterPinActivity, errorString, Toast.LENGTH_SHORT).show()
            }

            override fun onHelp(helpString: CharSequence) {
                Toast.makeText(this@MyEnterPinActivity, helpString, Toast.LENGTH_SHORT).show()
            }
        }

        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (fingerprintManager.isHardwareDetected) {
                //Get an instance of KeyguardManager and FingerprintManager//
                mKeyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager?
                mFingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager?

                //Check whether the user has granted your app the USE_FINGERPRINT permission//
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                    !== PackageManager.PERMISSION_GRANTED
                ) {
                    // If your app doesn't have this permission, then display the following text//
//                Toast.makeText(EnterPinActivity.this, "Please enable the fingerprint permission", Toast.LENGTH_LONG).show();
                    mImageViewFingerView!!.setVisibility(View.GONE)
                    //                    mTextFingerText.setVisibility(View.GONE);
                    return
                }

                //Check that the user has registered at least one fingerprint//
                if (!mFingerprintManager!!.hasEnrolledFingerprints()) {
                    // If the user hasn’t configured any fingerprints, then display the following message//
//                Toast.makeText(EnterPinActivity.this,
//                        "No fingerprint configured. Please register at least one fingerprint in your device's Settings",
//                        Toast.LENGTH_LONG).show();
                    mImageViewFingerView!!.setVisibility(View.GONE)
                    //                    mTextFingerText.setVisibility(View.GONE);
                    return
                }

                //Check that the lockscreen is secured//
                if (!mKeyguardManager!!.isKeyguardSecure) {
                    // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
//                Toast.makeText(EnterPinActivity.this, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_LONG).show();
                    mImageViewFingerView!!.setVisibility(View.GONE)
                    //                    mTextFingerText.setVisibility(View.GONE);
                    return
                } else {
                    try {
                        generateKey()
                        if (initCipher()) {
                            //If the mCipher is initialized successfully, then create a CryptoObject instance//
                            mCryptoObject = FingerprintManager.CryptoObject(mCipher!!)

                            // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible

                            helper.startAuth(mFingerprintManager!!, mCryptoObject)
                            helper.setFingerPrintListener(fingerPrintListener)

                        }
                    } catch (e: FingerprintException) {
                        Log.wtf(TAG, "Failed to generate key for fingerprint.", e)
                    }
                }
            } else {
                mImageViewFingerView!!.setVisibility(View.GONE)
                //                mTextFingerText.setVisibility(View.GONE);
            }
        } else {
            mImageViewFingerView!!.setVisibility(View.GONE)
            //            mTextFingerText.setVisibility(View.GONE);
        }
    }

    override fun onBackPressed() {}

    class FingerprintException(e: Exception?) : Exception(e)

}
