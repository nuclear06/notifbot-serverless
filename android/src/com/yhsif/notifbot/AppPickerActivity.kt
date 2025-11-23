package com.yhsif.notifbot

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppPickerActivity : AppCompatActivity(), View.OnClickListener {

  lateinit var adapter: AppPickerAdapter
  lateinit var allApps: List<PkgData>
  lateinit var searchBox: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_app_picker)

    setSupportActionBar(findViewById<Toolbar>(R.id.app_bar))
    getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

    searchBox = findViewById(R.id.search_box)
    adapter = AppPickerAdapter(mutableListOf(), this)
    
    findViewById<RecyclerView>(R.id.app_list).let { rv ->
      rv.setAdapter(adapter)
      rv.setLayoutManager(LinearLayoutManager(this))
    }

    loadInstalledApps()

    searchBox.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        filterApps(s.toString())
      }
      
      override fun afterTextChanged(s: Editable?) {}
    })
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.getItemId() == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  private fun loadInstalledApps() {
    val pm = getPackageManager()
    val existingPkgs = NotificationListener.getPkgSet(this)
    val defIcon = getDrawable(R.mipmap.default_icon)!!

    allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
      .filter { appInfo ->
        // Filter out system apps and already added apps
        (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && 
        !existingPkgs.contains(appInfo.packageName)
      }
      .map { appInfo ->
        createPkgData(pm, appInfo, defIcon)
      }
      .sortedBy { it.name.lowercase() }

    adapter.list = allApps.toMutableList()
    adapter.notifyDataSetChanged()
  }

  private fun filterApps(query: String) {
    val filtered = if (query.isEmpty()) {
      allApps
    } else {
      allApps.filter { 
        it.name.contains(query, ignoreCase = true) || 
        it.pkg.contains(query, ignoreCase = true)
      }
    }
    adapter.list = filtered.toMutableList()
    adapter.notifyDataSetChanged()
  }

  override fun onClick(v: View) {
    findViewById<RecyclerView>(R.id.app_list).let { rv ->
      val i = rv.getChildLayoutPosition(v)
      if (i >= 0 && i < adapter.list.size) {
        val data = adapter.list.get(i)
        addPackage(data.pkg)
      }
    }
  }

  private fun addPackage(pkg: String) {
    val pkgSet = NotificationListener.getPkgSet(this).toMutableSet()
    pkgSet.add(pkg)
    getSharedPreferences(MainActivity.PREF, 0).edit {
      putStringSet(MainActivity.KEY_PKGS, pkgSet)
    }
    
    val name = NotificationListener.getPackageName(this, pkg, false)
    MainActivity.showToast(this, getString(R.string.receiver_added_pkg, name))
    
    // Refresh the list to remove the added app
    loadInstalledApps()
  }

  private fun createPkgData(
    pm: PackageManager,
    appInfo: ApplicationInfo,
    defIcon: Drawable,
  ): PkgData {
    val name = pm.getApplicationLabel(appInfo).toString()
    val icon = try {
      pm.getApplicationIcon(appInfo)
    } catch (_: Exception) {
      defIcon
    }
    return PkgData(icon, name, appInfo.packageName)
  }
}
