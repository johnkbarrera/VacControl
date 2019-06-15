package com.andes.vaccontrol.configuration;

public interface AppServices {

    String URL_PROYECTO = "http://10.66.49.59/vacbservices";


    String URL_GANADERO = URL_PROYECTO+"/services/ganadero.php";
    String URL_ESTABLOS = URL_PROYECTO+"/services/establos.php";
    String URL_ADD_ESTABLO = URL_PROYECTO+"/services/add_establo.php";
    String URL_GANADOS = URL_PROYECTO+"/services/ganados.php";
    String URL_ADD_GANADO = URL_PROYECTO+"/services/add_ganado.php";

    String URL_VER_GANADO = URL_PROYECTO+"/services/mi_ganado.php";


}
