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
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francisco Riquelme <francisco.riquelme@konecta.com.py at Konecta SA>
 */
public class Cliente {

    public static void main(String[] args) {

        //puerto del servidor
        final int PUERTO_SERVIDOR = 5000;
        //buffer donde se almacenara los mensajes
        byte[] buffer = null;

        try {
            //Obtengo la localizacion de localhost
            InetAddress direccionServidor = InetAddress.getByName("localhost");

            //Creo el socket de UDP
            DatagramSocket socketUDP = new DatagramSocket();
            int idEstacion = 1;
            int idOperacion = 0; 
            while (idOperacion!=3) {
                Scanner lectura = new Scanner(System.in);
                //Lo envio con send
                System.out.println("=========== Seleccionar un tipo de operacion ===========");
                System.out.println("1) Recibir datos de sensores ");
                System.out.println("2) Consultar la temperatura en una ciudad ");
                System.out.println("3) Cerrar ");
                idOperacion = Integer.parseInt(lectura.next());
                System.out.println("=========================================================");
                Data data = new Data();
                switch (idOperacion) {
                    case 1:
                        System.out.println("Ingresar ciudad: ");
                        data.setCiudad(lectura.next());
                        System.out.println("Ingresar porcentaje de humedad: ");
                        data.setPorcentajeHumedad(lectura.next());
                        System.out.println("Ingresar temperatura: ");
                        data.setTemperatura(lectura.next());
                        System.out.println("Ingresar velocidad viento: ");
                        data.setVelocidadViento(lectura.next());
                        System.out.println("Ingresar fecha: ");
                        data.setFecha(lectura.next());
                        System.out.println("Ingresar hora: ");
                        data.setHora(lectura.next());
                        break;
                    case 2:
                        System.out.println("Ingresar ciudad: ");
                        data.setCiudad(lectura.next());
                        break;
                    case 3:
                        socketUDP.close();
                        break;
                }
                if (idOperacion == 1 || idOperacion == 2) {
                    data.setIdEstacion(idEstacion);
                    Map request = new HashMap();
                    request.put("idOperacion", idOperacion);
                    request.put("data", data);

                    String mensaje = new Gson().toJson(request);
                    buffer = mensaje.getBytes();
                    //Creo un datagrama
                    DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
                    socketUDP.send(pregunta);
                    buffer = new byte[2048];
                    //Preparo la respuesta
                    DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                    //Recibo la respuesta
                    socketUDP.receive(peticion);
                    mensaje = new String(peticion.getData());
                    int index = 0;
                    for (int i = 0; i < mensaje.length(); i++) {
                        int aa = mensaje.charAt(i);
                        if (aa == 0) {
                            index = i;
                            break;
                        }
                    }
                    String nuevoMensaje = mensaje.substring(0, index);
                    System.out.println(nuevoMensaje);
                    //cierro el socket
                    idEstacion++;
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
