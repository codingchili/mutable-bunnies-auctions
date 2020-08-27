package com.codingchili.bunneh.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunneh.R
import com.google.android.material.button.MaterialButton
import java.util.function.Consumer

class NavigableTreeDialog(
    var title: String,
    var tree: List<NavigableTree>,
    var listener: Consumer<NavigableTree>? = null
) : DialogFragment() {

    private fun adapter(inflater: LayoutInflater, view: View): ArrayAdapter<NavigableTree> {
        return object : ArrayAdapter<NavigableTree>(requireContext(), R.layout.item_navigable) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val node = getItem(position)!!
                val row = convertView ?: inflater.inflate(
                    R.layout.item_navigable,
                    parent,
                    false
                )
                row.findViewById<TextView>(R.id.item_text).text = node.name
                row.setOnClickListener {
                    view.visibility = View.VISIBLE

                    if (!node.isLeaf()) {
                        this.clear()
                        refresh(this, node.next)
                    } else {
                        listener?.accept(node)
                        dismiss()
                    }
                }
                return row
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_navigable_tree, container, false)
        val up = view.findViewById<RelativeLayout>(R.id.navigate_up)
        val list = view.findViewById<ListView>(R.id.list_view)
        val adapter = adapter(inflater, up)

        view.findViewById<TextView>(R.id.dialog_title).text = title
        view.findViewById<MaterialButton>(R.id.close_button)
            .setOnClickListener { dialog?.dismiss() }

        list.adapter = adapter
        refresh(adapter, tree)

        up.visibility = View.GONE
        up.setOnClickListener {
            up.visibility = View.GONE
            refresh(adapter, tree)
        }
        return view
    }

    private fun refresh(adapter: ArrayAdapter<NavigableTree>, items: Collection<NavigableTree>) {
        adapter.clear()
        adapter.addAll(items.sortedBy { it.name })
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
    }

}