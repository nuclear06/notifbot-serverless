package com.yhsif.notifbot.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.yhsif.notifbot.R

public class SettingsActivity :
  AppCompatActivity(),
  PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

  companion object {
    public const val KEY_AUTO_DISMISS = "auto_dismiss"
    public const val DEFAULT_AUTO_DISMISS = false
    public const val KEY_SEND_LABEL = "send_label"
    public const val DEFAULT_SEND_LABEL = true
    public const val KEY_SCREEN_LOCKED_ONLY = "screen_locked_only"
    public const val DEFAULT_SCREEN_LOCKED_ONLY = false
    public const val KEY_TELEGRAM_ENDPOINT = "telegram_endpoint"
    public const val DEFAULT_TELEGRAM_ENDPOINT = "https://api.telegram.org"

    val prefBinder = object : Preference.OnPreferenceChangeListener {
      override fun onPreferenceChange(
        pref: Preference,
        value: Any,
      ): Boolean {
        when (pref.getKey()) {
          KEY_TELEGRAM_ENDPOINT -> {
            val normalized = normalizeEndpoint(value.toString())
            pref.summary = normalized
          }
          KEY_AUTO_DISMISS -> {
            pref.setSummary(
              if (value == true) {
                R.string.pref_desc_auto_dismiss_yes
              } else {
                R.string.pref_desc_auto_dismiss_no
              },
            )
          }
          KEY_SEND_LABEL -> {
            pref.setSummary(
              if (value == true) {
                R.string.pref_desc_send_label_yes
              } else {
                R.string.pref_desc_send_label_no
              },
            )
          }
          KEY_SCREEN_LOCKED_ONLY -> {
            pref.setSummary(
              if (value == true) {
                R.string.pref_desc_screen_locked_only_yes
              } else {
                R.string.pref_desc_screen_locked_only_no
              },
            )
          }
          // For all other preferences, set the summary to the value's
          // simple string representation.
          else -> pref.setSummary(value.toString())
        }
        return true
      }
    }

    fun bindPreferenceSummaryToBoolean(
      preference: Preference,
      defaultValue: Boolean,
    ) {
      preference.setOnPreferenceChangeListener(prefBinder)
      prefBinder.onPreferenceChange(
        preference,
        PreferenceManager
          .getDefaultSharedPreferences(preference.getContext())
          .getBoolean(preference.getKey(), defaultValue),
      )
    }

    fun bindPreferenceSummaryToString(
      preference: Preference,
      defaultValue: String,
    ) {
      preference.setOnPreferenceChangeListener(prefBinder)
      prefBinder.onPreferenceChange(
        preference,
        PreferenceManager
          .getDefaultSharedPreferences(preference.getContext())
          .getString(preference.getKey(), defaultValue)
          ?: defaultValue,
      )
    }

    fun getTelegramEndpoint(ctx: Context): String {
      val raw = PreferenceManager
        .getDefaultSharedPreferences(ctx)
        .getString(KEY_TELEGRAM_ENDPOINT, DEFAULT_TELEGRAM_ENDPOINT)
      return normalizeEndpoint(raw ?: DEFAULT_TELEGRAM_ENDPOINT)
    }

    private fun normalizeEndpoint(raw: String): String {
      val trimmed = raw.trim()
      if (trimmed.isEmpty()) {
        return DEFAULT_TELEGRAM_ENDPOINT
      }
      val withScheme = if (
        trimmed.startsWith("http://") || trimmed.startsWith("https://")
      ) {
        trimmed
      } else {
        "https://$trimmed"
      }
      return withScheme.trimEnd('/')
    }

    public fun adjustPaddingFor35Plus(ctx: Context, view: View) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        // Add additional 46dx padding to top for api 35+,
        // as they no longer auto add the padding for the action bar nor menu bar.
        val dp = 46f
        val px = TypedValue.applyDimension(
          TypedValue.COMPLEX_UNIT_DIP,
          dp,
          ctx.resources.displayMetrics,
        )
        view.setPadding(0, px.toInt(), 0, 0)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupActionBar()
    setContentView(R.layout.settings)

    adjustPaddingFor35Plus(this, findViewById<View>(R.id.fragment_container))

    if (savedInstanceState == null) {
      var frag = getSupportFragmentManager().findFragmentByTag(
        PrefsFragment.FRAGMENT_TAG,
      )
      if (frag == null) {
        frag = PrefsFragment()
      }

      getSupportFragmentManager().commit {
        replace(R.id.fragment_container, frag, PrefsFragment.FRAGMENT_TAG)
      }
    }
  }

  fun setupActionBar() {
    // Show the Up button in the action bar.
    getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onPreferenceStartFragment(
    caller: PreferenceFragmentCompat,
    pref: Preference,
  ): Boolean {
    getSupportFragmentManager().commit {
      val key = pref.getKey()
      val args = Bundle()
      args.putString(
        PreferenceFragmentCompat.ARG_PREFERENCE_ROOT,
        key,
      )
      val frag: BasePreferenceFragment? =
        when (key) {
          getString(R.string.pref_tag_about) -> AboutPreferenceFragment()
          else -> null
        }
      if (frag != null) {
        frag.setArguments(args)
        replace(R.id.fragment_container, frag, key)
      }
      addToBackStack(key)
    }
    return true
  }
}
