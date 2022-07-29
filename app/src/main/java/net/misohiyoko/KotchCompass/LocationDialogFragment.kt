package net.misohiyoko.KotchCompass

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class LocationDialogFragment : DialogFragment(){
    interface LocationDialogListener{
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)

    }
    internal lateinit var listener: LocationDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as LocationDialogListener

        }catch (e: java.lang.ClassCastException)
        {
            throw java.lang.ClassCastException(context.toString()+"must Impl listener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.background_geolocation)
            .setMessage(R.string.background_geolocation_nav)
            .setPositiveButton(R.string.accept) { dialog, id ->
                listener.onDialogPositiveClick(this)
            }
            .setNegativeButton(R.string.cancel){dialog, id ->
                listener.onDialogNegativeClick(this)
            }
        return builder.create()
    }
}