package com.andes.vaccontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andes.vaccontrol.R;

import java.util.ArrayList;

public class AdaptadorProduccion extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context contexto;
    String tipo;
    ArrayList<ArrayList<String>> datos;

    public AdaptadorProduccion(Context context, String tipo, ArrayList<ArrayList<String>> datos) {
        this.contexto = context;
        this.tipo = tipo;
        this.datos = datos;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return datos.size(); }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View vista = inflater.inflate(R.layout.element_list_produccion, null);


        TextView titulo_produccion = (TextView) vista.findViewById(R.id.tv_titulo_produccion);
        TextView litros_produccion = (TextView) vista.findViewById(R.id.tv_litros_produccion);
        TextView estado_produccion = (TextView) vista.findViewById(R.id.tv_estado_produccion);
        TextView fecha_produccion = (TextView) vista.findViewById(R.id.tv_fecha_produccion);
        TextView hora_produccion = (TextView) vista.findViewById(R.id.tv_hora_produccion);
        TextView solidos_produccion = (TextView) vista.findViewById(R.id.tv_solidos_produccion);
        TextView csomaticas_produccion = (TextView) vista.findViewById(R.id.tv_csomaticas_produccion);

        titulo_produccion.setText(tipo + " " + (datos.size()-i));
        litros_produccion.setText(""+datos.get(i).get(1));
        estado_produccion.setText(""+datos.get(i).get(4));
        fecha_produccion.setText(""+datos.get(i).get(5));
        hora_produccion.setText(""+datos.get(i).get(6));
        solidos_produccion.setText(""+datos.get(i).get(2));
        csomaticas_produccion.setText(""+datos.get(i).get(3));

        return vista;
    }
}
