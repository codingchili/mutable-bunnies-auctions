package com.codingchili.bunnies.ui.notifications

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.codingchili.banking.model.Notification
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.auction.AuctionFragment
import com.codingchili.bunnies.ui.auction.AuctionViewModel
import com.codingchili.bunnies.ui.transform.ServerResource
import com.trello.rxlifecycle4.kotlin.bindToLifecycle

/**
 * This fragment loads notifications from the server and displays them here.
 * Like the inventory and search fragment it could have used a viewmodel to store
 * state. this feature is likely to arrive in another update when notifications
 * can be streamed from the server.
 */
class NotificationsFragment : Fragment() {
    private val model by activityViewModels<AuctionViewModel>()
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
                            service.findById(notification.auctionId!!).bindToLifecycle(fragment)
                                .subscribe { response, e ->
                                    if (e == null) {
                                        this@NotificationsFragment.model.auction.value = response

                                        requireActivity().supportFragmentManager.beginTransaction()
                                            .add(R.id.root, AuctionFragment())
                                            .addToBackStack(AuctionFragment.TAG)
                                            .commit()
                                    } else {
                                        AppToast.show(context, e.message)
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
        service.notifications().bindToLifecycle(fragment).subscribe { response, e ->
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
                AppToast.show(context, e.message)
            }
        }

        return fragment
    }
}