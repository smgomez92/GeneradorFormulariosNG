package com.fitbank.web.simulado.procesos;

import org.apache.commons.lang.StringUtils;

import com.fitbank.menujson.ItemTransaccion;
import com.fitbank.menujson.MenuCompania;
import com.fitbank.menujson.MenuJSON;
import com.fitbank.menujson.MenuSubsistema;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import org.apache.commons.lang.math.RandomUtils;

@Handler(GeneralRequestTypes.MENU)
@RevisarSeguridad
public class CargaMenu implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        pedido.setTipoMenu(pedido.getValorRequestHttp("menu"));

        MenuJSON menuJSON = new MenuJSON("TEST");
        switch (pedido.getTipoMenu()) {
            case CIAS:
                menuJSON.getItems().add(new MenuCompania("Compañía 1", "1"));
                menuJSON.getItems().add(new MenuCompania("Compañía 2", "2"));
                menuJSON.getItems().add(new MenuCompania("Compañía 3", "3"));
                menuJSON.getItems().add(new MenuCompania("Compañía 4", "4"));
                menuJSON.getItems().add(new MenuCompania("Compañía 5", "5"));
                break;

            case TRANS:
                String cia = pedido.getTransporteDB().getCompany();

                for (int i = 0; i < 0; i++) {
                    MenuCompania menucia = new MenuCompania("Menú compañía " + cia, cia);
                    menuJSON.getItems().add(menucia);

                    // Crear menus
                    for (int jj = 0; jj < (6 + (int) (Math.random() * 6)); jj++) {
                        int caso = RandomUtils.nextInt();
                        switch (caso) {
                            case 1:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso));
                                break;
                            case 2:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1)));
                                break;
                            case 3:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1),
                                        jj + "0" + (caso + 2)));
                                break;
                            case 4:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1), jj + "0"
                                        + (caso + 2), jj + "0" + (caso + 3)));
                                break;
                            case 5:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1), jj + "0"
                                        + (caso + 2), jj + "0" + (caso
                                        + 3), jj + "0" + (caso + 4)));
                                break;
                            case 6:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1), jj + "0"
                                        + (caso + 2), jj + "0" + (caso + 3), jj
                                        + "0" + (caso + 4), jj + "0"
                                        + (caso + 5)));
                                break;
                            case 7:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1), jj + "0"
                                        + (caso + 2), jj + "0" + (caso + 3), jj
                                        + "0" + (caso + 4), jj + "0"
                                        + (caso + 5), jj + "0" + (caso + 6)));
                                break;

                            case 8:
                                // Crear un subsistema que no pertenece a ningun menu
                                menucia.getItems().add(crearSubsistema("0" + caso));
                                break;

                            case 9:
                                // Crear una transaccion que no pertenece a ningun menu
                                menucia.getItems().add(crearTransaccion("0" + caso, "0001"));
                                break;

                            default:
                                menucia.getItems().add(crearSubmenu(jj, jj + "0"
                                        + caso, jj + "0" + (caso + 1), jj + "0"
                                        + (caso + 2), jj + "0" + (caso + 3), jj
                                        + "0" + (caso + 4), jj + "0"
                                        + (caso + 5), jj + "0" + (caso + 6), jj
                                        + "0" + (caso + 7)));
                        }
                    }
                }

                break;
        }

        respuesta.setContenido(menuJSON);

        return respuesta;
    }

    private MenuJSON crearSubmenu(int n, String... subsistemas) {
        MenuJSON menu = new MenuJSON("Submenu " + n);

        for (String subsistema : subsistemas) {
            menu.getItems().add(crearSubsistema(subsistema));
        }

        return menu;
    }

    private MenuSubsistema crearSubsistema(String subsistema) {
        MenuSubsistema menu = new MenuSubsistema("Menu subsistema " + subsistema,
                subsistema);

        for (int i = 0; i < (3 + (int) (Math.random() * 6)); i++) {
            menu.getItems().add(crearTransaccion(subsistema, "00" + (i > 9 ? ""
                    : "0") + i));
        }

        return menu;
    }

    private ItemTransaccion crearTransaccion(String subsistema,
            String transaccion) {
        return new ItemTransaccion("Transaccion " + subsistema + ":"
                + transaccion + repetirLetras("X", 5, 1), subsistema,
                transaccion);
    }

    private String repetirLetras(String letra, int max, int veces) {
        String resultado = "";
        for (int i = 0; i < veces; i++) {
            resultado += " " + StringUtils.repeat(letra, (int) (Math.random()
                    * max));
        }

        return resultado;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido(new MenuJSON("ERROR").toString());
    }

}
