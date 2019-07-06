package com.andes.vaccontrol.configuration;

public interface AppServices {

    String URL_PROYECTO = "http://172.22.3.169/vacbservices";


    String URL_LOGIN = URL_PROYECTO+"/services/login.php";
    String URL_REGISTER = URL_PROYECTO+"/services/register.php";
    String URL_CONFIRM= URL_PROYECTO+"/services/confirm.php";


    String URL_GANADERO = URL_PROYECTO+"/services/ganadero.php";
    String URL_ESTABLOS = URL_PROYECTO+"/services/establos.php";
    String URL_ADD_ESTABLO = URL_PROYECTO+"/services/add_establo.php";
    String URL_LISTA_GANADO = URL_PROYECTO+"/services/lista_ganado.php";
    String URL_LISTA_GANADO_ELIMINADO = URL_PROYECTO+"/services/lista_ganado_eliminado.php";
    String URL_ADD_GANADO = URL_PROYECTO+"/services/add_ganado.php";

    String URL_VER_GANADO = URL_PROYECTO+"/services/mi_ganado.php";
    String URL_VER_GANADO_REPRODUCCION = URL_PROYECTO+"/services/ganados_reproduccion.php";
    String URL_VER_GANADO_MONITORERO = URL_PROYECTO+"/services/ganados_monitoreo.php";
    String URL_VER_GANADO_PRODUCCION = URL_PROYECTO+"/services/ganados_produccion.php";

    String URL_ADD_FOTOS = URL_PROYECTO+"/services/add_sesion_fotos.php";
    String URL_SUBIR_FOTO = URL_PROYECTO+"/services/subir_foto.php";
    String URL_SUBIR_FOTO_HD = URL_PROYECTO+"/services/subir_foto_hd.php";

    String URL_ADD_ESTADO_REPRODUCCION = URL_PROYECTO+"/services/add_estado_reproduccion.php";
    String URL_ADD_PRODUCCION = URL_PROYECTO+"/services/add_produccion.php";

    String URL_GANADO_SACA = URL_PROYECTO+"/services/ganado_saca.php";





}
