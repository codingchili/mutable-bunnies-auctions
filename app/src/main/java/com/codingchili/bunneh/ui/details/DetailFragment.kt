package com.codingchili.bunneh.ui.details

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import kotlin.math.roundToInt

class DetailFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_details, container, false)

        activity!!.title = "Leafy Branch +5"

        val image = fragment.findViewById<ImageView>(R.id.item_image)

        val metrics = DisplayMetrics()
        activity!!.getWindowManager().getDefaultDisplay().getMetrics(metrics)

        val logicalDensity = metrics.density
        val px = (132.0 * logicalDensity).roundToInt()

        val source = BitmapFactory.decodeResource(getResources(), R.drawable.flaming_stick)
        val scaled = Bitmap.createScaledBitmap(source, px, px, false)

        image.setImageBitmap(scaled);

        return fragment
    }
}