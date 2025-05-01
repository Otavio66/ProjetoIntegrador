package com.example.pi3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class RegistroAdapter(context: Context, registros: List<Registro>) :
    ArrayAdapter<Registro>(context, 0, registros) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val registro = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_registro, parent, false)

        // Encontrar o TextView no layout e definir o nome do problema
        val nomeProblemaTextView = view.findViewById<TextView>(R.id.nomeProblemaTextView)
        nomeProblemaTextView.text = registro?.nomeProblema

        return view
    }
}
