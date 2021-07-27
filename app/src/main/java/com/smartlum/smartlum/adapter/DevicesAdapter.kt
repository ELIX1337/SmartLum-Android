package com.smartlum.smartlum.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.smartlum.smartlum.R
import com.smartlum.smartlum.ui.ScannerFragment
import com.smartlum.smartlum.viewmodels.DevicesLiveData

class DevicesAdapter(
    scannerFragment: ScannerFragment,
    devicesLiveData: DevicesLiveData
) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {
    private var devices: List<DiscoveredBluetoothDevice?>? = null
    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(device: DiscoveredBluetoothDevice)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutView = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return ViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices?.get(position)
        val deviceName = device?.name
        if (!TextUtils.isEmpty(deviceName)) holder.deviceName.text =
            deviceName else holder.deviceName.setText(R.string.unknown_device)
        val rssiPercent = (100.0f * (127.0f + (device?.rssi ?: 0)) / (127.0f + 20.0f)).toInt()
        holder.rssi.setImageLevel(rssiPercent)
    }

    override fun getItemId(position: Int): Long {
        return devices?.get(position).hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return devices?.size ?: 0
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceName: TextView = view.findViewById(R.id.device_name)
        val rssi: ImageView = view.findViewById(R.id.rssi)

        init {
            view.findViewById<View>(R.id.device_container).setOnClickListener {
                devices?.get(adapterPosition)?.let { it1 -> onItemClickListener?.onItemClick(it1) }
            }
        }
    }

    init {
        setHasStableIds(true)
        devicesLiveData.observe(
            scannerFragment.requireActivity(),
            { newDevices: List<DiscoveredBluetoothDevice?>? ->
                devices = newDevices
                val result = DiffUtil.calculateDiff(
                    DeviceDiffCallback(devices as List<DiscoveredBluetoothDevice>?,
                        newDevices as List<DiscoveredBluetoothDevice>?
                    ), false
                )
                result.dispatchUpdatesTo(this)
            })
    }
}