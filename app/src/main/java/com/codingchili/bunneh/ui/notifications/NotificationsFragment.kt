package com.codingchili.bunneh.ui.notifications

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuctionService
import com.codingchili.bunneh.api.MockData
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.Notification
import com.codingchili.bunneh.ui.AppToast
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.transform.ServerResource

class NotificationsFragment : Fragment() {
    private val service = AuctionService.instance


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_notifications, container, false)
        val list = fragment.findViewById<ListView>(R.id.notification_list)
        val progress = fragment.findViewById<ProgressBar>(R.id.progress_search)

        val adapter =
            object : ArrayAdapter<Notification>(requireContext(), R.layout.item_notification) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val notification = getItem(position)!!
                    val row = convertView ?: inflater.inflate(
                        R.layout.item_notification,
                        parent,
                        false
                    )
                    val image = row.findViewById<ImageView>(R.id.item_preview)

                    if (notification.icon != null) {
                        ServerResource.icon(image, notification.icon!!)
                    }

                    if (notification.auctionId != null) {
                        row.setOnClickListener {
                            progress.visibility = View.VISIBLE
                            service.findById(notification.auctionId!!).subscribe { response, e ->
                                if (e == null) {
                                    requireActivity().supportFragmentManager.beginTransaction()
                                        .add(R.id.root, AuctionFragment().load(response, listOf()))
                                        .addToBackStack(AuctionFragment.TAG)
                                        .commit()
                                } else {
                                    AppToast.show(requireContext(), e.message!!)
                                }
                                progress.visibility = View.GONE
                            }
                        }
                    } else {
                        row.findViewById<View>(R.id.navigate_next).visibility = View.INVISIBLE
                        row.findViewById<View>(R.id.container)
                            .setBackgroundColor(context.getColor(R.color.card))
                    }

                    row.findViewById<TextView>(R.id.notification_text).text =
                        Html.fromHtml(notification.message, HtmlCompat.FROM_HTML_MODE_COMPACT)

                    return row
                }
            }
        list.adapter = adapter

        val noHitsContainer = fragment.findViewById<View>(R.id.no_hits_container)

        progress.visibility = View.VISIBLE
        service.notifications().subscribe { response, e ->
            progress.visibility = View.GONE
            if (e == null) {
                if (response.isEmpty()) {
                    noHitsContainer.visibility = View.VISIBLE
                } else {
                    noHitsContainer.visibility = View.GONE
                    adapter.clear()
                    adapter.addAll(response)
                }
            } else {
                AppToast.show(requireContext(), e.message!!)
            }
        }

        return fragment
    }
}