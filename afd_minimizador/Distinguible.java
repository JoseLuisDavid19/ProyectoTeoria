/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package afd_minimizador;

/**
 *
 * @author josel_0v10him
 */
public class Distinguible {
    public String Estado1;
    public String Estado2;
    public int value;
    public Distinguible (String estado1, String estado2){
        Estado1=estado1;
        Estado2=estado2;
        value=-2;
    }
    public void print(){
        System.out.println("Estado1: "+Estado1+" Estado2: "+Estado2+" Value: "+value);
    }
}
