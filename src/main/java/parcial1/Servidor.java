/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parcial1;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francisco Riquelme <francisco.riquelme@konecta.com.py at Konecta SA>
 */
public class Servidor {

    public static void main(String[] args) {

        final int PUERTO = 5000;
        byte[] buffer = new byte[2048];
        List<Data> datosMeteo = new ArrayList<Data>();

        try {
            System.out.println("Iniciado el servidor UDP");
            //Creacion del socket
            DatagramSocket socketUDP = new DatagramSocket(PUERTO);
            //Siempre atendera peticiones
            while (true) {
                buffer = new byte[2048];
                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

                //Recibo el datagrama
                socketUDP.receive(peticion);

                String mensaje = new String(peticion.getData());
                int index = 0;
                for (int i = 0; i < mensaje.length(); i++) {
                    int aa = mensaje.charAt(i);
                    if (aa == 0) {
                        index = i;
                        break;
                    }
                }
                String nuevoMensaje = mensaje.substring(0, index);
                Map response = new Gson().fromJson(nuevoMensaje, Map.class);
                int idOperacion = Double.valueOf(response.get("idOperacion").toString()).intValue();
                Map responseData = (Map) response.get("data");
                String jsonData = new Gson().toJson(responseData);
                Data data = new Gson().fromJson(jsonData, Data.class);
                String mensajeRespuesta = "";
                if (idOperacion == 1) {
                    datosMeteo.add(data);
                    mensajeRespuesta = "Los datos fueron Recibidos correctamente";
                } else if (idOperacion == 2) {
                    String ciudad = data.getCiudad();
                    List<Data> ciudadesDato = new ArrayList<>();
                    for(Data x : datosMeteo){
                        if(x.getCiudad().equals(ciudad)){
                            ciudadesDato.add(x);
                        }
                    }
                    if(ciudadesDato.size() > 0){
                        mensajeRespuesta =new Gson().toJson(ciudadesDato);
                    }else{
                     mensajeRespuesta = "Ciudad no encontrada";
                    }
                }
                int puertoCliente = peticion.getPort();
                InetAddress direccion = peticion.getAddress();
                buffer = mensajeRespuesta.getBytes();
                //creo el datagrama
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                //Envio la información
                System.out.println("Envio la informacion del cliente");
                socketUDP.send(respuesta);
            }
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
